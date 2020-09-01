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

import static se.inera.intyg.rehabstod.service.export.BaseExportService.diagnoseListToString;
import static se.inera.intyg.rehabstod.service.export.BaseExportService.getRiskKategoriDesc;
import static se.inera.intyg.rehabstod.service.export.pdf.PdfConstants.FORMAT_ANTAL_DAGAR;
import static se.inera.intyg.rehabstod.service.export.pdf.PdfConstants.UNICODE_RIGHT_ARROW_SYMBOL;
import static se.inera.intyg.rehabstod.service.export.pdf.PdfUtil.TABLE_SEPARATOR_BORDER;
import static se.inera.intyg.rehabstod.service.export.pdf.PdfUtil.aCell;
import static se.inera.intyg.rehabstod.service.export.pdf.PdfUtil.ellipsize;

import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.BlockElement;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.UnitValue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.auth.RehabstodUserPreferences.Preference;
import se.inera.intyg.rehabstod.common.util.YearMonthDateFormatter;
import se.inera.intyg.rehabstod.service.Urval;
import se.inera.intyg.rehabstod.service.export.ExportField;
import se.inera.intyg.rehabstod.web.controller.api.dto.PrintSjukfallRequest;
import se.inera.intyg.rehabstod.web.model.SjukfallEnhet;

/**
 * Encapsulates the logic of building the "sjukfall" table in the pdf.
 */
class SjukfallTableBuilder {

    protected static final String TEMPLATE_ALDER = "%d år";
    private static final float TABLE_TOTAL_WIDTH = 100f;
    private static final float WIDTH_COLUMN_1 = 13f;
    private static final float WIDTH_COLUMN_2 = 30f;
    private static final float WIDTH_COLUMN_3 = 15f;
    private static final float WIDTH_COLUMN_4 = 22f;
    private static final float WIDTH_COLUMN_5 = 20f;
    private static final int MAXLENGTH_PATIENT_NAMN = 40;
    private static final int MAXLENGTH_LAKARE_NAMN = 30;
    private static final int MAXLENGTH_DIAGNOS = 40;
    private static final int TOTAL_NUM_COLUMNS = 5;
    private PdfStyle style;

    SjukfallTableBuilder(PdfStyle style) {
        this.style = style;
    }

    private static Paragraph aParagraph(String text) {
        return new Paragraph(text);
    }

    private static Cell aSjukFallCell(float widthPercentage) {
        return new Cell()
            .setBorder(Border.NO_BORDER)
            .setBorderTop(TABLE_SEPARATOR_BORDER)
            .setBorderBottom(TABLE_SEPARATOR_BORDER)
            .setWidth(UnitValue.createPercentValue(widthPercentage));
    }

    BlockElement buildsjukfallTable(List<SjukfallEnhet> sjukfallList, RehabstodUser user,
        PrintSjukfallRequest printSjukfallRequest, boolean srsFeatureActive) {

        final List<ExportField> enabledFields = ExportField.fromJson(user.getPreferences().get(Preference.SJUKFALL_TABLE_COLUMNS));

        Table table = new Table(TOTAL_NUM_COLUMNS)
            .setWidth(UnitValue.createPercentValue(TABLE_TOTAL_WIDTH));
        for (int i = 0; i < sjukfallList.size(); i++) {
            SjukfallEnhet sf = sjukfallList.get(i);
            boolean avslutat = sf.isNyligenAvslutat();

            // Column 1 - PatientId is not only controlled by user preference enabledFields toggling
            List<ExportField> c1Headers = new ArrayList<>();
            List<Paragraph> c1Values = new ArrayList<>();
            c1Headers.add(ExportField.LINE_NR);
            c1Values.add(aParagraph(String.valueOf(i + 1)));
            if (printSjukfallRequest.isShowPatientId()) {
                c1Headers.add(ExportField.PATIENT_ID);
                c1Values.add(aParagraph(sf.getPatient().getId()));
            }
            c1Headers.add(ExportField.PATIENT_AGE);
            c1Values.add(aParagraph(String.format(TEMPLATE_ALDER, sf.getPatient().getAlder())));

            Cell c1 = aSjukFallCell(WIDTH_COLUMN_1)
                .setBorderLeft(TABLE_SEPARATOR_BORDER)
                .add(buildSjukfallCellTable(enabledFields, c1Headers, c1Values, avslutat));

            // Column 2 - Patient name is not only controlled by user preference enabledFields toggling
            List<ExportField> c2Headers = new ArrayList<>();
            List<Paragraph> c2Values = new ArrayList<>();
            if (printSjukfallRequest.isShowPatientId()) {
                c2Headers.add(ExportField.PATIENT_NAME);
                c2Values.add(aParagraph(ellipsize(sf.getPatient().getNamn(), MAXLENGTH_PATIENT_NAMN)));
            }
            c2Headers.add(ExportField.PATIENT_GENDER);
            c2Values.add(aParagraph(sf.getPatient().getKon().getDescription()));
            c2Headers.add(ExportField.DIAGNOSE);
            c2Values.add(aParagraph(getCompoundDiagnoseText(sf)));

            Cell c2 = aSjukFallCell(WIDTH_COLUMN_2)
                .add(buildSjukfallCellTable(enabledFields, c2Headers, c2Values, avslutat));

            Cell c3 = aSjukFallCell(WIDTH_COLUMN_3)
                .add(buildSjukfallCellTable(enabledFields,
                    Arrays.asList(
                        ExportField.STARTDATE,
                        ExportField.ENDDATE,
                        ExportField.DAYS),
                    Arrays.asList(
                        aParagraph(YearMonthDateFormatter.print(sf.getStart())),
                        aParagraph(YearMonthDateFormatter.print(sf.getSlut())),
                        aParagraph(String.format(FORMAT_ANTAL_DAGAR, sf.getDagar()))), avslutat));

            Cell c4 = aSjukFallCell(WIDTH_COLUMN_4)
                .add(buildSjukfallCellTable(enabledFields,
                    Arrays.asList(
                        ExportField.NR_OF_INTYG,
                        ExportField.GRADER,
                        ExportField.ARENDEN),
                    Arrays.asList(
                        aParagraph(String.valueOf(sf.getIntyg())),
                        getGrader(sf),
                        aParagraph(String.valueOf(sf.getObesvaradeKompl()))), avslutat));

            // Column 5 - Lakare and SRS are not only controlled by user preference enabledFields toggling
            List<ExportField> c5Headers = new ArrayList<>();
            List<Paragraph> c5Values = new ArrayList<>();

            if (user.getUrval().equals(Urval.ALL)) {
                c5Headers.add(ExportField.LAKARE);
                c5Values.add(aParagraph(ellipsize(sf.getLakare().getNamn(), MAXLENGTH_LAKARE_NAMN)));
            }

            if (srsFeatureActive) {
                c5Headers.add(ExportField.SRS);
                c5Values.add(aParagraph(getRiskKategoriDesc(sf.getRiskSignal())));
            }

            Cell c5 = aSjukFallCell(WIDTH_COLUMN_5).setBorderRight(TABLE_SEPARATOR_BORDER)
                .add(buildSjukfallCellTable(enabledFields, c5Headers, c5Values, avslutat));

            table.addCell(c1);
            table.addCell(c2);
            table.addCell(c3);
            table.addCell(c4);
            table.addCell(c5);
        }
        return table;
    }

    private String getCompoundDiagnoseText(SjukfallEnhet sf) {
        StringBuilder b = new StringBuilder();
        b.append(sf.getDiagnos().getKod()).append(" ");
        final String bidiagnoser = diagnoseListToString(sf.getBiDiagnoser());
        b.append(ellipsize(sf.getDiagnos().getBeskrivning(), MAXLENGTH_DIAGNOS - bidiagnoser.length()));
        b.append(bidiagnoser);
        return b.toString();
    }

    private Paragraph getGrader(SjukfallEnhet is) {
        boolean first = true;
        Paragraph grader = new Paragraph();
        for (Integer grad : is.getGrader()) {
            if (!first) {
                grader.add(new Text(UNICODE_RIGHT_ARROW_SYMBOL + " ").addStyle(style.getCellStyle()));
            }
            grader.add(new Text(grad.toString() + "% ").addStyle(grad == is.getAktivGrad() ? style.getCellStyleBold()
                : style.getCellStyle()));
            first = false;
        }
        return grader;
    }

    private Table buildSjukfallCellTable(List<ExportField> enabledFields, List<ExportField> headerFields, List<Paragraph> values,
        boolean avslutat) {

        Table table = new Table(2).setKeepTogether(true);
        table.setBorder(Border.NO_BORDER);

        for (int i = 0; i < headerFields.size(); i++) {
            if (enabledFields.contains(headerFields.get(i))) {
                Cell header = aCell(1).add(new Paragraph(headerFields.get(i).getLabelPdf()).addStyle(style.getCellHeaderParagraphStyle()))
                    .addStyle(
                        avslutat ? style.getCellStyleItalic() : style.getCellStyle());
                Cell value = aCell(1).add(values.get(i)).addStyle(avslutat ? style.getCellStyleItalic() : style.getCellStyle());
                table.addCell(header).addCell(value);
            }
        }
        return table;
    }
}
