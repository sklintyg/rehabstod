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
import se.inera.intyg.rehabstod.service.certificate.CertificateService;
import se.inera.intyg.rehabstod.service.certificate.dto.SjukfallSummary;
import se.inera.intyg.rehabstod.service.user.UserService;

@RestController
@RequestMapping("/api/sjukfall-summary")
public class SjukfallSummaryController {

    @Autowired
    UserService userService;

    @Autowired
    CertificateService certificateService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public SjukfallSummary getUnitCertificateSummary() {
        RehabstodUser user = userService.getUser();
        if (user == null) {
            throw new AuthoritiesException("No user in session");
        } else {
            return certificateService.getSummary();
        }
    }
}
