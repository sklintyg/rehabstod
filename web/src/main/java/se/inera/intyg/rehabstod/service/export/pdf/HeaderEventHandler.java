/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
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

/**
 * Created by marced on 25/02/16.
 */

import static se.inera.intyg.rehabstod.service.export.pdf.PdfExportServiceImpl.PAGE_MARGIN_LEFT;
import static se.inera.intyg.rehabstod.service.export.pdf.PdfExportServiceImpl.PAGE_MARGIN_RIGHT;
import static se.inera.intyg.rehabstod.service.export.pdf.PdfExportServiceImpl.PAGE_MARGIN_TOP;
import static se.inera.intyg.rehabstod.service.export.pdf.PdfUtil.millimetersToPoints;

import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.property.TextAlignment;
import java.time.LocalDateTime;
import se.inera.intyg.rehabstod.common.util.HourMinuteFormatter;
import se.inera.intyg.rehabstod.common.util.YearMonthDateFormatter;

public class HeaderEventHandler implements IEventHandler {


    protected static final float LOGO_WIDTH = 25f;
    protected static final float LOGO_ESTIMATED_HEIGHT = 15f;

    private PdfImageXObject logo;
    private float footerFontSize;
    private String printedByText;


    public HeaderEventHandler(PdfImageXObject logo, String userName, String enhetsNamn, float footerFontSize) {
        this.logo = logo;
        this.footerFontSize = footerFontSize;
        LocalDateTime now = LocalDateTime.now();
        this.printedByText = String.format("%s %s                %s               Utskrift av %s",
            YearMonthDateFormatter.print(now), HourMinuteFormatter.print(now), enhetsNamn, userName);
    }


    @Override
    public void handleEvent(Event event) {
        if (!(event instanceof PdfDocumentEvent)) {
            return;
        }

        PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
        PdfDocument pdf = docEvent.getDocument();
        PdfPage page = docEvent.getPage();

        Rectangle pageSize = page.getPageSize();
        PdfCanvas pdfCanvas = new PdfCanvas(page.newContentStreamBefore(), page.getResources(), pdf);
        Canvas canvas = new Canvas(pdfCanvas, pdf, pageSize);

        // Logotyp
        pdfCanvas.addXObject(logo, millimetersToPoints(PAGE_MARGIN_LEFT), pageSize.getTop() - PAGE_MARGIN_TOP - LOGO_ESTIMATED_HEIGHT,
            millimetersToPoints(LOGO_WIDTH));

        renderPrintedBy(pageSize, canvas);

        pdfCanvas.release();
    }

    private void renderPrintedBy(Rectangle pageSize, Canvas canvas) {
        canvas.setFontSize(footerFontSize);
        canvas.showTextAligned(printedByText, pageSize.getWidth() - millimetersToPoints(PAGE_MARGIN_RIGHT),
            pageSize.getTop() - footerFontSize - PAGE_MARGIN_TOP, TextAlignment.RIGHT);
    }
}
