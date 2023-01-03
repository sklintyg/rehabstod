/*
 * Copyright (C) 2023 Inera AB (http://www.inera.se)
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

import static se.inera.intyg.rehabstod.service.export.pdf.PdfExportServiceImpl.PAGE_MARGIN_LEFT;
import static se.inera.intyg.rehabstod.service.export.pdf.PdfExportServiceImpl.PAGE_MARGIN_RIGHT;
import static se.inera.intyg.rehabstod.service.export.pdf.PdfExportServiceImpl.PAGE_MARGIN_TOP;
import static se.inera.intyg.rehabstod.service.export.pdf.PdfUtil.millimetersToPoints;

import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.kernel.pdf.xobject.PdfXObject;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.properties.TextAlignment;
import java.time.LocalDateTime;
import se.inera.intyg.rehabstod.common.util.HourMinuteFormatter;
import se.inera.intyg.rehabstod.common.util.YearMonthDateFormatter;

public class HeaderEventHandler implements IEventHandler {

    protected static final float LOGO_WIDTH = 25f;
    protected static final float LOGO_ESTIMATED_HEIGHT = 15f;

    private final PdfImageXObject logo;
    private final float footerFontSize;
    private final String printedByText;

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

        final var  docEvent = (PdfDocumentEvent) event;
        final var  pdf = docEvent.getDocument();
        final var  page = docEvent.getPage();

        final var  pageSize = page.getPageSize();
        final var  pdfCanvas = new PdfCanvas(page.newContentStreamBefore(), page.getResources(), pdf);
        final var  canvas = new Canvas(pdfCanvas, pageSize);

        final var logoX = millimetersToPoints(PAGE_MARGIN_LEFT);
        final var logoY = pageSize.getTop() - PAGE_MARGIN_TOP - LOGO_ESTIMATED_HEIGHT;
        final var logoWidth = millimetersToPoints(LOGO_WIDTH);
        final var rectangle = PdfXObject.calculateProportionallyFitRectangleWithWidth(logo, logoX, logoY, logoWidth);

        pdfCanvas.addXObjectFittedIntoRectangle(logo, rectangle);
        renderPrintedBy(pageSize, canvas);
        pdfCanvas.release();
    }

    private void renderPrintedBy(Rectangle pageSize, Canvas canvas) {
        canvas.setFontSize(footerFontSize);
        canvas.showTextAligned(printedByText, pageSize.getWidth() - millimetersToPoints(PAGE_MARGIN_RIGHT),
            pageSize.getTop() - footerFontSize - PAGE_MARGIN_TOP, TextAlignment.RIGHT);
    }
}
