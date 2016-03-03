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
package se.inera.intyg.rehabstod.service.export;

import org.springframework.beans.factory.annotation.Autowired;

import se.inera.intyg.rehabstod.service.diagnos.DiagnosKapitelService;
import se.inera.intyg.rehabstod.web.controller.api.dto.PrintSjukfallRequest;

/**
 * Created by eriklupander on 2016-02-26.
 */
public abstract class BaseExportService {

    @Autowired
    protected DiagnosKapitelService diagnosKapitelService;

    protected boolean notEmpty(PrintSjukfallRequest req) {
        return req.getFritext() != null && req.getFritext().trim().length() > 0;
    }

    protected String buildKonName(String konKod) {
        switch (konKod) {
        case "M":
            return "Man";
        case "F":
            return "Kvinna";
        default:
            return "Okänd";
        }
    }

}
