/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.intyg.rehabstod.service.export.pdf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

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

import se.inera.intyg.infra.monitoring.annotation.PrometheusTimeMethod;
import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.auth.RehabstodUserPreferences.Preference;
import se.inera.intyg.rehabstod.common.util.StringUtil;
import se.inera.intyg.rehabstod.common.util.YearMonthDateFormatter;
import se.inera.intyg.rehabstod.service.Urval;
import se.inera.intyg.rehabstod.service.export.BaseExportService;
import se.inera.intyg.rehabstod.web.controller.api.dto.PrintSjukfallRequest;
import se.inera.intyg.rehabstod.web.model.SjukfallEnhet;
import se.inera.intyg.rehabstod.web.model.Sortering;

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
    private static final int ELLIPSIZE_AT_LIMIT = 20;
    private static final int ELLIPSIZE_AT_LIMIT_ANONYMOUS = 40;
    private static final String ELLIPSIZE_SUFFIX = "...";

    private PathMatchingResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    private Font unicodeCapableFont;

    @Override
    @PrometheusTimeMethod
    public byte[] export(List<SjukfallEnhet> sjukfallList, PrintSjukfallRequest printSjukfallRequest, RehabstodUser user, int total) {

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
            document.add(
                    createSjukfallTable(sjukfallList, user.getUrval(), printSjukfallRequest.isShowPatientId(), isSrsFeatureActive(user)));

            // Finish off by closing the document (will invoke the event handlers)
            document.close();

        } catch (DocumentException | IOException | RuntimeException e) {
            throw new PdfExportServiceException("Failed to create PDF export!", e);
        }

        return bos.toByteArray();
    }

    private Element createFrontPage(PrintSjukfallRequest printRequest, RehabstodUser user, int showing, int total) {
        String maxGlapp = user.getPreferences().get(Preference.MAX_ANTAL_DAGAR_MELLAN_INTYG);

        Paragraph sida = new Paragraph();

        sida.add(getUrvalDesc(user));
        sida.add(new Chunk(new LineSeparator()));
        sida.add(getFilterDesc(printRequest, user));
        sida.add(getSjukfallsDefDesc(maxGlapp));
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

        // Fritext
        Paragraph valdFritext = new Paragraph(FILTER_TITLE_FRITEXTFILTER, PdfExportConstants.FRONTPAGE_H3);
        valdFritext.add(new Paragraph(StringUtil.isNullOrEmpty(printRequest.getFritext()) ? "-" : printRequest.getFritext(),
                PdfExportConstants.FRONTPAGE_NORMAL));

        // Visa Patientuppgifter
        Paragraph visaPatientUppgifter = new Paragraph(FILTER_TITLE_VISAPATIENTUPPGIFTER + ": ", PdfExportConstants.FRONTPAGE_H3);
        visaPatientUppgifter.add(new Phrase(printRequest.isShowPatientId() ? " Ja" : " Nej", PdfExportConstants.FRONTPAGE_NORMAL));

        // Ålder
        Paragraph valdAlder = new Paragraph(FILTER_TITLE_VALD_ALDER, PdfExportConstants.FRONTPAGE_H3);
        Paragraph alderVarden = new Paragraph();
        alderVarden.add(new Chunk("Mellan ", PdfExportConstants.FRONTPAGE_NORMAL));
        alderVarden
                .add(new Chunk(String.valueOf(printRequest.getAldersIntervall().getMin()), PdfExportConstants.FRONTPAGE_NORMAL_BOLD));
        alderVarden.add(new Chunk(" och ", PdfExportConstants.FRONTPAGE_NORMAL));
        alderVarden
                .add(new Chunk(String.valueOf(printRequest.getAldersIntervall().getMax()), PdfExportConstants.FRONTPAGE_NORMAL_BOLD));
        alderVarden.add(new Chunk(" år.", PdfExportConstants.FRONTPAGE_NORMAL));
        valdAlder.add(alderVarden);

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

        // Slutdddatum
        Paragraph valdSlutdatum = new Paragraph(FILTER_TITLE_VALD_SLUTDATUM, PdfExportConstants.FRONTPAGE_H3);
        Paragraph slutdatumVarden = new Paragraph(getFilterDate(printRequest.getSlutdatumIntervall()), PdfExportConstants.FRONTPAGE_NORMAL);
        valdSlutdatum.add(slutdatumVarden);

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

        // Lagg ihop undergrupperna till filter
        Paragraph filter = new Paragraph(VALDA_FILTER, PdfExportConstants.FRONTPAGE_H2);
        filter.add(valdFritext);
        filter.add(visaPatientUppgifter);
        filter.add(valdAlder);
        filter.add(valdaDiagnoser);
        filter.add(valdSlutdatum);
        filter.add(valdSjukskrivninglangd);
        filter.add(valdaLakare);

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

    private Element getSjukfallsDefDesc(String maxGlapp) {
        Paragraph def = new Paragraph();
        def.add(new Paragraph(H2_SJUKFALLSINSTALLNING, PdfExportConstants.FRONTPAGE_H2));
        def.add(new Phrase(MAXANTAL_DAGAR_UPPEHALL_MELLAN_INTYG + ": ", PdfExportConstants.FRONTPAGE_NORMAL));
        def.add(new Phrase(maxGlapp + " dagar", PdfExportConstants.FRONTPAGE_NORMAL_BOLD));
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

    private PdfPTable createTableColumns(Urval urval, boolean showPatientId, boolean showSrsRisk) {
        List<Float> tempHeaders = new ArrayList<>();
        tempHeaders.add(0.5f); // # radnr
        if (showPatientId) {
            tempHeaders.add(1.3f); // personnr
        }
        tempHeaders.add(0.5f); // # Ålder
        if (showPatientId) {
            tempHeaders.add(2f); // namn
        }
        tempHeaders.add(0.7f); // Kön

        if (showPatientId) {
            tempHeaders.add(2.1f); // Diagnos
        } else {
            tempHeaders.add(3.4f); // Diagnos extra bred eftersom vi inte visar patientinfo
        }

        tempHeaders.add(1.1f); // Startdatum
        tempHeaders.add(1.1f); // Slutdatum
        tempHeaders.add(1.0f); // Längd
        tempHeaders.add(0.45f); // Antal
        tempHeaders.add(2f); // Grader
        if (Urval.ALL.equals(urval)) {
            tempHeaders.add(2f); // Läkare
        }
        if (showSrsRisk) {
            tempHeaders.add(1f); // Srs Risk
        }

        return new PdfPTable(ArrayUtils.toPrimitive(tempHeaders.toArray(new Float[tempHeaders.size()])));

    }

    private PdfPTable createSjukfallTable(List<SjukfallEnhet> sjukfallList, Urval urval, boolean showPatientId, boolean showSrsRisk)
            throws DocumentException {

        // Setup column widths (relative to each other)
        PdfPTable table = createTableColumns(urval, showPatientId, showSrsRisk);

        table.setWidthPercentage(100.0f);

        table.getDefaultCell().setBackgroundColor(TABLE_HEADER_BASE_COLOR);
        table.getDefaultCell().setBorderColor(TABLE_HEADER_BASE_COLOR);
        table.getDefaultCell().setNoWrap(true);
        table.getDefaultCell().setPadding(3f);
        table.getDefaultCell().setPaddingLeft(2f);

        addCell(table, TABLEHEADER_NR, PdfExportConstants.TABLE_HEADER_FONT);

        if (showPatientId) {
            addCell(table, TABLEHEADER_PERSONNUMMER, PdfExportConstants.TABLE_HEADER_FONT);
        }

        addCell(table, TABLEHEADER_ALDER, PdfExportConstants.TABLE_HEADER_FONT);

        if (showPatientId) {
            addCell(table, TABLEHEADER_NAMN, PdfExportConstants.TABLE_HEADER_FONT);
        }

        addCell(table, TABLEHEADER_KON, PdfExportConstants.TABLE_HEADER_FONT);
        addCell(table, TABLEHEADER_NUVARANDE_DIAGNOS, PdfExportConstants.TABLE_HEADER_FONT);
        addCell(table, TABLEHEADER_STARTDATUM, PdfExportConstants.TABLE_HEADER_FONT);
        addCell(table, TABLEHEADER_SLUTDATUM, PdfExportConstants.TABLE_HEADER_FONT);
        addCell(table, TABLEHEADER_SJUKSKRIVNINGSLANGD, PdfExportConstants.TABLE_HEADER_FONT);
        addCell(table, TABLEHEADER_ANTAL, PdfExportConstants.TABLE_HEADER_FONT);
        addCell(table, TABLEHEADER_SJUKSKRIVNINGSGRAD, PdfExportConstants.TABLE_HEADER_FONT);
        if (Urval.ALL.equals(urval)) {
            addCell(table, TABLEHEADER_NUVARANDE_LAKARE, PdfExportConstants.TABLE_HEADER_FONT);
        }

        if (showSrsRisk) {
            addCell(table, TABLEHEADER_SRS_RISK, PdfExportConstants.TABLE_HEADER_FONT);
        }

        // Set cell styles for the non-header cells following hereafter
        table.getDefaultCell().setBackgroundColor(BaseColor.WHITE);
        table.getDefaultCell().setBorderColor(BaseColor.LIGHT_GRAY);
        table.getDefaultCell().setNoWrap(false);
        table.getDefaultCell().setPadding(2f);

        table.setHeaderRows(1);
        int rowNumber = 1;
        for (SjukfallEnhet is : sjukfallList) {
            if (rowNumber % 2 == 0) {
                table.getDefaultCell().setBackgroundColor(TABLE_EVEN_ROW_COLOR);
            } else {
                table.getDefaultCell().setBackgroundColor(TABLE_ODD_ROW_COLOR);
            }

            addCell(table, String.valueOf(rowNumber));

            if (showPatientId) {
                addCell(table, getPersonnummerColumn(is));
            }

            addCell(table, is.getPatient().getAlder() + " år");

            if (showPatientId) {
                addCell(table, is.getPatient().getNamn());
            }
            addCell(table, is.getPatient().getKon().getDescription());
            addCell(table, getCompoundDiagnoseText(is, showPatientId));
            addCell(table, is.getStart() != null ? YearMonthDateFormatter.print(is.getStart()) : "?");
            addCell(table, is.getSlut() != null ? YearMonthDateFormatter.print(is.getSlut()) : "?");
            addCell(table, getlangdText(is));
            addCell(table, is.getIntyg());
            addCell(table, getGrader(is));
            if (Urval.ALL.equals(urval)) {
                addCell(table, is.getLakare().getNamn());
            }
            if (showSrsRisk) {
                addCell(table, getRiskKategoriDesc(is.getRiskSignal()));
            }
            rowNumber++;
        }

        return table;

    }

    private String getCompoundDiagnoseText(SjukfallEnhet sf, boolean showPatientId) {
        String dignosKodSpace = sf.getDiagnos().getKod() + " ";

        StringBuilder b = new StringBuilder();
        b.append(dignosKodSpace);
        b.append(ellipsize(sf.getDiagnos().getBeskrivning(), dignosKodSpace.length(), showPatientId));
        b.append(diagnoseListToString(sf.getBiDiagnoser()));
        return b.toString();
    }

    private String ellipsize(String namn, int dignosKodLength, boolean showPatientId) {
        if (StringUtil.isNullOrEmpty(namn)) {
            return "";
        }

        int maxLength = showPatientId ? ELLIPSIZE_AT_LIMIT : ELLIPSIZE_AT_LIMIT_ANONYMOUS;
        maxLength -= dignosKodLength; // Ta bort längden för diagnoskoden
        if (namn.length() > maxLength) {
            return namn.substring(0, maxLength) + ELLIPSIZE_SUFFIX;
        } else {
            return namn;
        }
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

    private Phrase getGrader(SjukfallEnhet is) {
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

    private Phrase getPersonnummerColumn(SjukfallEnhet is) {
        Phrase p = new Phrase();
        p.add(new Chunk(is.getPatient().getId() != null ? is.getPatient().getId() : "", PdfExportConstants.TABLE_CELL_NORMAL));
        return p;
    }

    private Phrase getlangdText(SjukfallEnhet is) {
        Phrase p = new Phrase();
        p.add(new Chunk(String.format(FORMAT_ANTAL_DAGAR, is.getDagar()), PdfExportConstants.TABLE_CELL_NORMAL));
        return p;
    }

}
