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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import se.inera.intyg.rehabstod.service.Urval;
import se.inera.intyg.rehabstod.web.controller.api.dto.PrintSjukfallRequest;
import se.inera.intyg.rehabstod.web.model.InternalSjukfall;
import se.inera.intyg.rehabstod.web.model.Sjukfall;

/**
 * Created by eriklupander on 2016-02-23.
 */
@Service
public class XlsxExportServiceImpl implements XlsxExportService {

    public static final int DEFAULT_FONT_SIZE = 11;
    private XSSFCellStyle defaultStyle;
    private XSSFCellStyle boldStyle;
    private XSSFCellStyle filterMainHeaderStyle;
    private XSSFCellStyle filterHeaderStyle;
    private XSSFCellStyle filterTextStyle;

    private static final String[] HEADERS = new String[] { "#", "Person­nummer", "Namn", "Kön", "Nuvarande diagnos", "Startdatum", "Slutdatum", "Sjukskrivnings­längd",
            "Sjukskrivnings­grad", "Nuvarande läkare" };



    @Override
    public byte[] export(List<InternalSjukfall> sjukfallList, PrintSjukfallRequest req, Urval urval) throws IOException {

        String sheetName = "Sjukskrivningar";

        XSSFWorkbook wb = new XSSFWorkbook();
        setupFonts(wb);
        XSSFSheet sheet = wb.createSheet(sheetName);

        // CHECKSTYLE:OFF MagicNumber
        // Start with 2 empty rows to make space for filter
        for (int r = 0; r < 2; r++) {
            sheet.createRow(r);
        }

        addFilterMainHeader(sheet, 3, "Valda filter");
        addFilterHeader(sheet, 4, "Sjukskrivningslängd", req.getLangdIntervall().getMin() + "-" + req.getLangdIntervall().getMax() + " dagar");
        addFilterHeader(sheet, 5, "Läkare", toBulletList(req.getLakare()));
        addFilterHeader(sheet, 6, "Diagnoskapitel", toBulletList(req.getDiagnosGrupper()));
        addFilterHeader(sheet, 7, "Sökfilter", req.getFritext());

        addFilterMainHeader(sheet, 10, "Sjukfallsinställning");
        addFilterHeader(sheet, 11, "Max dagar mellan intyg", req.getMaxIntygsGlapp() + " dagar");

        addFilterMainHeader(sheet, 14, "Vald sortering");
        addFilterHeader(sheet, 15, "Kolumn", req.getSortering().getKolumn());
        addFilterHeader(sheet, 16, "Riktning", req.getSortering().getOrder());

        addFilterMainHeader(sheet, 19, "Sjukfallstabellen");
        addHeaderRow(sheet, 20, urval);
        addDataRows(sheet, 21, sjukfallList, urval);



        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        wb.write(baos);
        return baos.toByteArray();
    }

    private String toBulletList(List<String> lakare) {
        return lakare.stream().map(s -> "* " + s + "\n").collect(Collectors.joining());
    }

    private void addFilterHeader(XSSFSheet sheet, int rowIndex, String key, String value) {
        XSSFRow row = sheet.createRow(rowIndex);
        XSSFCell cell = row.createCell(1);
        cell.setCellValue(key);
        cell.setCellStyle(filterHeaderStyle);

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
            createDataCell(row, colIndex++, sf.getPatient().getId());
            createDataCell(row, colIndex++, sf.getPatient().getNamn());
            createDataCell(row, colIndex++, sf.getPatient().getKon().name());
            createDataCell(row, colIndex++, sf.getDiagnos().getKod());
            createDataCell(row, colIndex++, sf.getStart().toString("yyyy-MM-dd"));
            createDataCell(row, colIndex++, sf.getSlut().toString("yyyy-MM-dd"));
            createDataCell(row, colIndex++, "" + sf.getDagar());
            createDataCell(row, colIndex++, "" + sf.getAktivGrad());
            if (urval != Urval.ISSUED_BY_ME) {
                createDataCell(row, colIndex, sf.getLakare().getNamn());
            }
        }
        for (int a = 0; a < HEADERS.length; a++) {
            sheet.autoSizeColumn(a);
        }
    }

    private void createDataCell(XSSFRow row, int colIndex, String value) {
        XSSFCell cell = row.createCell(colIndex);
        cell.setCellValue(value);
        cell.setCellStyle(defaultStyle);
    }

    private void createHeaderCell(XSSFRow row, int colIndex, String value) {
        XSSFCell cell = row.createCell(colIndex);
        cell.setCellValue(value);
        cell.setCellStyle(boldStyle);
    }

    private void setupFonts(XSSFWorkbook wb) {

        Font fondBold = wb.createFont();
        fondBold.setFontHeightInPoints((short) DEFAULT_FONT_SIZE);
        fondBold.setFontName("Arial");
        fondBold.setColor(IndexedColors.BLACK.getIndex());
        fondBold.setBoldweight(Font.BOLDWEIGHT_BOLD);
        fondBold.setBold(true);

        boldStyle = wb.createCellStyle();
        boldStyle.setFont(fondBold);
        boldStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(40, 180, 196)));
        boldStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);

        defaultStyle = wb.createCellStyle();



        filterTextStyle = wb.createCellStyle();
        filterTextStyle.setFont(buildFont(wb, 12, "Arial", false, false));
        filterTextStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(240, 240, 240)));
        filterTextStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);

        filterHeaderStyle = wb.createCellStyle();
        filterHeaderStyle.setFont(buildFont(wb, 12, "Arial", true, false));
        filterHeaderStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(240, 240, 240)));
        filterHeaderStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);

        filterMainHeaderStyle = wb.createCellStyle();
        filterMainHeaderStyle.setFont(buildFont(wb, 16, "Arial", true, true));
        filterMainHeaderStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(240, 240, 240)));
        filterMainHeaderStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
    }

    private Font buildFont(XSSFWorkbook wb, int heightInPoints, String fontName, boolean bold, boolean underline) {
        Font font = wb.createFont();
        font.setFontHeightInPoints((short) heightInPoints);
        font.setFontName(fontName);
        font.setColor(IndexedColors.BLACK.getIndex());
        font.setBold(bold);
      //  font.setUnderline(underline);
        return font;
    }

    // CHECKSTYLE:ON MagicNumber
}
