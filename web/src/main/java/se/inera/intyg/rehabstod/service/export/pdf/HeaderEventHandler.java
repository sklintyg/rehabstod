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
package se.inera.intyg.rehabstod.service.export.pdf;

/**
 * Created by marced on 25/02/16.
 */

import org.joda.time.LocalDateTime;
import org.joda.time.format.ISODateTimeFormat;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

public class HeaderEventHandler extends PdfPageEventHelper {
    private static final int TOP_MARGIN_TO_HEADER = 10;
    private String userName;
    private String enhetsNamn;
    private Image logo;

    public HeaderEventHandler(Image logo, String userName, String enhetsNamn) {
        this.logo = logo;
        this.userName = userName;
        this.enhetsNamn = enhetsNamn;
    }

    public void onEndPage(PdfWriter writer, Document document) {

        // Create the header table
        PdfPTable table = new PdfPTable(2);
        table.setTotalWidth(document.getPageSize().getWidth() - (document.leftMargin() + document.rightMargin()));

        // Add out 2 cells
        table.addCell(getLogoCell());
        table.addCell(printedBy(userName, enhetsNamn));

        // write the table
        table.writeSelectedRows(0, -1, document.leftMargin(), document.getPageSize().getTop() - TOP_MARGIN_TO_HEADER, writer.getDirectContent());

    }

    private PdfPCell getLogoCell() {
        PdfPCell imageCell = new PdfPCell(logo, false);
        imageCell.setBorder(Rectangle.NO_BORDER);
        return imageCell;
    }

    private PdfPCell printedBy(String userName, String enhetsNamn) {
        LocalDateTime now = new LocalDateTime();

        Phrase printedBy = new Phrase("", PdfExportService.TABLE_CELL_NORMAL);
        printedBy.add(new Chunk("Utskrift av " + userName));
        printedBy.add(Chunk.NEWLINE);
        printedBy.add(new Chunk(enhetsNamn));
        printedBy.add(Chunk.NEWLINE);
        printedBy.add(new Chunk(ISODateTimeFormat.yearMonthDay().print(now)));
        printedBy.add(new Chunk(" - "));
        printedBy.add(new Chunk(ISODateTimeFormat.hourMinute().print(now)));

        PdfPCell cell = new PdfPCell(printedBy);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }

}
