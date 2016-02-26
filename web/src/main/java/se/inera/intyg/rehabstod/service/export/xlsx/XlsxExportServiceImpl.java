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
package se.inera.intyg.rehabstod.service.export.xlsx;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.inera.intyg.rehabstod.service.Urval;
import se.inera.intyg.rehabstod.service.export.BaseExportService;
import se.inera.intyg.rehabstod.service.user.UserService;
import se.inera.intyg.rehabstod.web.controller.api.dto.PrintSjukfallRequest;
import se.inera.intyg.rehabstod.web.model.InternalSjukfall;
import se.inera.intyg.rehabstod.web.model.Patient;
import se.inera.intyg.rehabstod.web.model.Sjukfall;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by eriklupander on 2016-02-23.
 */
@Service
public class XlsxExportServiceImpl extends BaseExportService implements XlsxExportService {

    private XSSFCellStyle boldStyle;
    private XSSFCellStyle filterMainHeaderStyle;
    private XSSFCellStyle filterHeaderStyle;
    private XSSFCellStyle filterTextStyle;

    private XSSFCellStyle stripedDarker;
    private XSSFCellStyle stripedLighter;

    private XSSFFont boldFont16;
    private XSSFFont defaultFont12;
    private XSSFFont boldFont12;
    private XSSFFont defaultFont11;
    private XSSFFont boldFont11;

    private static final String[] HEADERS = new String[] { "#", "Personnummer", "Namn", "Kön", "Nuvarande diagnos",
            "Startdatum", "Slutdatum", "Sjukskrivningslängd", "Sjukskrivningsgrad", "Nuvarande läkare" };


    @Autowired
    UserService userService;

    @Override
    public byte[] export(List<InternalSjukfall> sjukfallList, PrintSjukfallRequest req, Urval urval) throws IOException {

        String sheetName = "Sjukskrivningar";

        XSSFWorkbook wb = new XSSFWorkbook();
        setupFonts(wb);
        XSSFSheet sheet = wb.createSheet(sheetName);

        int rowNumber = 0;

        // CHECKSTYLE:OFF MagicNumber
        // Start with 2 empty rows to make space for filter
        for (; rowNumber < 2; rowNumber++) {
            sheet.createRow(rowNumber);
        }

        addFilterMainHeader(sheet, rowNumber++, "Valda filter");
        addFilterHeader(sheet, rowNumber++, "Sjukskrivningslängd", req.getLangdIntervall().getMin() + " - " + req.getLangdIntervall().getMax() + " dagar");
        addFilterHeader(sheet, rowNumber++, "Läkare", urval == Urval.ALL ? toBulletList(req.getLakare()) : userService.getUser().getNamn());
        addFilterHeaderWithRichTextValue(sheet, rowNumber++, "Diagnoskapitel", diagnosKapitelFormat(req.getDiagnosGrupper()));
        addFilterHeader(sheet, rowNumber++, "Sökfilter", notEmpty(req) ? req.getFritext() : "-");
        rowNumber += 3;
        addFilterMainHeader(sheet, rowNumber++, "Sjukfallsinställning");
        addFilterHeader(sheet, rowNumber++, "Max dagar mellan intyg", req.getMaxIntygsGlapp() + " dagar");
        rowNumber += 3;
        addFilterMainHeader(sheet, rowNumber++, "Vald sortering");
        addFilterHeader(sheet, rowNumber++, "Kolumn", req.getSortering().getKolumn());
        addFilterHeader(sheet, rowNumber++, "Riktning", sortOrderToHumanReadable(req));
        rowNumber += 3;
        addFilterMainHeader(sheet, rowNumber++, "Sjukfallstabellen");
        addHeaderRow(sheet, rowNumber++, urval);
        addDataRows(sheet, rowNumber, sjukfallList, urval);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        wb.write(baos);
        return baos.toByteArray();
    }

    private XSSFRichTextString diagnosKapitelFormat(List<String> diagnosGrupper) {

        if (diagnosGrupper == null || diagnosGrupper.size() == 0) {
            return new XSSFRichTextString("Alla");
        }

        StringBuilder buf = new StringBuilder();
        List<Pair> boldIndicies = new ArrayList<>();
        int currentIndex = 0;
        for (String diagnosKapitel : diagnosGrupper) {
            boldIndicies.add(new Pair(currentIndex, currentIndex + diagnosKapitel.length() + 3));
            buf.append("* ").append(diagnosKapitel).append(": ");
            buf.append(diagnosKapitelService.getDiagnosKapitel(diagnosKapitel).getName());
            buf.append("\n");
            currentIndex = buf.length();
        }

        XSSFRichTextString richTextString = new XSSFRichTextString();
        richTextString.setString(buf.toString());
        richTextString.applyFont(defaultFont12);

        // Apply bold text for the text between boldIndicies values.
        boldIndicies.stream().forEach(pair -> richTextString.applyFont(pair.getI1(), pair.getI2(), defaultFont12));
        return richTextString;
    }

    private void addFilterHeader(XSSFSheet sheet, int rowIndex, String key, String value) {
        XSSFRow row = buildFilterTitleCell(sheet, rowIndex, key);

        XSSFCell cell2 = row.createCell(2);
        cell2.setCellValue(value);
        cell2.setCellStyle(filterTextStyle);
    }

    private XSSFRow buildFilterTitleCell(XSSFSheet sheet, int rowIndex, String key) {
        XSSFRow row = sheet.createRow(rowIndex);
        XSSFCell cell = row.createCell(1);
        cell.setCellValue(key);
        cell.setCellStyle(filterHeaderStyle);
        return row;
    }

    private void addFilterHeaderWithRichTextValue(XSSFSheet sheet, int rowIndex, String key, XSSFRichTextString value) {
        XSSFRow row = buildFilterTitleCell(sheet, rowIndex, key);

        XSSFCell cell2 = row.createCell(2);
        cell2.setCellValue(value);
        cell2.setCellStyle(filterTextStyle);
    }

    private void addFilterMainHeader(XSSFSheet sheet, int rowIndex, String value) {
        XSSFRow row = sheet.createRow(rowIndex);
        XSSFCell cell = row.createCell(1);
        cell.setCellValue(value);
        cell.setCellStyle(filterMainHeaderStyle);

        XSSFCell cell2 = row.createCell(2);
        cell2.setCellStyle(filterMainHeaderStyle);
    }

    private void addHeaderRow(XSSFSheet sheet, int rowIndex, Urval urval) {

        XSSFRow row = sheet.createRow(rowIndex);

        for (int a = 0; a < HEADERS.length; a++) {
            if (!(urval == Urval.ISSUED_BY_ME && a == HEADERS.length - 1)) {
                createHeaderCell(row, a, HEADERS[a]);
            }
        }

    }

    private void addDataRows(XSSFSheet sheet, int rowIndex, List<InternalSjukfall> sjukfallList, Urval urval) {
        for (int a = 0; a < sjukfallList.size(); a++) {
            XSSFRow row = sheet.createRow(rowIndex + a);
            Sjukfall sf = sjukfallList.get(a).getSjukfall();

            int colIndex = 0;
            createDataCell(row, colIndex++, "" + (a + 1));
            createRichTextDataCell(row, colIndex++, buildPersonnummerAndAgeRichText(sf.getPatient()));
            createDataCell(row, colIndex++, sf.getPatient().getNamn());
            createDataCell(row, colIndex++, buildKonName(sf.getPatient().getKon().name()));
            createDataCell(row, colIndex++, sf.getDiagnos().getKod());
            createDataCell(row, colIndex++, sf.getStart().toString("yyyy-MM-dd"));
            createDataCell(row, colIndex++, sf.getSlut().toString("yyyy-MM-dd"));
            createDataCell(row, colIndex++, "" + sf.getDagar() + " dagar (" + sf.getIntyg() + " intyg)");
            createRichTextDataCell(row, colIndex++, buildGraderRichText(sf));
            if (urval != Urval.ISSUED_BY_ME) {
                createDataCell(row, colIndex, sf.getLakare().getNamn());
            }
        }
        for (int a = 0; a < HEADERS.length; a++) {
            sheet.autoSizeColumn(a);
        }
        // Makes sure the "namn" column isn't excessively wide due to the filter.
        sheet.setColumnWidth(2, 7000);
    }



    private XSSFRichTextString buildPersonnummerAndAgeRichText(Patient patient) {
        String value = patient.getId() + " (" + patient.getAlder() + " år)";
        XSSFRichTextString richTextString = new XSSFRichTextString();
        richTextString.setString(value);
        richTextString.applyFont(defaultFont11);
        richTextString.applyFont(value.indexOf("(") + 1, value.lastIndexOf(")"), boldFont11);
        return richTextString;
    }

    private XSSFRichTextString buildGraderRichText(Sjukfall sf) {

        if (sf.getGrader() == null || sf.getGrader().size() == 0) {
            return new XSSFRichTextString();
        }

        StringBuilder buf = new StringBuilder();
        Pair aktivIndicies = null;
        int currentIndex = 0;
        for (Integer grad : sf.getGrader()) {
            if (grad == sf.getAktivGrad()) {
                aktivIndicies = new Pair(currentIndex, currentIndex + ("" + grad + "%").length());
            }
            buf.append("").append(grad).append("% ");
            currentIndex = buf.length();
        }
        buf.setLength(buf.length() - 1);

        XSSFRichTextString richTextString = new XSSFRichTextString();
        richTextString.setString(buf.toString());
        richTextString.applyFont(defaultFont11);
        if (aktivIndicies != null) {
            richTextString.applyFont(aktivIndicies.getI1(), aktivIndicies.getI2(), boldFont11);
        }
        return richTextString;
    }

    private void createDataCell(XSSFRow row, int colIndex, String value) {
        XSSFCell cell = row.createCell(colIndex);
        cell.setCellValue(value);
        cell.setCellStyle(row.getRowNum() % 2 == 0 ? stripedDarker : stripedLighter);
    }

    private void createRichTextDataCell(XSSFRow row, int colIndex, XSSFRichTextString value) {
        XSSFCell cell = row.createCell(colIndex);
        cell.setCellValue(value);
        cell.setCellStyle(row.getRowNum() % 2 == 0 ? stripedDarker : stripedLighter);
    }

    private void createHeaderCell(XSSFRow row, int colIndex, String value) {
        XSSFCell cell = row.createCell(colIndex);
        cell.setCellValue(value);
        cell.setCellStyle(boldStyle);
    }

    private void setupFonts(XSSFWorkbook wb) {
        boldFont16 = buildFont(wb, 16, "Helvetica", true, true);
        boldFont12 = buildFont(wb, 12, "Helvetica", true, false);
        defaultFont12 = buildFont(wb, 12, "Helvetica", false, false);
        boldFont11 = buildFont(wb, 11, "Helvetica", true, false);
        defaultFont11 = buildFont(wb, 11, "Helvetica", false, false);

        boldStyle = wb.createCellStyle();
        boldStyle.setFont(boldFont11);
        boldStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(40, 180, 196)));
        boldStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);

        stripedDarker = wb.createCellStyle();
        stripedDarker.setFillForegroundColor(new XSSFColor(new java.awt.Color(230, 230, 230)));
        stripedDarker.setFillPattern(CellStyle.SOLID_FOREGROUND);

        stripedLighter = wb.createCellStyle();
        stripedLighter.setFillForegroundColor(new XSSFColor(new java.awt.Color(244, 244, 244)));
        stripedLighter.setFillPattern(CellStyle.SOLID_FOREGROUND);

        filterTextStyle = wb.createCellStyle();
        filterTextStyle.setFont(defaultFont12);
        filterTextStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(240, 240, 240)));
        filterTextStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        filterTextStyle.setBorderBottom(CellStyle.BORDER_THIN);
        filterTextStyle.setBottomBorderColor(IndexedColors.WHITE.getIndex());
        filterTextStyle.setWrapText(true);

        filterHeaderStyle = wb.createCellStyle();
        filterHeaderStyle.setFont(boldFont12);
        filterHeaderStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(240, 240, 240)));
        filterHeaderStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        filterHeaderStyle.setBorderBottom(CellStyle.BORDER_THIN);
        filterHeaderStyle.setBottomBorderColor(IndexedColors.WHITE.getIndex());
        filterHeaderStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

        filterMainHeaderStyle = wb.createCellStyle();
        filterMainHeaderStyle.setFont(boldFont16);
        filterMainHeaderStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(240, 240, 240)));
        filterMainHeaderStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        filterMainHeaderStyle.setBorderBottom(CellStyle.BORDER_THIN);
        filterMainHeaderStyle.setBottomBorderColor(IndexedColors.WHITE.getIndex());


    }

    private XSSFFont buildFont(XSSFWorkbook wb, int heightInPoints, String fontName, boolean bold, boolean underline) {
        XSSFFont font = wb.createFont();
        font.setFontHeightInPoints((short) heightInPoints);
        font.setFontName(fontName);
        font.setColor(IndexedColors.BLACK.getIndex());
        font.setBold(bold);
        font.setBoldweight(bold ? Font.BOLDWEIGHT_BOLD : Font.BOLDWEIGHT_NORMAL);
        font.setUnderline(underline ? XSSFFont.U_SINGLE : XSSFFont.U_NONE);
        return font;
    }

    // CHECKSTYLE:ON MagicNumber

}
