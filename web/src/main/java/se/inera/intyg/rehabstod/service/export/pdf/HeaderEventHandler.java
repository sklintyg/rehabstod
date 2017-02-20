/**
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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

import java.time.LocalDateTime;

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

import se.inera.intyg.rehabstod.common.util.HourMinuteFormatter;
import se.inera.intyg.rehabstod.common.util.YearMonthDateFormatter;

public class HeaderEventHandler extends PdfPageEventHelper {
    private static final int TOP_MARGIN_TO_HEADER = 20;
    private static final float LOGO_SCALE_FACTOR = 30.0f;
    private String userName;
    private String enhetsNamn;
    private Image logo;

    public HeaderEventHandler(Image logo, String userName, String enhetsNamn) {
        this.logo = logo;
        this.userName = userName;
        this.enhetsNamn = enhetsNamn;
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {

        // Create the header table
        PdfPTable table = new PdfPTable(2);
        table.setTotalWidth(document.getPageSize().getWidth() - (document.leftMargin() + document.rightMargin()));

        // Add out 2 cells
        table.addCell(getLogoCell());
        table.addCell(printedBy(userName, enhetsNamn));

        // write the table
        table.writeSelectedRows(0, -1, document.leftMargin(), document.getPageSize().getTop() - TOP_MARGIN_TO_HEADER,
                writer.getDirectContent());

    }

    private PdfPCell getLogoCell() {
        float scalePercentage = LOGO_SCALE_FACTOR;
        logo.scalePercent(scalePercentage);
        PdfPCell imageCell = new PdfPCell(logo, false);
        imageCell.setBorder(Rectangle.NO_BORDER);
        return imageCell;
    }

    private PdfPCell printedBy(String userName, String enhetsNamn) {
        LocalDateTime now = LocalDateTime.now();

        Phrase printedBy = new Phrase("", PdfExportConstants.TABLE_CELL_NORMAL);
        printedBy.add(new Chunk("Utskrift av " + userName));
        printedBy.add(Chunk.NEWLINE);
        printedBy.add(new Chunk(enhetsNamn));
        printedBy.add(Chunk.NEWLINE);
        printedBy.add(new Chunk(YearMonthDateFormatter.print(now)));
        printedBy.add(new Chunk(" - "));
        printedBy.add(new Chunk(HourMinuteFormatter.print(now)));

        PdfPCell cell = new PdfPCell(printedBy);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
        cell.setBorder(Rectangle.NO_BORDER);

        return cell;
    }

}
