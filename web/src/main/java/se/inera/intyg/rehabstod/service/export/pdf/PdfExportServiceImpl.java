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

import static com.itextpdf.io.font.PdfEncodings.IDENTITY_H;
import static se.inera.intyg.rehabstod.service.export.pdf.PdfConstants.TABLE_TITLE_MINA_SJUKFALL;
import static se.inera.intyg.rehabstod.service.export.pdf.PdfConstants.TABLE_TITLE_PA_ENHETEN;
import static se.inera.intyg.rehabstod.service.export.pdf.PdfConstants.TABLE_TITLE_PREFIX;
import static se.inera.intyg.rehabstod.service.export.pdf.PdfUtil.aCell;
import static se.inera.intyg.rehabstod.service.export.pdf.PdfUtil.millimetersToPoints;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.property.VerticalAlignment;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.monitoring.annotation.PrometheusTimeMethod;
import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.common.util.StringUtil;
import se.inera.intyg.rehabstod.service.Urval;
import se.inera.intyg.rehabstod.service.export.BaseExportService;
import se.inera.intyg.rehabstod.service.export.pdf.PdfStyle.PdfStyleBuilder;
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

  // In millimeters
  public static final float PAGE_MARGIN_TOP = 10f;
  public static final float PAGE_MARGIN_RIGHT = 6.5f;
  public static final float PAGE_MARGIN_BOTTOM = 6.5f;
  public static final float PAGE_MARGIN_LEFT = 6.5f;
  protected static final String TEMPLATESTRING_TABLE_METADATA = "Antal sjukfall på enheten: %d              Presenterade sjukfall: %d";
  protected static final String TEMPLATESTRING_TABLE_SORTORDER = "Tabellen är sorterad enligt %s i %s ordning";
  private static final float FOOTER_FONT_SIZE = 6.0f;
  private static final String LOGO_PATH = "pdf-assets/rehab_pdf_logo.png";
  private static final String REGULAR_UNICODE_CAPABLE_FONT_PATH = "/pdf-assets/FreeSans.ttf";
  private static final String BOLD_UNICODE_CAPABLE_FONT_PATH = "/pdf-assets/FreeSansBold.ttf";
  private static final float FILTER_HEADER_FONTSIZE = 7.5f;
  private static final Color PAGE_HEADER_FONTCOLOR = new DeviceRgb(0x00, 0x83, 0x91);
  private static final float DEFAULT_FONT_SIZE = 7.5f;
  private static final Color DEFAULT_FONT_COLOR = new DeviceRgb(0x00, 0x0, 0x0);
  private static final float PAGE_HEADER_FONTSIZE = 7.5f;
  private static final float TABLE_MARGIN_TOP = millimetersToPoints(6.5f);

  private PdfImageXObject logoImage;

  private PdfStyle style;


  @Override
  @PrometheusTimeMethod
  public byte[] export(List<SjukfallEnhet> sjukfallList, PrintSjukfallRequest printSjukfallRequest, RehabstodUser user, int total) {

    ByteArrayOutputStream bos = new ByteArrayOutputStream();

    try {
      initFontStyles();

      // Load icons for observandum
      this.logoImage = new PdfImageXObject(
          ImageDataFactory.create(IOUtils.toByteArray(new ClassPathResource(LOGO_PATH).getInputStream())));

      // Initialize PDF writer
      PdfWriter writer = new PdfWriter(bos);

      PdfDocument pdf = new PdfDocument(writer);
      // Initialize document
      Document document = new Document(pdf, PageSize.A4.rotate(), false);
      document.setMargins(
          millimetersToPoints(PAGE_MARGIN_TOP),
          millimetersToPoints(PAGE_MARGIN_RIGHT),
          millimetersToPoints(PAGE_MARGIN_BOTTOM),
          millimetersToPoints(PAGE_MARGIN_LEFT));

      document.setFont(style.getRegularFont())
          .setFontSize(DEFAULT_FONT_SIZE);

      // Initialize event handlers for header, footer etc.
      pdf.addEventHandler(PdfDocumentEvent.END_PAGE,
          new HeaderEventHandler(logoImage, user.getNamn(), user.getValdVardenhet().getNamn(), FOOTER_FONT_SIZE));

      // On first page, add filter settings
      FilterTableBuilder filterTableBuilder = new FilterTableBuilder(diagnosKapitelService, style);
      document.add(filterTableBuilder.buildFilterSettings(printSjukfallRequest, user));

      // Add table with all sjukfall (could span several pages)
      document
          .add(createSjukfallTable(sjukfallList, user, total, printSjukfallRequest, isSrsFeatureActive(user)));

      //We now know how many pages the document will have, so we can write that now
      writePageNumbers(pdf, document);

      // Finish off by closing the document (will invoke the event handlers)
      document.close();

    } catch (IOException | RuntimeException e) {
      throw new PdfExportServiceException("Failed to create PDF export!", e);
    }

    return bos.toByteArray();
  }

  private IBlockElement createSjukfallTable(List<SjukfallEnhet> sjukfallList, RehabstodUser user, int total,
      PrintSjukfallRequest printSjukfallRequest,
      boolean srsFeatureActive) {

    Div root = new Div().setFillAvailableArea(true);
    StringBuilder tableTitle = new StringBuilder(TABLE_TITLE_PREFIX);
    tableTitle.append(user.getUrval() == Urval.ALL ? TABLE_TITLE_PA_ENHETEN : TABLE_TITLE_MINA_SJUKFALL);

    root.add(new Paragraph(tableTitle.toString()).addStyle(style.getPageHeaderStyle()).setMarginTop(TABLE_MARGIN_TOP));

    Table tableAbove = new Table(2);
    tableAbove.setWidth(UnitValue.createPercentValue(100f));

    Cell showingCell = aCell().
        add(new Paragraph(String.format(TEMPLATESTRING_TABLE_METADATA, total, sjukfallList.size()))
            .addStyle(style.getPageHeaderStyle()));
    Cell sortedByCell = aCell().
        add(new Paragraph(getSorteringDesc(printSjukfallRequest.getSortering()))
            .addStyle(style.getPageHeaderStyle())).setTextAlignment(TextAlignment.RIGHT);

    tableAbove.addCell(showingCell);
    tableAbove.addCell(sortedByCell);
    root.add(tableAbove);

    SjukfallTableBuilder sjukfallTableBuilder = new SjukfallTableBuilder(style);
    root.add(sjukfallTableBuilder.buildsjukfallTable(sjukfallList, user, printSjukfallRequest, srsFeatureActive));

    return root;


  }


  private void writePageNumbers(PdfDocument pdf, Document document) {
    int n = pdf.getNumberOfPages();
    Paragraph footer;
    for (int page = 1; page <= n; page++) {
      footer = new Paragraph(String.format("Sida %s (%s)", page, n));
      footer.setFont(style.getRegularFont()).setFontSize(FOOTER_FONT_SIZE);
      document.showTextAligned(footer, pdf.getPage(page).getPageSize().getWidth() / 2, millimetersToPoints(PAGE_MARGIN_BOTTOM), page,
          TextAlignment.CENTER, VerticalAlignment.TOP, 0);
    }
  }


  private String getSorteringDesc(Sortering sortering) {
    if (sortering == null || StringUtil.isNullOrEmpty(sortering.getKolumn())) {
      return "(Ingen sortering vald)";
    }

    return String.format(TEMPLATESTRING_TABLE_SORTORDER, sortering.getKolumn(), sortering.getOrder().toLowerCase());

  }

  private void initFontStyles() {
    try {
      PdfFont regularFont = PdfFontFactory
          .createFont(IOUtils.toByteArray(new ClassPathResource(REGULAR_UNICODE_CAPABLE_FONT_PATH).getInputStream()), IDENTITY_H,
              true);
      PdfFont boldFont = PdfFontFactory
          .createFont(IOUtils.toByteArray(new ClassPathResource(BOLD_UNICODE_CAPABLE_FONT_PATH).getInputStream()), IDENTITY_H,
              true);

      Style defaultParagraphStyle = new Style()
          .setFont(regularFont)
          .setFontSize(DEFAULT_FONT_SIZE)
          .setFontColor(DEFAULT_FONT_COLOR);

      Style pageHeaderStyle = new Style()
          .setFont(boldFont)
          .setFontSize(PAGE_HEADER_FONTSIZE)
          .setFontColor(PAGE_HEADER_FONTCOLOR);

      Style cellHeaderParagraphStyle = new Style()
          .setFont(boldFont)
          .setFontSize(FILTER_HEADER_FONTSIZE)
          .setFontColor(DEFAULT_FONT_COLOR);

      Style cellStyle = new Style()
          .setFont(regularFont)
          .setFontSize(DEFAULT_FONT_SIZE)
          .setFontColor(DEFAULT_FONT_COLOR)
          .setBorder(Border.NO_BORDER);

      Style cellStyleItalic = new Style()
          .setFont(regularFont)
          .setItalic()
          .setFontSize(DEFAULT_FONT_SIZE)
          .setFontColor(DEFAULT_FONT_COLOR)
          .setBorder(Border.NO_BORDER);

      Style cellStyleBold = new Style()
          .setFont(boldFont)
          .setFontSize(DEFAULT_FONT_SIZE)
          .setFontColor(DEFAULT_FONT_COLOR)
          .setBorder(Border.NO_BORDER);

      this.style = PdfStyleBuilder.aPdfStyle()
          .withDefaultParagraphStyle(defaultParagraphStyle)
          .withCellStyle(cellStyle)
          .withPageHeaderStyle(pageHeaderStyle)
          .withCellHeaderParagraphStyle(cellHeaderParagraphStyle)
          .withCellStyleBold(cellStyleBold)
          .withCellStyle(cellStyle)
          .withCellStyleItalic(cellStyleItalic)
          .withBoldFont(boldFont)
          .withRegularFont(regularFont)
          .build();

    } catch (IOException e) {
      throw new IllegalArgumentException("Could not load fonts / styles: " + e.getMessage());
    }
  }
}
