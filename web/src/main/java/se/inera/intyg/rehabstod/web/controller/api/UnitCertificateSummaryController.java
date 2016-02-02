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
package se.inera.intyg.rehabstod.web.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.auth.authorities.AuthoritiesException;
import se.inera.intyg.rehabstod.service.user.UserService;
import se.inera.intyg.rehabstod.web.controller.api.dto.GetUnitCertificateSummaryResponse;

import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/api/unit-certificate-summary")
public class UnitCertificateSummaryController {

    @Autowired
    UserService userService;

    @RequestMapping(value = "/active", method = RequestMethod.GET)
    public GetUnitCertificateSummaryResponse getUnitCertificateSummary() {
        RehabstodUser user = userService.getUser();
        if (user == null) {
            throw new AuthoritiesException("No user in session");
        } else {

            int total = ThreadLocalRandom.current().nextInt(0, 1000 + 1);

            int men = ThreadLocalRandom.current().nextInt(0, 100 + 1);

            int women = 100 - men;

            return new GetUnitCertificateSummaryResponse(total, men, women);
        }
    }
}
