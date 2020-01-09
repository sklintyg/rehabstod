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
package se.inera.intyg.rehabstod.web.controller.api;

import static se.inera.intyg.rehabstod.auth.RehabstodUserDetailsService.PDL_CONSENT_GIVEN;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import se.inera.intyg.infra.security.authorities.AuthoritiesException;
import se.inera.intyg.infra.security.authorities.CommonAuthoritiesResolver;
import se.inera.intyg.infra.security.common.model.AuthenticationMethod;
import se.inera.intyg.rehabstod.auth.RehabstodUnitChangeService;
import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.auth.RehabstodUserPreferences;
import se.inera.intyg.rehabstod.auth.RehabstodUserPreferences.Preference;
import se.inera.intyg.rehabstod.auth.RehabstodUserTokens;
import se.inera.intyg.rehabstod.persistence.model.AnvandarPreference;
import se.inera.intyg.rehabstod.persistence.repository.AnvandarPreferenceRepository;
import se.inera.intyg.rehabstod.service.user.TokenExchangeService;
import se.inera.intyg.rehabstod.service.user.TokenServiceException;
import se.inera.intyg.rehabstod.service.user.UserPreferencesService;
import se.inera.intyg.rehabstod.service.user.UserService;
import se.inera.intyg.rehabstod.web.controller.api.dto.ChangeSelectedUnitRequest;
import se.inera.intyg.rehabstod.web.controller.api.dto.GetAccessTokenResponse;
import se.inera.intyg.rehabstod.web.controller.api.dto.GetUserResponse;
import se.inera.intyg.rehabstod.web.controller.api.dto.GivePdlLoggingConsentRequest;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);
    private static final int ACCESSTOKEN_EXPIRE_LIMIT_MINUTES = 10;

    @Autowired
    private UserService userService;

    @Autowired
    private UserPreferencesService userPreferencesService;

    @Autowired
    private AnvandarPreferenceRepository anvandarPreferenceRepository;

    @Autowired
    private RehabstodUnitChangeService rehabstodUnitChangeService;

    @Autowired
    private CommonAuthoritiesResolver commonAuthoritiesResolver;

    @Autowired
    private TokenExchangeService tokenExchangeService;

    @Autowired
    private Environment environment;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public GetUserResponse getUser() {
        RehabstodUser user = getRehabstodUser();
        return new GetUserResponse(user);
    }

    @RequestMapping(value = "/accesstoken", method = RequestMethod.GET)
    public GetAccessTokenResponse getAccessToken() {
        RehabstodUser user = getRehabstodUser();

        // Check if Tokens are available and refresh access token if applicable
        RehabstodUserTokens tokens = user.getTokens();
        if (tokens != null) {
            if (LocalDateTime.now().plusMinutes(ACCESSTOKEN_EXPIRE_LIMIT_MINUTES).isAfter(tokens.getAccessTokenExpiration())) {
                try {
                    tokens = tokenExchangeService.refresh(tokens);
                    user.setTokens(tokens);
                } catch (TokenServiceException exception) {
                    // Couldn't get AccessToken. Log and continue since this is not vital for Rehabstod.
                    // User will not be able to use "Visa Intyg".
                    LOG.error("Unable to refresh AccessToken for user {} with reason {}", user.getHsaId(), exception.getMessage());
                }
            }
        }

        // Don't use when prod profile is active
        String[] activeProfiles = environment.getActiveProfiles();
        if (Stream.of(activeProfiles).noneMatch("prod"::equalsIgnoreCase)) {
            if (tokens == null) {
                if (AuthenticationMethod.FAKE.equals(user.getAuthenticationMethod())) {
                    tokens = new RehabstodUserTokens();
                    tokens.setAccessToken("fakeToken-" + user.getHsaId());
                }
            }
        }

        return new GetAccessTokenResponse(tokens);
    }

    /**
     * Changes the selected care unit in the security context for the logged in user.
     */
    @RequestMapping(value = "/andraenhet", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public GetUserResponse changeSelectedUnitOnUser(@RequestBody ChangeSelectedUnitRequest changeSelectedEnhetRequest) {

        RehabstodUser user = getRehabstodUser();

        LOG.debug("Attempting to change selected unit for user '{}', currently selected unit is '{}'", user.getHsaId(),
            user.getValdVardenhet() != null ? user.getValdVardenhet().getId() : "<null>");

        // boolean changeSuccess = user.changeValdVardenhet(changeSelectedEnhetRequest.getId());
        // INTYG-5068: Do systemRole check here for Lakare???
        boolean changeSuccess = rehabstodUnitChangeService.changeValdVardenhet(changeSelectedEnhetRequest.getId(), user);

        if (!changeSuccess) {
            throw new AuthoritiesException(String.format("Could not change active unit: Unit '%s' is not present in the MIUs for user '%s'",
                changeSelectedEnhetRequest.getId(), user.getHsaId()));
        }

        user.setFeatures(
            commonAuthoritiesResolver.getFeatures(Arrays.asList(user.getValdVardenhet().getId(), user.getValdVardgivare().getId())));

        LOG.debug("Selected vardenhet is now '{}'", user.getValdVardenhet().getId());

        return new GetUserResponse(user);
    }

    @RequestMapping(value = "/giveconsent", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public GetUserResponse givePdlLoggingConsent(@RequestBody GivePdlLoggingConsentRequest pdlLoggingConsentRequest) {

        RehabstodUser user = getRehabstodUser();
        // Update backend
        AnvandarPreference pdlConsentGiven = anvandarPreferenceRepository.findByHsaIdAndKey(user.getHsaId(), PDL_CONSENT_GIVEN);
        if (pdlConsentGiven == null) {
            pdlConsentGiven = new AnvandarPreference(user.getHsaId(), PDL_CONSENT_GIVEN,
                Boolean.toString(pdlLoggingConsentRequest.isConsentGiven()));
        } else {
            pdlConsentGiven.setValue(Boolean.toString(pdlLoggingConsentRequest.isConsentGiven()));
        }
        anvandarPreferenceRepository.save(pdlConsentGiven);

        // Update current user context.
        user.setPdlConsentGiven(pdlLoggingConsentRequest.isConsentGiven());

        //also update the user preference store
        user.setPreferences(userPreferencesService.getAllPreferences());

        LOG.debug(String.format("User %s has now set PDL logging consent to '%s' ", user.getHsaId(), user.isPdlConsentGiven()));

        return new GetUserResponse(user);
    }

    @RequestMapping(value = "/preferences", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> updatePref(@RequestBody Map<String, String> keyValueMap) {

        RehabstodUserPreferences newPreferences = RehabstodUserPreferences.fromFrontend(keyValueMap);
        newPreferences.validate();

        RehabstodUserPreferences oldPreferences = userPreferencesService.getAllPreferences();

        // Set current pdl consent value
        newPreferences.updatePreference(Preference.PDL_CONSENT_GIVEN, oldPreferences.get(Preference.PDL_CONSENT_GIVEN));

        // Update preferences with new values
        userPreferencesService.updatePreferences(newPreferences);
        LOG.debug("Updating user pref with values {}", keyValueMap);

        // Check if preferences has changed for sjukfallresult related settings
        if (hasPreferencesChanged(oldPreferences, newPreferences,
            RehabstodUserPreferences.Preference.MAX_ANTAL_DAGAR_MELLAN_INTYG,
            RehabstodUserPreferences.Preference.MAX_ANTAL_DAGAR_SEDAN_SJUKFALL_AVSLUT)) {

            // INTYG-8139: clear user values if above preferences has changed.
            // This will force user to fetch some of the information yet again in order to
            // recalculate the patient history view.
            RehabstodUser user = getRehabstodUser();
            user.clearSjfData();
        }

        return getAllPrefs();
    }

    @RequestMapping(value = "/preferences", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> getAllPrefs() {
        return userPreferencesService.getAllPreferences().toFrontendMap();
    }

    private RehabstodUser getRehabstodUser() {
        RehabstodUser user = userService.getUser();

        if (user == null) {
            throw new AuthoritiesException("No user in session");
        }
        return user;
    }

    private boolean hasPreferencesChanged(RehabstodUserPreferences oldPreferences,
        RehabstodUserPreferences newPreferences,
        RehabstodUserPreferences.Preference... preferences) {

        if (oldPreferences == null && newPreferences != null) {
            return true;
        }
        if (oldPreferences != null && newPreferences == null) {
            return false;
        }
        if (preferences == null || preferences.length == 0) {
            return false;
        }

        for (RehabstodUserPreferences.Preference preference : preferences) {
            if (!oldPreferences.get(preference).equals(newPreferences.get(preference))) {
                return true;
            }
        }

        return false;
    }

}
