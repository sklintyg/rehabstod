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

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.layout.Style;

public class PdfStyle {

  private PdfFont boldFont;
  private PdfFont regularFont;
  private Style pageHeaderStyle;

  private Style defaultParagraphStyle;
  private Style cellHeaderParagraphStyle;
  private Style cellStyle;
  private Style cellStyleBold;

  public PdfStyle(PdfFont boldFont, PdfFont regularFont, Style pageHeaderStyle, Style defaultParagraphStyle,
      Style cellHeaderParagraphStyle, Style cellStyle, Style cellStyleBold) {
    this.boldFont = boldFont;
    this.regularFont = regularFont;
    this.pageHeaderStyle = pageHeaderStyle;
    this.defaultParagraphStyle = defaultParagraphStyle;
    this.cellHeaderParagraphStyle = cellHeaderParagraphStyle;
    this.cellStyle = cellStyle;
    this.cellStyleBold = cellStyleBold;
  }

  public PdfFont getBoldFont() {
    return boldFont;
  }

  public PdfFont getRegularFont() {
    return regularFont;
  }

  public Style getPageHeaderStyle() {
    return pageHeaderStyle;
  }

  public Style getDefaultParagraphStyle() {
    return defaultParagraphStyle;
  }

  public Style getCellHeaderParagraphStyle() {
    return cellHeaderParagraphStyle;
  }

  public Style getCellStyle() {
    return cellStyle;
  }

  public Style getCellStyleBold() {
    return cellStyleBold;
  }

  public static class PdfStyleBuilder {

    private PdfFont boldFont;
    private PdfFont regularFont;
    private Style pageHeaderStyle;
    private Style defaultParagraphStyle;
    private Style cellHeaderParagraphStyle;
    private Style cellStyle;
    private Style cellStyleBold;

    public PdfStyleBuilder setBoldFont(PdfFont boldFont) {
      this.boldFont = boldFont;
      return this;
    }

    public PdfStyleBuilder setRegularFont(PdfFont regularFont) {
      this.regularFont = regularFont;
      return this;
    }

    public PdfStyleBuilder setPageHeaderStyle(Style pageHeaderStyle) {
      this.pageHeaderStyle = pageHeaderStyle;
      return this;
    }

    public PdfStyleBuilder setDefaultParagraphStyle(Style defaultParagraphStyle) {
      this.defaultParagraphStyle = defaultParagraphStyle;
      return this;
    }

    public PdfStyleBuilder setCellHeaderParagraphStyle(Style cellHeaderParagraphStyle) {
      this.cellHeaderParagraphStyle = cellHeaderParagraphStyle;
      return this;
    }

    public PdfStyleBuilder setCellStyle(Style cellStyle) {
      this.cellStyle = cellStyle;
      return this;
    }

    public PdfStyleBuilder setCellStyleBold(Style cellStyleBold) {
      this.cellStyleBold = cellStyleBold;
      return this;
    }

    public PdfStyle createPdfStyle() {
      return new PdfStyle(boldFont, regularFont, pageHeaderStyle, defaultParagraphStyle, cellHeaderParagraphStyle, cellStyle,
          cellStyleBold);
    }
  }
}
