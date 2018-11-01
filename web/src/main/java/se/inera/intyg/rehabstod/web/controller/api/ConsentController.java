/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.web.controller.api;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.service.sjukfall.ConsentService;
import se.inera.intyg.rehabstod.service.user.UserService;
import se.inera.intyg.rehabstod.web.controller.api.dto.RegisterExtendedConsentRequest;
import se.inera.intyg.rehabstod.web.controller.api.dto.RegisterExtendedConsentResponse;
import se.inera.intyg.rehabstod.web.controller.api.util.ControllerUtil;

@RestController
@RequestMapping("/api/consent")
public class ConsentController {

    @Autowired
    private ConsentService consentService;

    @Autowired
    private UserService userService;

    /**
     * Register a consent for a patient.
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public RegisterExtendedConsentResponse registerConsent(@RequestBody RegisterExtendedConsentRequest request) {

        RegisterExtendedConsentResponse response;

        // Get logged in user
        RehabstodUser user = ControllerUtil.getRehabstodUser(userService);

        LocalDate today = LocalDate.now();

        LocalDateTime consentFrom = today.atStartOfDay();
        LocalDateTime consentTo = today.plusDays(request.getDays()).atTime(23,59,59);

        try {
            request.getGiveConsentToUnits().forEach((vgId, value) -> value.forEach(veId -> {
                consentService.giveConsent(vgId, veId, request.getPatientId(), request.isOnlyCurrentUser(),
                        null, consentFrom, consentTo, user);
            }));

            response = createResponse(RegisterExtendedConsentResponse.ResponseCode.OK, user.getHsaId());

        } catch (Exception e) {
            response = createResponse(RegisterExtendedConsentResponse.ResponseCode.ERROR, user.getHsaId());
            response.setResponseMessage(e.getMessage());
        }

        return response;
    }

    private RegisterExtendedConsentResponse createResponse(RegisterExtendedConsentResponse.ResponseCode responseCode,
                                                           String registeredBy) {

        RegisterExtendedConsentResponse response = new RegisterExtendedConsentResponse();
        response.setResponseCode(responseCode);
        response.setRegisteredBy(registeredBy);
        return response;
    }
}
