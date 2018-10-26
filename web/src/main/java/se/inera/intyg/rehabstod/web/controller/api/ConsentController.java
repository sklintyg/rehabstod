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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import se.inera.intyg.infra.security.authorities.AuthoritiesException;
import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.integration.samtyckestjanst.service.SamtyckestjanstIntegrationService;
import se.inera.intyg.rehabstod.service.user.UserService;
import se.inera.intyg.rehabstod.web.controller.api.dto.RegisterExtendedConsentRequest;
import se.inera.intyg.rehabstod.web.controller.api.dto.RegisterExtendedConsentResponse;
import se.riv.informationsecurity.authorization.consent.v2.ActionType;
import se.riv.informationsecurity.authorization.consent.v2.ActorType;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/consent")
public class ConsentController {

    private static final Logger LOG = LoggerFactory.getLogger(ConsentController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private SamtyckestjanstIntegrationService samtyckestjanstIntegrationService;

    /**
     * Register a consent for a patient.
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public RegisterExtendedConsentResponse registerConsent(@RequestBody RegisterExtendedConsentRequest request) {

        RegisterExtendedConsentResponse response;

        // Get logged in user
        RehabstodUser user = getRehabstodUser();

        LocalDateTime registrationDate = LocalDateTime.now();
        LocalDateTime consentFrom = request.getConsentFrom() == null ? null : request.getConsentFrom().atStartOfDay();
        LocalDateTime consentTo = request.getConsentTo() == null ? null : request.getConsentTo().atStartOfDay();

        try {
            LOG.debug("Attempting to register consent for care provider '{}' and unit '{}'. Registration is made by user '{}'",
                    user.getValdVardgivare().getId(), user.getValdVardenhet().getId(), user.getHsaId());

            samtyckestjanstIntegrationService.registerConsent(user.getValdVardgivare().getId(), user.getValdVardenhet().getId(),
                    request.getPatientId(), request.getUserHsaId(), request.getRepresentedBy(),
                    consentFrom, consentTo,createActionType(user, registrationDate));

            response = new RegisterExtendedConsentResponse(
                    RegisterExtendedConsentResponse.ResponseCode.OK, user.getHsaId(), registrationDate.toLocalDate());

        } catch(Exception e) {
            response = new RegisterExtendedConsentResponse(
                    RegisterExtendedConsentResponse.ResponseCode.ERROR, user.getHsaId(), registrationDate.toLocalDate());
            response.setResponseMessage(e.getMessage());
        }

        return response;
    }

    private ActionType createActionType(RehabstodUser user, LocalDateTime registrationDate) {
        ActionType registrationAction = new ActionType();
        registrationAction.setRegisteredBy(createActorType(user));
        registrationAction.setRegistrationDate(registrationDate);
        registrationAction.setRequestedBy(createActorType(user));
        registrationAction.setRequestDate(registrationDate);
        return registrationAction;
    }

    private ActorType createActorType(RehabstodUser user) {
        ActorType actorType = new ActorType();
        actorType.setEmployeeId(user.getHsaId());
        actorType.setAssignmentName(user.getSelectedMedarbetarUppdragNamn());
        return actorType;
    }

    private RehabstodUser getRehabstodUser() {
        RehabstodUser user = userService.getUser();

        if (user == null) {
            throw new AuthoritiesException("No user in session");
        }
        return user;
    }

}
