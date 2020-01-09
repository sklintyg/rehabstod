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

import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;

/**
 * Helper functions for transforming millimeters to itext 7 points.
 */
public final class PdfUtil {

    public static final Border TABLE_SEPARATOR_BORDER = new SolidBorder(new DeviceRgb(0x99, 0x99, 0x99), 1);
    private static final String ELLIPSIZE_SUFFIX = "...";
    private static final float PPI = 72f;
    private static final float MM_PER_TUM = 25.4f;

    private PdfUtil() {

    }

    /**
     * Konverterar från millimeter till iText7 points.
     */
    public static float millimetersToPoints(final float value) {
        return inchesToPoints(millimetersToInches(value));
    }

    /**
     * Konverterar från millimeter till tum.
     */
    private static float millimetersToInches(final float value) {
        return value / MM_PER_TUM;
    }

    /**
     * Konverterar från tum till iText 7 points.
     */
    private static float inchesToPoints(final float value) {
        return value * PPI;
    }

    public static String ellipsize(String value, int maxlength) {
        if (value != null && value.length() > maxlength) {
            return value.substring(0, maxlength) + ELLIPSIZE_SUFFIX;
        } else {
            return value;
        }
    }

    public static Cell aCell() {
        return aCell(1);
    }

    public static Cell aCell(int colspan) {
        return new Cell(1, colspan).setBorder(Border.NO_BORDER);
    }

}
