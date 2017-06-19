/**
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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
import se.inera.intyg.rehabstod.persistence.model.AnvandarPreference;
import se.inera.intyg.rehabstod.persistence.repository.AnvandarPreferenceRepository;
import se.inera.intyg.rehabstod.service.user.UserService;
import se.inera.intyg.rehabstod.web.controller.api.dto.ChangeSelectedUnitRequest;
import se.inera.intyg.rehabstod.web.controller.api.dto.ChangeUrvalRequest;
import se.inera.intyg.rehabstod.web.controller.api.dto.GetUserResponse;
import se.inera.intyg.rehabstod.web.controller.api.dto.GivePdlLoggingConsentRequest;

import static se.inera.intyg.rehabstod.auth.RehabstodUserDetailsService.PDL_CONSENT_GIVEN;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private AnvandarPreferenceRepository anvandarPreferenceRepository;

    // api

    @RequestMapping(value = "", method = RequestMethod.GET)
    public GetUserResponse getUser() {
        RehabstodUser user = getRehabstodUser();
        return new GetUserResponse(user);
    }

    /**
     * Changes the selected care unit in the security context for the logged in user.
     *
     * @param changeSelectedEnhetRequest
     * @return
     */
    @RequestMapping(value = "/andraenhet", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public GetUserResponse changeSelectedUnitOnUser(@RequestBody ChangeSelectedUnitRequest changeSelectedEnhetRequest) {

        RehabstodUser user = getRehabstodUser();

        LOG.debug("Attempting to change selected unit for user '{}', currently selected unit is '{}'", user.getHsaId(),
                user.getValdVardenhet() != null ? user.getValdVardenhet().getId() : "<null>");

        boolean changeSuccess = user.changeValdVardenhet(changeSelectedEnhetRequest.getId());

        if (!changeSuccess) {
            throw new AuthoritiesException(String.format("Could not change active unit: Unit '%s' is not present in the MIUs for user '%s'",
                    changeSelectedEnhetRequest.getId(), user.getHsaId()));
        }

        LOG.debug("Selected vardenhet is now '{}'", user.getValdVardenhet().getId());

        return new GetUserResponse(user);
    }

    @RequestMapping(value = "/urval", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public GetUserResponse changeSelectedUrvalOnUser(@RequestBody ChangeUrvalRequest changeUrvalRequest) {

        RehabstodUser user = getRehabstodUser();

        boolean changeSuccess = user.changeSelectedUrval(changeUrvalRequest.getUrval());
        if (!changeSuccess) {
            throw new AuthoritiesException(String.format("User %s was not allowed to change urval to %s",
                    user.getHsaId(), changeUrvalRequest.getUrval()));
        }

        LOG.debug(String.format("Selected urval for user %s is now '%s' ", user.getHsaId(), user.getUrval()));

        return new GetUserResponse(user);
    }

    @RequestMapping(value = "/giveconsent", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public GetUserResponse givePdlLoggingConsent(@RequestBody GivePdlLoggingConsentRequest pdlLoggingConsentRequest) {

        RehabstodUser user = getRehabstodUser();
        
        // Update backend
        AnvandarPreference pdlConsentGiven = anvandarPreferenceRepository.findByHsaIdAndKey(user.getHsaId(), PDL_CONSENT_GIVEN);
        pdlConsentGiven.setValue(Boolean.toString(pdlLoggingConsentRequest.isConsentGiven()));
        anvandarPreferenceRepository.save(pdlConsentGiven);

        // Update current user context.
        user.setPdlConsentGiven(pdlLoggingConsentRequest.isConsentGiven());

        LOG.debug(String.format("User %s has now set PDL logging consent to '%s' ", user.getHsaId(), user.isPdlConsentGiven()));

        return new GetUserResponse(user);
    }

    // private scope

    private RehabstodUser getRehabstodUser() {
        RehabstodUser user = userService.getUser();

        if (user == null) {
            throw new AuthoritiesException("No user in session");
        }

        return user;
    }

}
