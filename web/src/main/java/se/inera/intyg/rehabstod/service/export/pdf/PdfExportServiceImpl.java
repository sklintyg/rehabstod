/**
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
 *
 * This file is part of rehabstod (https://github.com/sklintyg/rehabstod).
 *
 * rehabstod is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * rehabstod is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.intyg.rehabstod.service.export.pdf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.common.util.StringUtil;
import se.inera.intyg.rehabstod.service.Urval;
import se.inera.intyg.rehabstod.service.export.BaseExportService;
import se.inera.intyg.rehabstod.web.controller.api.dto.PrintSjukfallRequest;
import se.inera.intyg.rehabstod.web.model.InternalSjukfall;
import se.inera.intyg.rehabstod.web.model.Sjukfall;
import se.inera.intyg.rehabstod.web.model.Sortering;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

/**
 * Created by marced on 24/02/16.
 */

// Because of a lot of tinkering with margins and absolute positioning of elements we actually DO use a lot of magic
// numbers!
// CHECKSTYLE:OFF MagicNumber
@Service
public class PdfExportServiceImpl extends BaseExportService implements PdfExportService {

    private static final String LOGO_PATH = "classpath:pdf-assets/rehab_pdf_logo.png";

    private PathMatchingResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
    private static final BaseColor TABLE_HEADER_BASE_COLOR = new BaseColor(70, 87, 97);
    private static final BaseColor TABLE_EVEN_ROW_COLOR = BaseColor.WHITE;
    private static final BaseColor TABLE_ODD_ROW_COLOR = new BaseColor(220, 220, 220);

    @Override
    public byte[] export(List<InternalSjukfall> sjukfallList, PrintSjukfallRequest printSjukfallRequest, RehabstodUser user, int total)
            throws DocumentException, IOException {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        Document document = new Document();

        document.setPageSize(PageSize.A4);
        document.setMargins(20, 20, 70, 20);

        PdfWriter writer = PdfWriter.getInstance(document, bos);
        // Add handlers for page events

        writer.setPageEvent(new HeaderEventHandler(
                Image.getInstance(IOUtils.toByteArray(resourcePatternResolver.getResource(LOGO_PATH).getInputStream())),
                user.getNamn()));
        writer.setPageEvent(new PageNumberingEventHandler());

        document.open();

        // Add the front page with meta info
        document.add(createFrontPage(printSjukfallRequest, user, sjukfallList.size(), total));

        // Switch to landscape mode
        document.setPageSize(PageSize.A4.rotate());
        document.newPage();

        // Add table with all sjukfall (could span several pages)
        document.add(createSjukfallTable(sjukfallList, user.getUrval()));

        // Finish off by closing the document (will invoke the event handlers)
        document.close();

        return bos.toByteArray();

    }

    private Element createFrontPage(PrintSjukfallRequest printRequest, RehabstodUser user, int showing, int total) {
        Paragraph sida = new Paragraph();

        sida.add(getUrvalDesc(user));
        sida.add(new Chunk(new LineSeparator()));
        sida.add(getFilterDesc(printRequest, user));
        sida.add(getSjukfallsDefDesc(printRequest));
        sida.add(getSorteringDesc(printRequest.getSortering()));
        sida.add(getAntalDesc(user.getUrval(), showing, total));

        return sida;

    }

    private Paragraph getUrvalDesc(RehabstodUser user) {
        Paragraph urvalsRubrik = new Paragraph();
        String unitContextString = "\"" + user.getValdVardgivare().getNamn() + "-" + user.getValdVardenhet().getNamn() + "\"";
        if (Urval.ISSUED_BY_ME == user.getUrval()) {
            urvalsRubrik.add(new Paragraph("Mina pågående sjukfall", FRONTPAGE_H1));
            urvalsRubrik.add(new Paragraph("- På enheten " + unitContextString, FRONTPAGE_H2));
        } else {
            urvalsRubrik.add(new Paragraph("Alla sjukfall", FRONTPAGE_H1));
            urvalsRubrik.add(new Paragraph("- Samtliga pågående fall på enheten " + unitContextString, FRONTPAGE_H2));
        }
        return urvalsRubrik;
    }

    private Element getFilterDesc(PrintSjukfallRequest printRequest, RehabstodUser user) {

        // Diagnoser
        Paragraph valdaDiagnoser = new Paragraph("Valda diagnoser", FRONTPAGE_H3);
        com.itextpdf.text.List diagnosLista = new com.itextpdf.text.List(com.itextpdf.text.List.UNORDERED);
        if (printRequest.getDiagnosGrupper() != null) {
            printRequest.getDiagnosGrupper().forEach(dg -> diagnosLista.add(new ListItem(getDiagnosKapitelDisplayValue(dg), FRONTPAGE_NORMAL)));
        } else {
            diagnosLista.add(new ListItem("Alla", FRONTPAGE_NORMAL));
        }
        valdaDiagnoser.add(diagnosLista);

        // Lakare
        Paragraph valdaLakare = new Paragraph("Valda läkare", FRONTPAGE_H3);
        com.itextpdf.text.List lakarLista = new com.itextpdf.text.List(com.itextpdf.text.List.UNORDERED);
        if (printRequest.getLakare() != null) {

            printRequest.getLakare().forEach(dg -> lakarLista.add(new ListItem(dg, FRONTPAGE_NORMAL)));
        } else {
            lakarLista.add(new ListItem(user.getUrval() == Urval.ISSUED_BY_ME ? user.getNamn() : "Alla", FRONTPAGE_NORMAL));
        }
        valdaLakare.add(lakarLista);

        // Sjukskrivningslangd
        Paragraph valdSjukskrivninglangd = new Paragraph("Vald sjukskrivningslängd", FRONTPAGE_H3);
        Paragraph sjukskrivningslangdVarden = new Paragraph();
        sjukskrivningslangdVarden.add(new Chunk("Mellan ", FRONTPAGE_NORMAL));
        sjukskrivningslangdVarden.add(new Chunk(String.valueOf(printRequest.getLangdIntervall().getMin()), FRONTPAGE_NORMAL_BOLD));
        sjukskrivningslangdVarden.add(new Chunk(" och ", FRONTPAGE_NORMAL));
        sjukskrivningslangdVarden.add(new Chunk(String.valueOf(printRequest.getLangdIntervall().getMax()), FRONTPAGE_NORMAL_BOLD));
        sjukskrivningslangdVarden.add(new Chunk(" dagar.", FRONTPAGE_NORMAL));
        valdSjukskrivninglangd.add(sjukskrivningslangdVarden);

        // Fritext
        Paragraph valdFritext = new Paragraph("Fritextfilter", FRONTPAGE_H3);
        valdFritext.add(new Paragraph(StringUtil.isNullOrEmpty(printRequest.getFritext()) ? "-" : printRequest.getFritext(), FRONTPAGE_NORMAL));

        // Lagg ihop undergrupperna till filter
        Paragraph filter = new Paragraph("Valda filter", FRONTPAGE_H2);
        filter.add(valdaDiagnoser);
        filter.add(valdaLakare);
        filter.add(valdSjukskrivninglangd);
        filter.add(valdFritext);

        return filter;
    }

    private String getDiagnosKapitelDisplayValue(String diagnosKapitel) {
        StringBuilder b = new StringBuilder(diagnosKapitel);
        if (b.length() > 0) {
            b.append(": ");
        }
        b.append(diagnosKapitelService.getDiagnosKapitel(diagnosKapitel).getName());
        return b.toString();
    }

    private Element getSjukfallsDefDesc(PrintSjukfallRequest printRequest) {
        Paragraph def = new Paragraph();
        def.add(new Paragraph("Sjukfallsinställning", FRONTPAGE_H2));
        def.add(new Paragraph("Sjukfallsdefinition", FRONTPAGE_H3));
        def.add(new Phrase("Maxantal dagar uppehåll mellan intyg: ", FRONTPAGE_NORMAL));
        def.add(new Phrase(printRequest.getMaxIntygsGlapp() + " dagar", FRONTPAGE_NORMAL_BOLD));
        return def;
    }

    private Element getSorteringDesc(Sortering sortering) {
        Paragraph def = new Paragraph("Vald sortering på tabellen", FRONTPAGE_H2);

        Paragraph kolumn = new Paragraph();
        kolumn.add(new Chunk("Kolumn: ", FRONTPAGE_NORMAL_BOLD));

        Paragraph riktning = new Paragraph();
        riktning.add(new Chunk("Riktning: ", FRONTPAGE_NORMAL_BOLD));

        if (sortering == null || StringUtil.isNullOrEmpty(sortering.getKolumn())) {
            kolumn.add(new Chunk("Ingen", FRONTPAGE_NORMAL));
            riktning.add(new Chunk("-", FRONTPAGE_NORMAL));
        } else {
            kolumn.add(new Phrase(sortering.getKolumn(), FRONTPAGE_NORMAL));
            riktning.add(new Chunk(sortering.getOrder(), FRONTPAGE_NORMAL));
        }
        def.add(kolumn);
        def.add(riktning);
        return def;

    }

    private Element getAntalDesc(Urval urval, int showing, int total) {
        Paragraph def = new Paragraph("Visar antal pågående sjukfall", FRONTPAGE_H2);

        Paragraph kolumn = new Paragraph();
        kolumn.add(new Chunk("Exporten visar: ", FRONTPAGE_NORMAL_BOLD));
        kolumn.add(new Phrase(String.valueOf(showing), FRONTPAGE_NORMAL));

        Paragraph riktning = new Paragraph();
        riktning.add(new Chunk(urval == Urval.ISSUED_BY_ME ? "Totalt: " : "Totalt på enheten: ", FRONTPAGE_NORMAL_BOLD));
        riktning.add(new Chunk(String.valueOf(total), FRONTPAGE_NORMAL));

        def.add(kolumn);
        def.add(riktning);
        return def;

    }

    private PdfPTable createSjukfallTable(List<InternalSjukfall> sjukfallList, Urval urval) throws DocumentException {

        PdfPTable table;

        // Setup column widths (relative to each other)
        if (Urval.ALL.equals(urval)) {
            table = new PdfPTable(new float[] { 0.8f, 2.5f, 3, 1, 2, 1.5f, 1.5f, 2, 2, 2 });
        } else {
            table = new PdfPTable(new float[] { 0.8f, 2.5f, 3, 1, 2, 1.5f, 1.5f, 2, 2 });
        }

        table.setWidthPercentage(100.0f);

        table.getDefaultCell().setBackgroundColor(TABLE_HEADER_BASE_COLOR);
        table.getDefaultCell().setBorderColor(TABLE_HEADER_BASE_COLOR);
        table.getDefaultCell().setNoWrap(true);
        table.getDefaultCell().setPadding(3f);
        table.getDefaultCell().setPaddingLeft(2f);

        addCell(table, "#", TABLE_HEADER_FONT);
        addCell(table, "Personnummer", TABLE_HEADER_FONT);
        addCell(table, "Namn", TABLE_HEADER_FONT);
        addCell(table, "Kön", TABLE_HEADER_FONT);
        addCell(table, "Nuvarande diagnos", TABLE_HEADER_FONT);
        addCell(table, "Startdatum", TABLE_HEADER_FONT);
        addCell(table, "Slutdatum", TABLE_HEADER_FONT);
        addCell(table, "Sjukskrivningslängd", TABLE_HEADER_FONT);
        addCell(table, "Sjukskrivningsgrad", TABLE_HEADER_FONT);
        if (Urval.ALL.equals(urval)) {
            addCell(table, "Nuvarande läkare", TABLE_HEADER_FONT);
        }

        // Set cell styles for the non-header cells following hereafter
        table.getDefaultCell().setBackgroundColor(BaseColor.WHITE);
        table.getDefaultCell().setBorderColor(BaseColor.LIGHT_GRAY);
        table.getDefaultCell().setNoWrap(false);
        table.getDefaultCell().setPadding(2f);

        table.setHeaderRows(1);
        int rowNumber = 1;
        for (InternalSjukfall is : sjukfallList) {
            if (rowNumber % 2 == 0) {
                table.getDefaultCell().setBackgroundColor(TABLE_EVEN_ROW_COLOR);
            } else {
                table.getDefaultCell().setBackgroundColor(TABLE_ODD_ROW_COLOR);
            }
            Sjukfall s = is.getSjukfall();

            addCell(table, String.valueOf(rowNumber));
            addCell(table, getPersonnummerColumn(s));
            addCell(table, s.getPatient().getNamn());
            addCell(table, buildKonName(s.getPatient().getKon().name()));
            addCell(table, s.getDiagnos().getIntygsVarde());

            addCell(table, s.getStart() != null ? ISODateTimeFormat.yearMonthDay().print(s.getStart()) : "?");
            addCell(table, s.getSlut() != null ? ISODateTimeFormat.yearMonthDay().print(s.getSlut()) : "?");
            addCell(table, getlangdText(s));
            addCell(table, getGrader(s));
            if (Urval.ALL.equals(urval)) {
                addCell(table, s.getLakare().getNamn());
            }
            rowNumber++;
        }

        return table;

    }

    private void addCell(PdfPTable table, Phrase p) {
        table.addCell(p);
    }

    private void addCell(PdfPTable table, String s) {
        addCell(table, s, TABLE_CELL_NORMAL);
    }

    private void addCell(PdfPTable table, String s, Font font) {
        table.addCell(new Phrase(s, font));
    }

    private Phrase getGrader(Sjukfall s) {
        Phrase grader = new Phrase();
        for (Integer grad : s.getGrader()) {
            if (grad == s.getAktivGrad()) {
                grader.add(new Chunk(grad.toString() + "% ", TABLE_CELL_BOLD));
            } else {
                grader.add(new Chunk(grad.toString() + "% ", TABLE_CELL_NORMAL));
            }
        }
        return grader;
    }

    private Phrase getPersonnummerColumn(Sjukfall s) {
        Phrase p = new Phrase();
        p.add(new Chunk(s.getPatient().getId() != null ? s.getPatient().getId() : "", TABLE_CELL_NORMAL));
        p.add(new Chunk(String.format(" (%d år)", s.getPatient().getAlder()), TABLE_CELL_BOLD));
        return p;
    }

    private Phrase getlangdText(Sjukfall s) {
        Phrase p = new Phrase();
        p.add(new Chunk(String.format("%d dagar", s.getDagar()), TABLE_CELL_NORMAL));
        p.add(new Chunk(String.format(" (%d intyg)", s.getIntyg()), TABLE_CELL_SMALL));
        return p;
    }

}
