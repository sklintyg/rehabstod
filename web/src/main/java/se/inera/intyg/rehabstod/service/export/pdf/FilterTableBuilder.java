/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
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


import static se.inera.intyg.rehabstod.service.export.pdf.PdfConstants.FILTER_SELECTION_VALUE_ALLA;
import static se.inera.intyg.rehabstod.service.export.pdf.PdfConstants.FILTER_TITLE_ALDERSPANN;
import static se.inera.intyg.rehabstod.service.export.pdf.PdfConstants.FILTER_TITLE_AVSLUTADE_SJUKFALL;
import static se.inera.intyg.rehabstod.service.export.pdf.PdfConstants.FILTER_TITLE_DIAGNOSER;
import static se.inera.intyg.rehabstod.service.export.pdf.PdfConstants.FILTER_TITLE_FRITEXT;
import static se.inera.intyg.rehabstod.service.export.pdf.PdfConstants.FILTER_TITLE_ARENDESTATUS;
import static se.inera.intyg.rehabstod.service.export.pdf.PdfConstants.FILTER_TITLE_LAKARE;
import static se.inera.intyg.rehabstod.service.export.pdf.PdfConstants.FILTER_TITLE_MAXANTAL_DAGAR_UPPEHALL_MELLAN_INTYG;
import static se.inera.intyg.rehabstod.service.export.pdf.PdfConstants.FILTER_TITLE_PATIENTUPPGIFTER;
import static se.inera.intyg.rehabstod.service.export.pdf.PdfConstants.FILTER_TITLE_SJUKSKRIVNINGSLANGD;
import static se.inera.intyg.rehabstod.service.export.pdf.PdfConstants.FILTER_TITLE_SLUTDATUM;
import static se.inera.intyg.rehabstod.service.export.pdf.PdfUtil.TABLE_SEPARATOR_BORDER;
import static se.inera.intyg.rehabstod.service.export.pdf.PdfUtil.aCell;
import static se.inera.intyg.rehabstod.service.export.pdf.PdfUtil.ellipsize;
import static se.inera.intyg.rehabstod.service.export.pdf.PdfUtil.millimetersToPoints;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.BlockElement;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.UnitValue;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.auth.RehabstodUserPreferences.Preference;
import se.inera.intyg.rehabstod.common.util.StringUtil;
import se.inera.intyg.rehabstod.service.Urval;
import se.inera.intyg.rehabstod.service.diagnos.DiagnosKapitelService;
import se.inera.intyg.rehabstod.service.export.BaseExportService;
import se.inera.intyg.rehabstod.web.controller.api.dto.PrintSjukfallRequest;
import se.inera.intyg.rehabstod.web.model.LangdIntervall;

/**
 * Encapsulates the logic of building the "filter selection" table in the pdf.
 */
class FilterTableBuilder {

    protected static final String PATIENTUPPGIFTER_VISAS = "Visas";
    protected static final String PATIENTUPPGIFTER_VISAS_EJ = "Visas ej";
    protected static final String LANGD_OVER_ONE_YEAR = "366";
    protected static final String LANGD_OVER_ONE_YEAR_DISPLAYVALUE = "365+";
    private static final float TABLE_WIDTH = 100f;
    private static final float TABLE_PADDING = 2.5f;
    private static final float TABLE_MARGIN_TOP = 10f;
    private static final String FILTER_TITLE_FILTERSECTION = "Valda filter";
    private static final String FILTER_TITLE_SETTINGSSECTION = "Sjukfallsinställningar";
    private static final int MAXLENGTH_FRITEXT = 30;
    private static final String TEMPLATESTRING_SJUKSKRIVNINGSLANGD = "Mellan %s och %s dagar";
    private static final String TEMPLATESTRING_ALDERSINTERVALL = "Mellan %s och %s år";
    private static final int MAXLENGTH_FILTERDIAGNOS = 50;
    private static final int MAXLENGTH_LAKARNAMN = 30;
    private static final String TEMPLATESTRING_GLAPPDAGAR = "%s dagar";
    private static final String TEMPLATESTRING_DAGAR_AVSLUTADE = "%s dagar";
    private static final String NO_FILTER_VALUES_SELECTED_PLACEHOLDER = "-";
    private static final Color FILTER_TABLE_BACKGROUND_COLOR = new DeviceRgb(0xEF, 0xEF, 0xEF);
    private static final float FILTER_TABLE_MIN_HEIGHT = 25f;
    private DiagnosKapitelService diagnosKapitelService;
    private PdfStyle style;

    // CHECKSTYLE:OFF MagicNumber

    FilterTableBuilder(DiagnosKapitelService diagnosKapitelService, PdfStyle style) {
        this.diagnosKapitelService = diagnosKapitelService;
        this.style = style;
    }

    BlockElement buildFilterSettings(PrintSjukfallRequest printRequest, RehabstodUser user) {

        int nrFilterColumns = user.getUrval() == Urval.ALL ? 4 : 3;

        Table table = new Table(nrFilterColumns)
            .setWidth(UnitValue.createPercentValue(TABLE_WIDTH))
            .setBackgroundColor(FILTER_TABLE_BACKGROUND_COLOR)
            .setMinHeight(millimetersToPoints(FILTER_TABLE_MIN_HEIGHT))
            .setPadding(millimetersToPoints(TABLE_PADDING))
            .setMarginTop(TABLE_MARGIN_TOP)
            .setBorder(Border.NO_BORDER);

        table.addCell(new Cell(1, nrFilterColumns)
            .addStyle(style.getPageHeaderStyle())
            .setBorder(Border.NO_BORDER)
            .add(new Paragraph(FILTER_TITLE_FILTERSECTION).addStyle(style.getPageHeaderStyle())));

        table.addCell(getDiagnosFilterCell(printRequest));
        if (user.getUrval() == Urval.ALL) {
            table.addCell(getLakareFilterCell(printRequest, user));
        }
        table.addCell(getSjukskrivningFilterCell(printRequest));
        table.addCell(getArendeFilterCell(printRequest));

        // Settings
        table.addCell(new Cell(1, nrFilterColumns)
            .addStyle(style.getPageHeaderStyle())
            .setBorder(Border.NO_BORDER)
            .add(new Paragraph(FILTER_TITLE_SETTINGSSECTION).addStyle(style.getPageHeaderStyle())));

        table.addCell(buildFilterCellMulti(true, 1,
            Arrays.asList(FILTER_TITLE_MAXANTAL_DAGAR_UPPEHALL_MELLAN_INTYG),
            Arrays.asList(String.format(TEMPLATESTRING_GLAPPDAGAR, user.getPreferences().get(Preference.MAX_ANTAL_DAGAR_MELLAN_INTYG)))));
        table.addCell(buildFilterCellMulti(false, nrFilterColumns - 1,
            Arrays.asList(FILTER_TITLE_AVSLUTADE_SJUKFALL),
            Arrays.asList(
                String
                    .format(TEMPLATESTRING_DAGAR_AVSLUTADE, user.getPreferences().get(Preference.MAX_ANTAL_DAGAR_SEDAN_SJUKFALL_AVSLUT)))));

        return table;

    }

    private Cell getArendeFilterCell(PrintSjukfallRequest printRequest) {
        //arende
        String arende = BaseExportService.getQAFilterDisplayValue(printRequest.getQa());

        //visa patientuppgifter
        String patientuppgifter = printRequest.isShowPatientId() ? PATIENTUPPGIFTER_VISAS : PATIENTUPPGIFTER_VISAS_EJ;

        //fritext
        String fritext =
            StringUtil.isNullOrEmpty(printRequest.getFritext()) ? NO_FILTER_VALUES_SELECTED_PLACEHOLDER
                : ellipsize(printRequest.getFritext(), MAXLENGTH_FRITEXT);

        return buildFilterCellMulti(false, 1,
            Arrays.asList(FILTER_TITLE_ARENDESTATUS, FILTER_TITLE_PATIENTUPPGIFTER, FILTER_TITLE_FRITEXT),
            Arrays.asList(arende, patientuppgifter, fritext));
    }

    private Cell getSjukskrivningFilterCell(PrintSjukfallRequest printRequest) {
        //sjukskrivningslängd
        String sjukskrivning = getSjukskrivningsintervalDescription(printRequest.getLangdIntervall());

        //slutdatum
        String slutdatum = getSlutdatumFilterDate(printRequest.getSlutdatumIntervall());

        //aldersspann
        String alderspann = printRequest.getAldersIntervall() == null ? NO_FILTER_VALUES_SELECTED_PLACEHOLDER : String
            .format(TEMPLATESTRING_ALDERSINTERVALL, printRequest.getAldersIntervall().getMin(), printRequest.getAldersIntervall().getMax());

        return buildFilterCellMulti(false, 1,
            Arrays.asList(FILTER_TITLE_SJUKSKRIVNINGSLANGD, FILTER_TITLE_SLUTDATUM, FILTER_TITLE_ALDERSPANN),
            Arrays.asList(sjukskrivning, slutdatum, alderspann));
    }

    private String getSjukskrivningsintervalDescription(LangdIntervall intervall) {
        if (intervall == null) {
            return NO_FILTER_VALUES_SELECTED_PLACEHOLDER;
        }

        String minDesc = LANGD_OVER_ONE_YEAR.equals(intervall.getMin()) ? LANGD_OVER_ONE_YEAR_DISPLAYVALUE : intervall.getMin();
        String maxDesc = LANGD_OVER_ONE_YEAR.equals(intervall.getMax()) ? LANGD_OVER_ONE_YEAR_DISPLAYVALUE : intervall.getMax();
        return String.format(TEMPLATESTRING_SJUKSKRIVNINGSLANGD, minDesc, maxDesc);
    }

    private Cell getLakareFilterCell(PrintSjukfallRequest printRequest, RehabstodUser user) {

        final List<String> lakare = printRequest.getLakare() != null ? printRequest.getLakare()
            : Arrays.asList(user.getUrval() == Urval.ISSUED_BY_ME ? user.getNamn() : FILTER_SELECTION_VALUE_ALLA);

        List<String> truncated = lakare.stream().map(name -> ellipsize(name, MAXLENGTH_LAKARNAMN)).collect(Collectors.toList());
        return buildFilterCell(false, FILTER_TITLE_LAKARE, truncated);
    }

    public static String getSlutdatumFilterDate(LangdIntervall dateIntervall) {
        String max = dateIntervall.getMax();
        String min = dateIntervall.getMin();

        if (max != null && !max.isEmpty() && min != null && !min.isEmpty()) {
            if (max.equals(min)) {
                return max;
            } else {
                return min + " till " + max;
            }
        }

        return "-";
    }

    private Cell getDiagnosFilterCell(PrintSjukfallRequest printRequest) {
        final List<String> diagnoses = printRequest.getDiagnosGrupper() != null ? printRequest.getDiagnosGrupper().stream()
            .map(dg -> getDiagnosKapitelDisplayValue(dg)).collect(Collectors.toList())
            : Arrays.asList(NO_FILTER_VALUES_SELECTED_PLACEHOLDER);

        return buildFilterCell(true, FILTER_TITLE_DIAGNOSER, diagnoses);
    }

    private String getDiagnosKapitelDisplayValue(String diagnosKapitel) {
        StringBuilder b = new StringBuilder(diagnosKapitel);
        if (b.length() > 0) {
            b.append(": ");
        }
        b.append(diagnosKapitelService.getDiagnosKapitel(diagnosKapitel).getName());

        return ellipsize(b.toString(), MAXLENGTH_FILTERDIAGNOS);
    }

    private Cell buildFilterCell(boolean isFirstCell, String headerText, List<String> values) {
        Cell cell = getFilterCell(isFirstCell);
        Table table = new Table(1);
        table.setBorder(Border.NO_BORDER);

        Paragraph headerParagraph = new Paragraph(headerText);
        headerParagraph.addStyle(style.getCellHeaderParagraphStyle());
        Cell header = new Cell().add(headerParagraph).addStyle(style.getCellStyle());
        table.addCell(header);
        values.forEach(value -> table
            .addCell(new Cell().add(new Paragraph(value).addStyle(style.getDefaultParagraphStyle())).addStyle(style.getCellStyle())));

        cell.add(table);

        return cell;
    }


    private Cell getFilterCell(boolean isFirstCell) {
        return getFilterCell(isFirstCell, 1);
    }

    private Cell getFilterCell(boolean isFirstCell, int colspan) {
        return isFirstCell ? aCell(colspan).setBorderRight(TABLE_SEPARATOR_BORDER) : aCell(colspan).setBorderLeft(TABLE_SEPARATOR_BORDER);
    }

    private Cell buildFilterCellMulti(boolean isFirstCell, int colspan, List<String> headerTexts, List<String> values) {
        Cell cell = getFilterCell(isFirstCell, colspan);

        Table table = new Table(2);
        table.setBorder(Border.NO_BORDER);

        for (int i = 0; i < headerTexts.size(); i++) {
            Cell header = new Cell().add(new Paragraph(headerTexts.get(i)).addStyle(style.getCellHeaderParagraphStyle()))
                .addStyle(style.getCellStyle());
            Cell value = new Cell().add(new Paragraph(values.get(i))).addStyle(style.getCellStyle());
            table.addCell(header).addCell(value);
        }

        cell.add(table);

        return cell;
    }
}
