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

import java.io.IOException;
import java.util.List;

import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.web.controller.api.dto.PrintSjukfallRequest;
import se.inera.intyg.rehabstod.web.model.InternalSjukfall;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;

/**
 * Created by marced on 24/02/16.
 */
public interface PdfExportService {
    /**
     * Fonts that will be used in Rehab export PDF.
     */
    Font TABLE_HEADER_FONT = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, BaseColor.WHITE);
    Font TABLE_CELL_BOLD = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD);
    Font TABLE_CELL_NORMAL = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL);
    Font TABLE_CELL_SMALL = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL);

    Font FRONTPAGE_H1 = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
    Font FRONTPAGE_H2 = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
    Font FRONTPAGE_H3 = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD);

    Font FRONTPAGE_NORMAL = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL);
    Font FRONTPAGE_NORMAL_BOLD = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD);

    byte[] export(List<InternalSjukfall> sjukfallList, PrintSjukfallRequest printSjukfallRequest, RehabstodUser user)
            throws DocumentException, IOException;
}
