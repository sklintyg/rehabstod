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

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.joda.time.format.ISODateTimeFormat;
import org.springframework.stereotype.Service;

import se.inera.intyg.rehabstod.service.Urval;
import se.inera.intyg.rehabstod.web.controller.api.dto.PrintSjukfallRequest;
import se.inera.intyg.rehabstod.web.model.Gender;
import se.inera.intyg.rehabstod.web.model.InternalSjukfall;
import se.inera.intyg.rehabstod.web.model.Sjukfall;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * Created by marced on 24/02/16.
 */
@Service
public class PdfExportServiceImpl implements PdfExportService {
    /**
     * A font that will be used in our PDF.
     */
    public static final Font HEADER_FONT = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, BaseColor.WHITE);
    public static final Font BOLD = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD);
    public static final Font NORMAL = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL);
    public static final Font SMALL = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL);
    private static final float TABLE_WIDTH = 100.0f;
    private static final float HEADER_PADDING = 3f;

    @Override
    public byte[] export(List<InternalSjukfall> sjukfallList, PrintSjukfallRequest printSjukfallRequest, Urval urval) throws DocumentException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        Document document = new Document();

        document.setPageSize(PageSize.A4.rotate());

        PdfWriter writer = PdfWriter.getInstance(document, bos);
        writer.setPageEvent(new PageNumberingEventHandler());
        document.open();

        // document.addheader?
        document.add(createTable(sjukfallList, urval));

        // step 5
        document.close();

        return bos.toByteArray();

    }

    private PdfPTable createTable(List<InternalSjukfall> sjukfallList, Urval urval) throws DocumentException {
        // CHECKSTYLE:OFF MagicNumber
        PdfPTable table;

        if (Urval.ALL.equals(urval)) {
            table = new PdfPTable(new float[]{0.8f, 2.5f, 3, 1, 2, 1.5f, 1.5f, 2, 2, 2});
        } else {
            table = new PdfPTable(new float[]{0.8f, 2.5f, 3, 1, 2, 1.5f, 1.5f, 2, 2});
        }

        final BaseColor baseColor = new BaseColor(70, 87, 97);
        // CHECKSTYLE:ON MagicNumber

        table.setWidthPercentage(TABLE_WIDTH);

        table.getDefaultCell().setBackgroundColor(baseColor);
        table.getDefaultCell().setBorderColor(baseColor);
        table.getDefaultCell().setNoWrap(true);
        table.getDefaultCell().setPadding(HEADER_PADDING);
        table.getDefaultCell().setPaddingLeft(2f);

        addCell(table, "#", HEADER_FONT);
        addCell(table, "Personnummer", HEADER_FONT);
        addCell(table, "Namn", HEADER_FONT);
        addCell(table, "Kön", HEADER_FONT);
        addCell(table, "Nuvarande diagnos", HEADER_FONT);
        addCell(table, "Startdatum", HEADER_FONT);
        addCell(table, "Slutdatum", HEADER_FONT);
        addCell(table, "Sjukskrivningslängd", HEADER_FONT);
        addCell(table, "Sjukskrivningsgrad", HEADER_FONT);
        if (Urval.ALL.equals(urval)) {
            addCell(table, "Nuvarande läkare", HEADER_FONT);
        }

        table.getDefaultCell().setBackgroundColor(BaseColor.WHITE);
        table.getDefaultCell().setBorderColor(BaseColor.LIGHT_GRAY);
        table.getDefaultCell().setNoWrap(false);
        table.getDefaultCell().setPadding(2f);

        table.setHeaderRows(1);
        int rowNumber = 1;
        for (InternalSjukfall is : sjukfallList) {
            Sjukfall s = is.getSjukfall();

            addCell(table, String.valueOf(rowNumber));
            addCell(table, getPersonnummerColumn(s));
            addCell(table, s.getPatient().getNamn());
            addCell(table, getKonDesc(s.getPatient().getKon()));
            addCell(table, s.getDiagnos().getIntygsVarde());

            addCell(table, s.getStart() != null ? ISODateTimeFormat.yearMonthDay().print(s.getStart()) : "?");
            addCell(table, s.getSlut() != null ? ISODateTimeFormat.yearMonthDay().print(s.getSlut()) : "?");
            addCell(table, getlangdText(s));
            addCell(table, getGrader(s));
            if (Urval.ALL.equals(urval)) {
                addCell(table, s.getLakare());
            }
            rowNumber++;
        }

        return table;

    }

    private void addCell(PdfPTable table, Phrase p) {
        table.addCell(p);
    }

    private void addCell(PdfPTable table, String s) {
        addCell(table, s, NORMAL);
    }

    private void addCell(PdfPTable table, String s, Font font) {
        table.addCell(new Phrase(s, font));
    }

    private Phrase getGrader(Sjukfall s) {
        Phrase grader = new Phrase();
        for (Integer grad : s.getGrader()) {
            if (grad == s.getAktivGrad()) {
                grader.add(new Chunk(grad.toString() + "% ", BOLD));
            } else {
                grader.add(new Chunk(grad.toString() + "% ", NORMAL));
            }
        }
        return grader;
    }

    private Phrase getPersonnummerColumn(Sjukfall s) {
        Phrase p = new Phrase();
        p.add(new Chunk(s.getPatient().getId() != null ? s.getPatient().getId() : "", NORMAL));
        p.add(new Chunk(String.format(" (%d år)", s.getPatient().getAlder()), BOLD));
        return p;
    }

    private Phrase getlangdText(Sjukfall s) {
        Phrase p = new Phrase();
        p.add(new Chunk(String.format("%d dagar", s.getDagar()), NORMAL));
        p.add(new Chunk(String.format(" (%d intyg)", s.getIntyg()), SMALL));
        return p;
    }

    private String getKonDesc(Gender kon) {
        if (Gender.F.equals(kon)) {
            return "Kvinna";

        } else if (Gender.M.equals(kon)) {
            return "Man";
        }
        return "Okänt";
    }

    class PageNumberingEventHandler extends PdfPageEventHelper {

        private static final int WIDTH = 30;
        private static final int HEIGHT = 16;
        private static final float TOTAL_WIDTH = 100f;
        /**
         * The template with the total number of pages.
         */
        PdfTemplate total;

        /**
         * Creates the PdfTemplate that will hold the total number of pages.
         * @see com.itextpdf.text.pdf.PdfPageEventHelper#onOpenDocument(com.itextpdf.text.pdf.PdfWriter,
         * com.itextpdf.text.Document)
         */
        public void onOpenDocument(PdfWriter writer, Document document) {
            total = writer.getDirectContent().createTemplate(WIDTH, HEIGHT);
        }

        /**
         * Adds a header to every page.
         * @see com.itextpdf.text.pdf.PdfPageEventHelper#onEndPage(com.itextpdf.text.pdf.PdfWriter,
         * com.itextpdf.text.Document)
         */
        public void onEndPage(PdfWriter writer, Document document) {
            PdfPTable table = new PdfPTable(2);
            try {
                table.setWidths(new int[]{2, 1});
                table.setTotalWidth(TOTAL_WIDTH);

                table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.getDefaultCell().setBorder(Rectangle.NO_BORDER);

                table.addCell(new Phrase(String.format("Sida %d av ", writer.getPageNumber()), NORMAL));
                PdfPCell cell = new PdfPCell(Image.getInstance(total));
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                table.writeSelectedRows(0, -1, document.right() - WIDTH * 2, document.bottom() - document.bottomMargin() / 2 + HEIGHT,
                        writer.getDirectContent());
            } catch (DocumentException de) {
                throw new ExceptionConverter(de);
            }
        }

        /**
         * Fills out the total number of pages before the document is closed.
         * @see com.itextpdf.text.pdf.PdfPageEventHelper#onCloseDocument(com.itextpdf.text.pdf.PdfWriter,
         * com.itextpdf.text.Document)
         */
        public void onCloseDocument(PdfWriter writer, Document document) {
            // CHECKSTYLE:OFF MagicNumber
            ColumnText.showTextAligned(total, Element.ALIGN_LEFT, new Phrase(String.valueOf(writer.getPageNumber()), NORMAL), 1, 4, 0);
        }
    }
}
