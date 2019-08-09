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

import static se.inera.intyg.rehabstod.service.export.BaseExportService.FILTER_TITLE_FRITEXTFILTER;
import static se.inera.intyg.rehabstod.service.export.BaseExportService.FILTER_TITLE_KOMPLETTERINGSSTATUS;
import static se.inera.intyg.rehabstod.service.export.BaseExportService.FILTER_TITLE_VALDA_DIAGNOSER;
import static se.inera.intyg.rehabstod.service.export.BaseExportService.FILTER_TITLE_VALDA_LAKARE;
import static se.inera.intyg.rehabstod.service.export.BaseExportService.FILTER_TITLE_VALD_ALDER;
import static se.inera.intyg.rehabstod.service.export.BaseExportService.FILTER_TITLE_VALD_SJUKSKRIVNINGSLANGD;
import static se.inera.intyg.rehabstod.service.export.BaseExportService.FILTER_TITLE_VALD_SLUTDATUM;
import static se.inera.intyg.rehabstod.service.export.BaseExportService.FILTER_TITLE_VISAPATIENTUPPGIFTER;
import static se.inera.intyg.rehabstod.service.export.BaseExportService.MAXANTAL_DAGAR_UPPEHALL_MELLAN_INTYG;
import static se.inera.intyg.rehabstod.service.export.BaseExportService.SELECTION_VALUE_ALLA;
import static se.inera.intyg.rehabstod.service.export.BaseExportService.getFilterDate;
import static se.inera.intyg.rehabstod.service.export.BaseExportService.getKompletteringFilterDisplayValue;
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
import se.inera.intyg.rehabstod.web.controller.api.dto.PrintSjukfallRequest;

/**
 * Encapsulates the logic of building the "filter selection" table in the pdf.
 */
public class FilterTableBuilder {

  protected static final int TOTAL_NUM_COLUMNS = 5;
  protected static final float TABLE_WIDTH = 100f;
  protected static final float TABLE_PADDING = 2.5f;
  protected static final float TABLE_MARGIN_TOP = 10f;
  protected static final String FILTER_TITLE_TEXT = "Valda filter och sjukfallsinställningar";
  protected static final int MAXLENGTH_FRITEXT = 30;
  protected static final String TEMPLATESTRING_SJUKSKRIVNINGSLANGD = "Mellan %s och %s dagar";
  protected static final String TEMPLATESTRING_ALDERSINTERVALL = "Mellan %s och %s år";
  protected static final int MAXLENGTH_FILTERDIAGNOS = 50;
  protected static final int MAXLENGTH_LAKARNAMN = 30;
  protected static final String TEMPLATESTRING_GLAPPDAGAR = "%s dagar";
  private static final String NO_FILTER_VALUES_SELECTED_PLACEHOLDER = "-";
  private static final Color FILTER_TABLE_BACKGROUND_COLOR = new DeviceRgb(0xEF, 0xEF, 0xEF);
  private static final float FILTER_TABLE_MIN_HEIGHT = 25f;
  private DiagnosKapitelService diagnosKapitelService;
  private PdfStyle style;

  public FilterTableBuilder(DiagnosKapitelService diagnosKapitelService, PdfStyle style) {
    this.diagnosKapitelService = diagnosKapitelService;
    this.style = style;
  }

  public BlockElement buildFilterSettings(PrintSjukfallRequest printRequest, RehabstodUser user) {
    String maxGlapp = user.getPreferences().get(Preference.MAX_ANTAL_DAGAR_MELLAN_INTYG);

    Table table = new Table(TOTAL_NUM_COLUMNS)
        .setWidth(UnitValue.createPercentValue(TABLE_WIDTH))
        .setBackgroundColor(FILTER_TABLE_BACKGROUND_COLOR)
        .setMinHeight(millimetersToPoints(FILTER_TABLE_MIN_HEIGHT))
        .setPadding(millimetersToPoints(TABLE_PADDING))
        .setMarginTop(TABLE_MARGIN_TOP)
        .setBorder(Border.NO_BORDER);

    Cell titleCell = new Cell(1, TOTAL_NUM_COLUMNS)
        .addStyle(style.getPageHeaderStyle())
        .setBorder(Border.NO_BORDER)
        .add(new Paragraph(FILTER_TITLE_TEXT).addStyle(style.getPageHeaderStyle()));
    table.addCell(titleCell);

    table.addCell(getDiagnosFilterCell(printRequest));
    table.addCell(getLakareFilterCell(printRequest, user));
    table.addCell(getSjukskrivningFilterCell(printRequest));
    table.addCell(getKompletteringFilterCell(printRequest));
    table.addCell(
        buildFilterCell(false, MAXANTAL_DAGAR_UPPEHALL_MELLAN_INTYG, Arrays.asList(String.format(TEMPLATESTRING_GLAPPDAGAR, maxGlapp))));

    return table;

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
    return isFirstCell ? aCell().setBorderRight(TABLE_SEPARATOR_BORDER) : aCell().setBorderLeft(TABLE_SEPARATOR_BORDER);
  }

  private Cell buildFilterCellMulti(boolean isFirstCell, List<String> headerTexts, List<String> values) {
    Cell cell = getFilterCell(isFirstCell);

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

  private Cell getKompletteringFilterCell(PrintSjukfallRequest printRequest) {
    //komplettering
    String komplettering = getKompletteringFilterDisplayValue(printRequest.getKomplettering());

    //visa patientuppgifter
    String patientuppgifter = printRequest.isShowPatientId() ? "Ja" : "Nej";

    //fritext
    String fritext =
        StringUtil.isNullOrEmpty(printRequest.getFritext()) ? NO_FILTER_VALUES_SELECTED_PLACEHOLDER
            : ellipsize(printRequest.getFritext(), MAXLENGTH_FRITEXT);

    return buildFilterCellMulti(false,
        Arrays.asList(FILTER_TITLE_KOMPLETTERINGSSTATUS, FILTER_TITLE_VISAPATIENTUPPGIFTER, FILTER_TITLE_FRITEXTFILTER),
        Arrays.asList(komplettering, patientuppgifter, fritext));
  }

  private Cell getSjukskrivningFilterCell(PrintSjukfallRequest printRequest) {
    //sjukskrivningslängd
    String sjukskrivning = String
        .format(TEMPLATESTRING_SJUKSKRIVNINGSLANGD, printRequest.getLangdIntervall().getMin(), printRequest.getLangdIntervall().getMax());

    //slutdatum
    String slutdatum = getFilterDate(printRequest.getSlutdatumIntervall());

    //aldersspann
    String alderspann = String
        .format(TEMPLATESTRING_ALDERSINTERVALL, printRequest.getAldersIntervall().getMin(), printRequest.getAldersIntervall().getMax());

    return buildFilterCellMulti(false,
        Arrays.asList(FILTER_TITLE_VALD_SJUKSKRIVNINGSLANGD, FILTER_TITLE_VALD_SLUTDATUM, FILTER_TITLE_VALD_ALDER),
        Arrays.asList(sjukskrivning, slutdatum, alderspann));
  }

  private Cell getLakareFilterCell(PrintSjukfallRequest printRequest, RehabstodUser user) {

    final List<String> lakare = printRequest.getLakare() != null ? printRequest.getLakare()
        : Arrays.asList(user.getUrval() == Urval.ISSUED_BY_ME ? user.getNamn() : SELECTION_VALUE_ALLA);

    List<String> truncated = lakare.stream().map(name -> ellipsize(name, MAXLENGTH_LAKARNAMN)).collect(Collectors.toList());
    return buildFilterCell(false, FILTER_TITLE_VALDA_LAKARE, truncated);
  }

  private Cell getDiagnosFilterCell(PrintSjukfallRequest printRequest) {
    final List<String> diagnoses = printRequest.getDiagnosGrupper() != null ? printRequest.getDiagnosGrupper().stream()
        .map(dg -> getDiagnosKapitelDisplayValue(dg)).collect(Collectors.toList()) : Arrays.asList(NO_FILTER_VALUES_SELECTED_PLACEHOLDER);

    return buildFilterCell(true, FILTER_TITLE_VALDA_DIAGNOSER, diagnoses);
  }


  private String getDiagnosKapitelDisplayValue(String diagnosKapitel) {
    StringBuilder b = new StringBuilder(diagnosKapitel);
    if (b.length() > 0) {
      b.append(": ");
    }
    b.append(diagnosKapitelService.getDiagnosKapitel(diagnosKapitel).getName());

    return ellipsize(b.toString(), MAXLENGTH_FILTERDIAGNOS);
  }
}
