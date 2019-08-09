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
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.BlockElement;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.property.VerticalAlignment;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;
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

  // In millimeters
  public static final float PAGE_MARGIN_TOP = 10f;
  public static final float PAGE_MARGIN_RIGHT = 6.5f;
  public static final float PAGE_MARGIN_BOTTOM = 6.5f;
  public static final float PAGE_MARGIN_LEFT = 6.5f;
  private static final float FOOTER_FONT_SIZE = 6.0f;
  private static final Color TABLE_HEADER_BASE_COLOR = new DeviceRgb(70, 87, 97);
  private static final Color TABLE_EVEN_ROW_COLOR = new DeviceRgb(255, 255, 255);
  private static final Color TABLE_ODD_ROW_COLOR = new DeviceRgb(220, 220, 220);
  private static final String LOGO_PATH = "pdf-assets/rehab_pdf_logo.png";
  private static final String REGULAR_UNICODE_CAPABLE_FONT_PATH = "/pdf-assets/FreeSans.ttf";
  private static final String BOLD_UNICODE_CAPABLE_FONT_PATH = "/pdf-assets/FreeSansBold.ttf";
  private static final int ELLIPSIZE_AT_LIMIT = 20;
  private static final int ELLIPSIZE_AT_LIMIT_ANONYMOUS = 40;
  private static final String ELLIPSIZE_SUFFIX = "...";
  private static final Color FILTER_TABLE_BACKGROUND_COLOR = new DeviceRgb(0xEF, 0xEF, 0xEF);
  private static final float FILTER_TABLE_MIN_HEIGHT = 25f;
  private static final Border TABLE_SEPARATOR_BORDER = new SolidBorder(new DeviceRgb(0x99, 0x99, 0x99), 1);
  private static final String NO_FILTER_VALUES_SELECTED_PLACEHOLDER = "-";
  private static final float FILTER_HEADER_FONTSIZE = 7.5f;
  private static final Color PAGE_HEADER_FONTCOLOR = new DeviceRgb(0x00, 0x83, 0x91);
  private static final float DEFAULT_FONT_SIZE = 7.5f;
  private static final Color DEFAULT_FONT_COLOR = new DeviceRgb(0x00, 0x0, 0x0);
  private static final float PAGE_HEADER_FONTSIZE = 7.5f;
  private PathMatchingResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

  private PdfFont regularFont;
  private PdfFont boldFont;
  private PdfImageXObject logoImage;
  private Style defaultParagraphStyle;
  private Style pageHeaderStyle;
  private Style filterCellHeaderParagraphStyle;
  private Style filterCellStyle;
  private Style filterCellStyleBold;

  private static Cell aCell() {
    return new Cell().setBorder(Border.NO_BORDER);
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

  @Override
  @PrometheusTimeMethod
  public byte[] export(List<SjukfallEnhet> sjukfallList, PrintSjukfallRequest printSjukfallRequest, RehabstodUser user, int total) {

    ByteArrayOutputStream bos = new ByteArrayOutputStream();

    try {

      // Load icons for observandum
      this.logoImage = new PdfImageXObject(
          ImageDataFactory.create(IOUtils.toByteArray(new ClassPathResource(LOGO_PATH).getInputStream())));

      // Initialize PDF writer
      PdfWriter writer = new PdfWriter(bos);

      setupStyles();
      PdfDocument pdf = new PdfDocument(writer);
      // Initialize document
      Document document = new Document(pdf, PageSize.A4.rotate(), false);
      document.setMargins(
          millimetersToPoints(PAGE_MARGIN_TOP),
          millimetersToPoints(PAGE_MARGIN_RIGHT),
          millimetersToPoints(PAGE_MARGIN_BOTTOM),
          millimetersToPoints(PAGE_MARGIN_LEFT));

      document.setFont(regularFont)
          .setFontSize(DEFAULT_FONT_SIZE);

      // Initialize event handlers for header, footer etc.
      pdf.addEventHandler(PdfDocumentEvent.END_PAGE,
          new HeaderEventHandler(logoImage, user.getNamn(), user.getValdVardenhet().getNamn(), FOOTER_FONT_SIZE));

      // On first page, add filter settings
      document.add(buildFilterSettings(printSjukfallRequest, user));

      // Add table with all sjukfall (could span several pages)
      document
          .add(createSjukfallTable(sjukfallList, user.getUrval(), total, printSjukfallRequest, isSrsFeatureActive(user)));

      //We now know how many pages the document will have, so we can write that now
      writePageNumbers(pdf, document);

      // Finish off by closing the document (will invoke the event handlers)
      document.close();

    } catch (IOException | RuntimeException e) {
      throw new PdfExportServiceException("Failed to create PDF export!", e);
    }

    return bos.toByteArray();
  }

  private IBlockElement createSjukfallTable(List<SjukfallEnhet> sjukfallList, Urval urval, int total,
      PrintSjukfallRequest printSjukfallRequest,
      boolean srsFeatureActive) {

    Div root = new Div().setFillAvailableArea(true);
    root.add(new Paragraph(SAMTLIGA_PAGAENDE_FALL_PA_ENHETEN).addStyle(pageHeaderStyle));

    Table tableAbove = new Table(2);
    tableAbove.setWidth(UnitValue.createPercentValue(100f));

    Cell showingCell = aCell().
        add(new Paragraph(String.format("Antal sjukfall på enheten: %d              Presenterade sjukfall: %d", total, sjukfallList.size()))
            .addStyle(pageHeaderStyle));
    Cell sortedByCell = aCell().
        add(new Paragraph(getSorteringDesc(printSjukfallRequest.getSortering()))
            .addStyle(pageHeaderStyle)).setTextAlignment(TextAlignment.RIGHT);

    tableAbove.addCell(showingCell);
    tableAbove.addCell(sortedByCell);
    root.add(tableAbove);

    Table t = new Table(5)
        .setWidth(UnitValue.createPercentValue(100f));
    for (int i = 0; i < sjukfallList.size(); i++) {
      SjukfallEnhet sf = sjukfallList.get(i);

      Cell c1 = aSjukFallCell(13f).setBorderLeft(TABLE_SEPARATOR_BORDER)
          .add(buildSjukfallCellTable(
              Arrays.asList("#", "Personnr", "Ålder"),
              Arrays.asList(
                  aParagraph(String.valueOf(i + 1)),
                  aParagraph(sf.getPatient().getId()),
                  aParagraph(String.valueOf(sf.getPatient().getAlder()) + " år"))));

      Cell c2 = aSjukFallCell(30f)
          .add(buildSjukfallCellTable(
              Arrays.asList("Namn", "Kön", "Diagnoser"),
              Arrays.asList(
                  aParagraph(sf.getPatient().getNamn()),
                  aParagraph(sf.getPatient().getKon().getDescription()),
                  aParagraph(getCompoundDiagnoseText(sf)))));

      Cell c3 = aSjukFallCell(15f)
          .add(buildSjukfallCellTable(
              Arrays.asList("Startdatum", "Slutdatum", "Längd"),
              Arrays.asList(
                  aParagraph(YearMonthDateFormatter.print(sf.getStart())),
                  aParagraph(YearMonthDateFormatter.print(sf.getSlut())),
                  aParagraph(String.format(FORMAT_ANTAL_DAGAR, sf.getDagar())))));

      Cell c4 = aSjukFallCell(22)
          .add(buildSjukfallCellTable(
              Arrays.asList("Antal", "Grad", "Komplettering"),
              Arrays.asList(
                  aParagraph(String.valueOf(sf.getIntyg())),
                  getGrader(sf),
                  aParagraph(String.valueOf(sf.getObesvaradeKompl())))));
      Cell c5 = aSjukFallCell(20).setBorderRight(TABLE_SEPARATOR_BORDER)
          .add(buildSjukfallCellTable(Arrays.asList("Läkare"),
              Arrays.asList(
                  aParagraph(ellipsize(sf.getLakare().getNamn(), 30)))));

      t.addCell(c1);
      t.addCell(c2);
      t.addCell(c3);
      t.addCell(c4);
      t.addCell(c5);
    }

    root.add(t);

    return root;


  }

  private String getCompoundDiagnoseText(SjukfallEnhet sf) {
    String dignosKodSpace = sf.getDiagnos().getKod() + " ";

    StringBuilder b = new StringBuilder();
    b.append(dignosKodSpace);
    final String bidiagnoser = diagnoseListToString(sf.getBiDiagnoser());
    b.append(ellipsize(sf.getDiagnos().getBeskrivning(), 40 - bidiagnoser.length()));
    b.append(bidiagnoser);
    return b.toString();
  }

  private Paragraph getGrader(SjukfallEnhet is) {
    boolean first = true;
    Paragraph grader = new Paragraph();
    for (Integer grad : is.getGrader()) {
      if (!first) {
        grader.add(new Text(UNICODE_RIGHT_ARROW_SYMBOL + " ").addStyle(filterCellStyle));
      }
      grader.add(new Text(grad.toString() + "% ").addStyle(grad == is.getAktivGrad() ? filterCellStyleBold : filterCellStyle));
      first = false;
    }
    return grader;
  }

  private Table buildSjukfallCellTable(List<String> headerTexts, List<Paragraph> values) {

    Table table = new Table(2).setKeepTogether(true);
    table.setBorder(Border.NO_BORDER);

    for (int i = 0; i < headerTexts.size(); i++) {
      Cell header = aCell().add(new Paragraph(headerTexts.get(i)).addStyle(filterCellHeaderParagraphStyle)).addStyle(filterCellStyle);
      Cell value = aCell().add(values.get(i)).addStyle(filterCellStyle);
      table.addCell(header).addCell(value);
    }
    return table;
  }

  private void writePageNumbers(PdfDocument pdf, Document document) {
    int n = pdf.getNumberOfPages();
    Paragraph footer;
    for (int page = 1; page <= n; page++) {
      footer = new Paragraph(String.format("Sida %s (%s)", page, n));
      footer.setFont(regularFont).setFontSize(FOOTER_FONT_SIZE);
      document.showTextAligned(footer, pdf.getPage(page).getPageSize().getWidth() / 2, millimetersToPoints(PAGE_MARGIN_BOTTOM), page,
          TextAlignment.CENTER, VerticalAlignment.TOP, 0);
    }
  }

  private BlockElement buildFilterSettings(PrintSjukfallRequest printRequest, RehabstodUser user) {
    String maxGlapp = user.getPreferences().get(Preference.MAX_ANTAL_DAGAR_MELLAN_INTYG);

    Table table = new Table(5)
        .setWidth(UnitValue.createPercentValue(100f))
        .setBackgroundColor(FILTER_TABLE_BACKGROUND_COLOR)
        .setMinHeight(millimetersToPoints(FILTER_TABLE_MIN_HEIGHT))
        .setPadding(millimetersToPoints(2.5f))
        .setMarginTop(10f)
        .setBorder(Border.NO_BORDER);

    Cell titleCell = new Cell(1, 5)
        .addStyle(pageHeaderStyle)
        .setBorder(Border.NO_BORDER)
        .add(new Paragraph("Valda filter och sjukfallsinställningar").addStyle(pageHeaderStyle));
    table.addCell(titleCell);

    table.addCell(getDiagnosFilterCell(printRequest));
    table.addCell(getLakareFilterCell(printRequest, user));
    table.addCell(getSjukskrivningFilterCell(printRequest));
    table.addCell(getKompletteringFilterCell(printRequest));
    table.addCell(buildFilterCell(false, MAXANTAL_DAGAR_UPPEHALL_MELLAN_INTYG, Arrays.asList(String.format("%s dagar", maxGlapp))));

    return table;

  }

  private Cell getKompletteringFilterCell(PrintSjukfallRequest printRequest) {
    //komplettering
    String komplettering = getKompletteringFilterDisplayValue(printRequest.getKomplettering());

    //visa patientuppgifter
    String patientuppgifter = printRequest.isShowPatientId() ? " Ja" : " Nej";

    //fritext
    String fritext =
        StringUtil.isNullOrEmpty(printRequest.getFritext()) ? NO_FILTER_VALUES_SELECTED_PLACEHOLDER
            : ellipsize(printRequest.getFritext(), 20);

    return buildFilterCellMulti(false,
        Arrays.asList(FILTER_TITLE_KOMPLETTERINGSSTATUS, FILTER_TITLE_VISAPATIENTUPPGIFTER, FILTER_TITLE_FRITEXTFILTER),
        Arrays.asList(komplettering, patientuppgifter, fritext));
  }

  private Cell getSjukskrivningFilterCell(PrintSjukfallRequest printRequest) {
    //sjukskrivningslängd
    String sjukskrivning = String
        .format("Mellan %s och %s dagar", printRequest.getLangdIntervall().getMin(), printRequest.getLangdIntervall().getMax());

    //slutdatum
    String slutdatum = getFilterDate(printRequest.getSlutdatumIntervall());

    //aldersspann
    String alderspann = String
        .format("Mellan %s och %s år", printRequest.getAldersIntervall().getMin(), printRequest.getAldersIntervall().getMax());

    return buildFilterCellMulti(false,
        Arrays.asList(FILTER_TITLE_VALD_SJUKSKRIVNINGSLANGD, FILTER_TITLE_VALD_SLUTDATUM, FILTER_TITLE_VALD_ALDER),
        Arrays.asList(sjukskrivning, slutdatum, alderspann));
  }

  private Cell getDiagnosFilterCell(PrintSjukfallRequest printRequest) {
    final List<String> diagnoses = printRequest.getDiagnosGrupper() != null ? printRequest.getDiagnosGrupper().stream()
        .map(dg -> getDiagnosKapitelDisplayValue(dg)).collect(Collectors.toList()) : Arrays.asList(NO_FILTER_VALUES_SELECTED_PLACEHOLDER);

    return buildFilterCell(true, FILTER_TITLE_VALDA_DIAGNOSER, diagnoses);
  }

  private Cell getLakareFilterCell(PrintSjukfallRequest printRequest, RehabstodUser user) {

    final List<String> lakare = printRequest.getLakare() != null ? printRequest.getLakare()
        : Arrays.asList(user.getUrval() == Urval.ISSUED_BY_ME ? user.getNamn() : SELECTION_VALUE_ALLA);

    return buildFilterCell(false, FILTER_TITLE_VALDA_LAKARE, lakare);
  }

  private String getDiagnosKapitelDisplayValue(String diagnosKapitel) {
    StringBuilder b = new StringBuilder(diagnosKapitel);
    if (b.length() > 0) {
      b.append(": ");
    }
    b.append(diagnosKapitelService.getDiagnosKapitel(diagnosKapitel).getName());

    return ellipsize(b.toString(), 50);
  }

  private String ellipsize(String value, int maxlength) {
    if (value != null && value.length() > maxlength) {
      return value.substring(0, maxlength) + ELLIPSIZE_SUFFIX;
    } else {
      return value;
    }
  }

  private Cell buildFilterCell(boolean isFirstCell, String headerText, List<String> values) {
    Cell cell = getFilterCell(isFirstCell);
    Table table = new Table(1);
    table.setBorder(Border.NO_BORDER);

    Paragraph headerParagraph = new Paragraph(headerText);
    headerParagraph.addStyle(filterCellHeaderParagraphStyle);
    Cell header = new Cell().add(headerParagraph).addStyle(filterCellStyle);
    table.addCell(header);
    values.forEach(value -> table.addCell(new Cell().add(new Paragraph(value).addStyle(defaultParagraphStyle)).addStyle(filterCellStyle)));

    cell.add(table);

    return cell;
  }


  private Cell buildFilterCellMulti(boolean isFirstCell, List<String> headerTexts, List<String> values) {
    Cell cell = getFilterCell(isFirstCell);

    Table table = new Table(2);
    table.setBorder(Border.NO_BORDER);

    for (int i = 0; i < headerTexts.size(); i++) {
      Cell header = new Cell().add(new Paragraph(headerTexts.get(i)).addStyle(filterCellHeaderParagraphStyle)).addStyle(filterCellStyle);
      Cell value = new Cell().add(new Paragraph(values.get(i))).addStyle(filterCellStyle);
      table.addCell(header).addCell(value);
    }

    cell.add(table);

    return cell;
  }


  private Cell getFilterCell(boolean isFirstCell) {
    return isFirstCell ? aCell().setBorderRight(TABLE_SEPARATOR_BORDER) : aCell().setBorderLeft(TABLE_SEPARATOR_BORDER);
  }

  private String getSorteringDesc(Sortering sortering) {
    if (sortering == null || StringUtil.isNullOrEmpty(sortering.getKolumn())) {
      return "(Ingen sortering vald)";
    }

    String template = "Tabellen är sorterad enligt %s i %s ordning";
    return String.format(template, sortering.getKolumn(), sortering.getOrder());

  }

  /*
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
        tempHeaders.add(1.5f); // Kompletteringsstatus
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
        addCell(table, TABLEHEADER_KOMPLETTERINGSSTATUS, PdfExportConstants.TABLE_HEADER_FONT);
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
          addCell(table, getKompletteringStatusFormat(is.getObesvaradeKompl()));
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


    */
  private void setupStyles() {
    try {
      this.regularFont = PdfFontFactory
          .createFont(IOUtils.toByteArray(new ClassPathResource(REGULAR_UNICODE_CAPABLE_FONT_PATH).getInputStream()), IDENTITY_H,
              true);
      this.boldFont = PdfFontFactory
          .createFont(IOUtils.toByteArray(new ClassPathResource(BOLD_UNICODE_CAPABLE_FONT_PATH).getInputStream()), IDENTITY_H,
              true);

      this.defaultParagraphStyle = new Style()
          .setFont(regularFont)
          .setFontSize(DEFAULT_FONT_SIZE)
          .setFontColor(DEFAULT_FONT_COLOR);

      this.pageHeaderStyle = new Style()
          .setFont(boldFont)
          .setFontSize(PAGE_HEADER_FONTSIZE)
          .setFontColor(PAGE_HEADER_FONTCOLOR);

      this.filterCellHeaderParagraphStyle = new Style()
          .setFont(boldFont)
          .setFontSize(FILTER_HEADER_FONTSIZE)
          .setFontColor(DEFAULT_FONT_COLOR);

      this.filterCellStyle = new Style()
          .setFont(regularFont)
          .setFontSize(DEFAULT_FONT_SIZE)
          .setFontColor(DEFAULT_FONT_COLOR)
          .setBorder(Border.NO_BORDER);

      this.filterCellStyleBold = new Style()
          .setFont(boldFont)
          .setFontSize(DEFAULT_FONT_SIZE)
          .setFontColor(DEFAULT_FONT_COLOR)
          .setBorder(Border.NO_BORDER);


    } catch (IOException e) {
      throw new IllegalArgumentException("Could not load font: " + e.getMessage());
    }
  }
}
