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

import static se.inera.intyg.rehabstod.service.export.BaseExportService.FORMAT_ANTAL_DAGAR;
import static se.inera.intyg.rehabstod.service.export.BaseExportService.UNICODE_RIGHT_ARROW_SYMBOL;
import static se.inera.intyg.rehabstod.service.export.BaseExportService.diagnoseListToString;
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
import java.util.Arrays;
import java.util.List;
import se.inera.intyg.rehabstod.common.util.YearMonthDateFormatter;
import se.inera.intyg.rehabstod.web.model.SjukfallEnhet;

/**
 * Encapsulates the logic of building the "sjukfall" table in the pdf.
 */
public class SjukfallTableBuilder {

  protected static final float TABLE_TOTAL_WIDTH = 100f;
  protected static final float WIDTH_COLUMN_1 = 13f;
  protected static final float WIDTH_COLUMN_2 = 30f;
  protected static final float WIDTH_COLUMN_3 = 15f;
  protected static final float WIDTH_COLUMN_4 = 22f;
  protected static final float WIDTH_COLUMN_5 = 20f;
  protected static final int MAXLENGTH_PATIENT_NAMN = 40;
  protected static final int MAXLENGTH_LAKARE_NAMN = 30;
  protected static final int MAXLENGTH_DIAGNOS = 40;
  protected static final int TOTAL_NUM_COLUMNS = 5;
  private PdfStyle style;

  public SjukfallTableBuilder(PdfStyle style) {
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

  public BlockElement buildsjukfallTable(List<SjukfallEnhet> sjukfallList) {
    Table table = new Table(TOTAL_NUM_COLUMNS)
        .setWidth(UnitValue.createPercentValue(TABLE_TOTAL_WIDTH));
    for (int i = 0; i < sjukfallList.size(); i++) {
      SjukfallEnhet sf = sjukfallList.get(i);

      Cell c1 = aSjukFallCell(WIDTH_COLUMN_1).setBorderLeft(TABLE_SEPARATOR_BORDER)
          .add(buildSjukfallCellTable(
              Arrays.asList("#", "Personnr", "Ålder"),
              Arrays.asList(
                  aParagraph(String.valueOf(i + 1)),
                  aParagraph(sf.getPatient().getId()),
                  aParagraph(String.valueOf(sf.getPatient().getAlder()) + " år"))));

      Cell c2 = aSjukFallCell(WIDTH_COLUMN_2)
          .add(buildSjukfallCellTable(
              Arrays.asList("Namn", "Kön", "Diagnoser"),
              Arrays.asList(
                  aParagraph(ellipsize(sf.getPatient().getNamn(), MAXLENGTH_PATIENT_NAMN)),
                  aParagraph(sf.getPatient().getKon().getDescription()),
                  aParagraph(getCompoundDiagnoseText(sf)))));

      Cell c3 = aSjukFallCell(WIDTH_COLUMN_3)
          .add(buildSjukfallCellTable(
              Arrays.asList("Startdatum", "Slutdatum", "Längd"),
              Arrays.asList(
                  aParagraph(YearMonthDateFormatter.print(sf.getStart())),
                  aParagraph(YearMonthDateFormatter.print(sf.getSlut())),
                  aParagraph(String.format(FORMAT_ANTAL_DAGAR, sf.getDagar())))));

      Cell c4 = aSjukFallCell(WIDTH_COLUMN_4)
          .add(buildSjukfallCellTable(
              Arrays.asList("Antal", "Grad", "Komplettering"),
              Arrays.asList(
                  aParagraph(String.valueOf(sf.getIntyg())),
                  getGrader(sf),
                  aParagraph(String.valueOf(sf.getObesvaradeKompl())))));
      
      Cell c5 = aSjukFallCell(WIDTH_COLUMN_5).setBorderRight(TABLE_SEPARATOR_BORDER)
          .add(buildSjukfallCellTable(Arrays.asList("Läkare"),
              Arrays.asList(
                  aParagraph(ellipsize(sf.getLakare().getNamn(), MAXLENGTH_LAKARE_NAMN)))));

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

  private Table buildSjukfallCellTable(List<String> headerTexts, List<Paragraph> values) {

    Table table = new Table(2).setKeepTogether(true);
    table.setBorder(Border.NO_BORDER);

    for (int i = 0; i < headerTexts.size(); i++) {
      Cell header = aCell().add(new Paragraph(headerTexts.get(i)).addStyle(style.getCellHeaderParagraphStyle())).addStyle(
          style.getCellStyle());
      Cell value = aCell().add(values.get(i)).addStyle(style.getCellStyle());
      table.addCell(header).addCell(value);
    }
    return table;
  }
}
