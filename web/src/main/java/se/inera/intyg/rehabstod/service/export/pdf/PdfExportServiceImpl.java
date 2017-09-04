/**
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;
import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.common.util.StringUtil;
import se.inera.intyg.rehabstod.common.util.YearMonthDateFormatter;
import se.inera.intyg.rehabstod.service.Urval;
import se.inera.intyg.rehabstod.service.export.BaseExportService;
import se.inera.intyg.rehabstod.web.controller.api.dto.PrintSjukfallRequest;
import se.inera.intyg.rehabstod.web.model.SjukfallEnhetRS;
import se.inera.intyg.rehabstod.web.model.Sortering;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * @author marced on 24/02/16.
 */

// Because of a lot of tinkering with margins and absolute positioning of elements we actually DO use a lot of magic
// numbers!
// CHECKSTYLE:OFF MagicNumber
@Service
public class PdfExportServiceImpl extends BaseExportService implements PdfExportService {

    private static final BaseColor TABLE_HEADER_BASE_COLOR = new BaseColor(70, 87, 97);
    private static final BaseColor TABLE_EVEN_ROW_COLOR = BaseColor.WHITE;
    private static final BaseColor TABLE_ODD_ROW_COLOR = new BaseColor(220, 220, 220);

    private static final String LOGO_PATH = "classpath:pdf-assets/rehab_pdf_logo.png";
    private static final String UNICODE_CAPABLE_FONT_PATH = "/pdf-assets/FreeSans.ttf";

    private PathMatchingResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    private Font unicodeCapableFont;

    @Override
    public byte[] export(List<SjukfallEnhetRS> sjukfallList, PrintSjukfallRequest printSjukfallRequest, RehabstodUser user, int total) {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            unicodeCapableFont = new Font(BaseFont.createFont(UNICODE_CAPABLE_FONT_PATH, BaseFont.IDENTITY_H, BaseFont.EMBEDDED), 9,
                    Font.NORMAL);

            Document document = new Document();
            document.setPageSize(PageSize.A4);
            document.setMargins(20, 20, 60, 20);

            PdfWriter writer = PdfWriter.getInstance(document, bos);
            // Add handlers for page events

            writer.setPageEvent(new HeaderEventHandler(
                    Image.getInstance(IOUtils.toByteArray(resourcePatternResolver.getResource(LOGO_PATH).getInputStream())),
                    user.getNamn(), user.getValdVardenhet().getNamn()));
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

        } catch (DocumentException | IOException | RuntimeException e) {
            throw new PdfExportServiceException("Failed to create PDF export!", e);
        }

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

        if (Urval.ISSUED_BY_ME == user.getUrval()) {
            urvalsRubrik.add(new Paragraph(MINA_PAGAENDE_SJUKFALL, PdfExportConstants.FRONTPAGE_H1));
            urvalsRubrik.add(new Paragraph(PA_ENHETEN, PdfExportConstants.FRONTPAGE_H2));
        } else {
            urvalsRubrik.add(new Paragraph(ALLA_SJUKFALL, PdfExportConstants.FRONTPAGE_H1));
            urvalsRubrik.add(new Paragraph(SAMTLIGA_PAGAENDE_FALL_PA_ENHETEN, PdfExportConstants.FRONTPAGE_H2));
        }
        return urvalsRubrik;
    }

    private Element getFilterDesc(PrintSjukfallRequest printRequest, RehabstodUser user) {

        // Diagnoser
        Paragraph valdaDiagnoser = new Paragraph(FILTER_TITLE_VALDA_DIAGNOSER, PdfExportConstants.FRONTPAGE_H3);
        com.itextpdf.text.List diagnosLista = new com.itextpdf.text.List(com.itextpdf.text.List.UNORDERED);
        if (printRequest.getDiagnosGrupper() != null) {
            printRequest.getDiagnosGrupper()
                    .forEach(dg -> diagnosLista.add(new ListItem(getDiagnosKapitelDisplayValue(dg), PdfExportConstants.FRONTPAGE_NORMAL)));
        } else {
            diagnosLista.add(new ListItem(SELECTION_VALUE_ALLA, PdfExportConstants.FRONTPAGE_NORMAL));
        }
        valdaDiagnoser.add(diagnosLista);

        // Lakare
        Paragraph valdaLakare = new Paragraph(FILTER_TITLE_VALDA_LAKARE, PdfExportConstants.FRONTPAGE_H3);
        com.itextpdf.text.List lakarLista = new com.itextpdf.text.List(com.itextpdf.text.List.UNORDERED);
        if (printRequest.getLakare() != null) {

            printRequest.getLakare().forEach(dg -> lakarLista.add(new ListItem(dg, PdfExportConstants.FRONTPAGE_NORMAL)));
        } else {
            lakarLista.add(new ListItem(user.getUrval() == Urval.ISSUED_BY_ME ? user.getNamn() : SELECTION_VALUE_ALLA,
                    PdfExportConstants.FRONTPAGE_NORMAL));
        }
        valdaLakare.add(lakarLista);

        // Sjukskrivningslangd
        Paragraph valdSjukskrivninglangd = new Paragraph(FILTER_TITLE_VALD_SJUKSKRIVNINGSLANGD, PdfExportConstants.FRONTPAGE_H3);
        Paragraph sjukskrivningslangdVarden = new Paragraph();
        sjukskrivningslangdVarden.add(new Chunk("Mellan ", PdfExportConstants.FRONTPAGE_NORMAL));
        sjukskrivningslangdVarden
                .add(new Chunk(String.valueOf(printRequest.getLangdIntervall().getMin()), PdfExportConstants.FRONTPAGE_NORMAL_BOLD));
        sjukskrivningslangdVarden.add(new Chunk(" och ", PdfExportConstants.FRONTPAGE_NORMAL));
        sjukskrivningslangdVarden
                .add(new Chunk(String.valueOf(printRequest.getLangdIntervall().getMax()), PdfExportConstants.FRONTPAGE_NORMAL_BOLD));
        sjukskrivningslangdVarden.add(new Chunk(" dagar.", PdfExportConstants.FRONTPAGE_NORMAL));
        valdSjukskrivninglangd.add(sjukskrivningslangdVarden);

        // Fritext
        Paragraph valdFritext = new Paragraph(FILTER_TITLE_FRITEXTFILTER, PdfExportConstants.FRONTPAGE_H3);
        valdFritext.add(new Paragraph(StringUtil.isNullOrEmpty(printRequest.getFritext()) ? "-" : printRequest.getFritext(),
                PdfExportConstants.FRONTPAGE_NORMAL));

        // Lagg ihop undergrupperna till filter
        Paragraph filter = new Paragraph(VALDA_FILTER, PdfExportConstants.FRONTPAGE_H2);
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
        def.add(new Paragraph(H2_SJUKFALLSINSTALLNING, PdfExportConstants.FRONTPAGE_H2));
        def.add(new Phrase(MAXANTAL_DAGAR_UPPEHALL_MELLAN_INTYG, PdfExportConstants.FRONTPAGE_NORMAL));
        def.add(new Phrase(printRequest.getMaxIntygsGlapp() + " dagar", PdfExportConstants.FRONTPAGE_NORMAL_BOLD));
        return def;
    }

    private Element getSorteringDesc(Sortering sortering) {
        Paragraph def = new Paragraph(VALD_SORTERING_PA_TABELLEN, PdfExportConstants.FRONTPAGE_H2);

        Paragraph kolumn = new Paragraph();
        kolumn.add(new Chunk(SORTERING_KOLUMN, PdfExportConstants.FRONTPAGE_NORMAL_BOLD));

        Paragraph riktning = new Paragraph();
        riktning.add(new Chunk(SORTERING_RIKTNING, PdfExportConstants.FRONTPAGE_NORMAL_BOLD));

        if (sortering == null || StringUtil.isNullOrEmpty(sortering.getKolumn())) {
            kolumn.add(new Chunk(SORTERING_INGEN, PdfExportConstants.FRONTPAGE_NORMAL));
            riktning.add(new Chunk("-", PdfExportConstants.FRONTPAGE_NORMAL));
        } else {
            kolumn.add(new Phrase(sortering.getKolumn(), PdfExportConstants.FRONTPAGE_NORMAL));
            riktning.add(new Chunk(sortering.getOrder(), PdfExportConstants.FRONTPAGE_NORMAL));
        }
        def.add(kolumn);
        def.add(riktning);
        return def;

    }

    private Element getAntalDesc(Urval urval, int showing, int total) {
        Paragraph def = new Paragraph(ANTAL_VISAR_ANTAL_PAGAENDE_SJUKFALL, PdfExportConstants.FRONTPAGE_H2);

        Paragraph kolumn = new Paragraph();
        kolumn.add(new Chunk(ANTAL_EXPORTEN_VISAR, PdfExportConstants.FRONTPAGE_NORMAL_BOLD));
        kolumn.add(new Phrase(String.valueOf(showing), PdfExportConstants.FRONTPAGE_NORMAL));

        Paragraph riktning = new Paragraph();
        riktning.add(new Chunk(urval == Urval.ISSUED_BY_ME ? ANTAL_TOTALT_MINA : ANTAL_TOTALT_PA_ENHETEN,
                PdfExportConstants.FRONTPAGE_NORMAL_BOLD));
        riktning.add(new Chunk(String.valueOf(total), PdfExportConstants.FRONTPAGE_NORMAL));

        def.add(kolumn);
        def.add(riktning);
        return def;

    }

    private PdfPTable createSjukfallTable(List<SjukfallEnhetRS> sjukfallList, Urval urval) throws DocumentException {

        PdfPTable table;

        // Setup column widths (relative to each other)
        if (Urval.ALL.equals(urval)) {
            table = new PdfPTable(new float[] { 0.8f, 1.7f, 0.8f, 3, 1, 1, 1.5f, 1.5f, 1.5f, 2, 0.8f, 2, 3f });
        } else {
            table = new PdfPTable(new float[] { 0.8f, 1.7f, 0.8f, 3, 1, 1, 1.5f, 1.5f, 1.5f, 2, 0.8f, 2 });
        }

        table.setWidthPercentage(100.0f);

        table.getDefaultCell().setBackgroundColor(TABLE_HEADER_BASE_COLOR);
        table.getDefaultCell().setBorderColor(TABLE_HEADER_BASE_COLOR);
        table.getDefaultCell().setNoWrap(true);
        table.getDefaultCell().setPadding(3f);
        table.getDefaultCell().setPaddingLeft(2f);

        addCell(table, TABLEHEADER_NR, PdfExportConstants.TABLE_HEADER_FONT);
        addCell(table, TABLEHEADER_PERSONNUMMER, PdfExportConstants.TABLE_HEADER_FONT);
        addCell(table, TABLEHEADER_ALDER, PdfExportConstants.TABLE_HEADER_FONT);
        addCell(table, TABLEHEADER_NAMN, PdfExportConstants.TABLE_HEADER_FONT);
        addCell(table, TABLEHEADER_KON, PdfExportConstants.TABLE_HEADER_FONT);
        addCell(table, TABLEHEADER_NUVARANDE_DIAGNOS, PdfExportConstants.TABLE_HEADER_FONT);
        addCell(table, TABLEHEADER_BIDIAGNOSER, PdfExportConstants.TABLE_HEADER_FONT);
        addCell(table, TABLEHEADER_STARTDATUM, PdfExportConstants.TABLE_HEADER_FONT);
        addCell(table, TABLEHEADER_SLUTDATUM, PdfExportConstants.TABLE_HEADER_FONT);
        addCell(table, TABLEHEADER_SJUKSKRIVNINGSLANGD, PdfExportConstants.TABLE_HEADER_FONT);
        addCell(table, TABLEHEADER_ANTAL, PdfExportConstants.TABLE_HEADER_FONT);
        addCell(table, TABLEHEADER_SJUKSKRIVNINGSGRAD, PdfExportConstants.TABLE_HEADER_FONT);
        if (Urval.ALL.equals(urval)) {
            addCell(table, TABLEHEADER_NUVARANDE_LAKARE, PdfExportConstants.TABLE_HEADER_FONT);
        }

        // Set cell styles for the non-header cells following hereafter
        table.getDefaultCell().setBackgroundColor(BaseColor.WHITE);
        table.getDefaultCell().setBorderColor(BaseColor.LIGHT_GRAY);
        table.getDefaultCell().setNoWrap(false);
        table.getDefaultCell().setPadding(2f);

        table.setHeaderRows(1);
        int rowNumber = 1;
        for (SjukfallEnhetRS is : sjukfallList) {
            if (rowNumber % 2 == 0) {
                table.getDefaultCell().setBackgroundColor(TABLE_EVEN_ROW_COLOR);
            } else {
                table.getDefaultCell().setBackgroundColor(TABLE_ODD_ROW_COLOR);
            }

            addCell(table, String.valueOf(rowNumber));
            addCell(table, getPersonnummerColumn(is));
            addCell(table, is.getPatient().getAlder());
            addCell(table, is.getPatient().getNamn());
            addCell(table, is.getPatient().getKon().getDescription());
            addCell(table, is.getDiagnos().getIntygsVarde());
            //TODO: bidiagnodes kommasepararerade (och "-" om inga)
            addCell(table, "-");

            addCell(table, is.getStart() != null ? YearMonthDateFormatter.print(is.getStart()) : "?");
            addCell(table, is.getSlut() != null ? YearMonthDateFormatter.print(is.getSlut()) : "?");
            addCell(table, getlangdText(is));
            addCell(table, is.getIntyg());
            addCell(table, getGrader(is));
            if (Urval.ALL.equals(urval)) {
                addCell(table, is.getLakare().getNamn());
            }
            rowNumber++;
        }

        return table;

    }

    private void addCell(PdfPTable table, Phrase p) {
        table.addCell(p);
    }

    private void addCell(PdfPTable table, int i) {
        addCell(table, Integer.toString(i), PdfExportConstants.TABLE_CELL_NORMAL);
    }
    private void addCell(PdfPTable table, String s) {
        addCell(table, s, PdfExportConstants.TABLE_CELL_NORMAL);
    }

    private void addCell(PdfPTable table, String s, Font font) {
        table.addCell(new Phrase(s, font));
    }

    private Phrase getGrader(SjukfallEnhetRS is) {
        boolean first = true;
        Phrase grader = new Phrase();
        for (Integer grad : is.getGrader()) {
            if (!first) {
                grader.add(new Chunk(UNICODE_RIGHT_ARROW_SYMBOL + " ", unicodeCapableFont));
            }
            if (grad == is.getAktivGrad()) {
                grader.add(new Chunk(grad.toString() + "% ", PdfExportConstants.TABLE_CELL_BOLD));
            } else {
                grader.add(new Chunk(grad.toString() + "% ", PdfExportConstants.TABLE_CELL_NORMAL));
            }
            first = false;
        }
        return grader;
    }

    private Phrase getPersonnummerColumn(SjukfallEnhetRS is) {
        Phrase p = new Phrase();
        p.add(new Chunk(is.getPatient().getId() != null ? is.getPatient().getId() : "", PdfExportConstants.TABLE_CELL_NORMAL));
        return p;
    }

    private Phrase getlangdText(SjukfallEnhetRS is) {
        Phrase p = new Phrase();
        p.add(new Chunk(String.format(FORMAT_ANTAL_DAGAR, is.getDagar()), PdfExportConstants.TABLE_CELL_NORMAL));
        return p;
    }

}
