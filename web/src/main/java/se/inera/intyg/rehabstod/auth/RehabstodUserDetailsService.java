/*
 * Copyright (C) 2025 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.auth;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import se.inera.intyg.infra.integration.hsatk.model.legacy.UserCredentials;
import se.inera.intyg.infra.integration.hsatk.model.legacy.Vardgivare;
import se.inera.intyg.infra.security.common.model.IntygUser;
import se.inera.intyg.infra.security.siths.BaseUserDetailsService;
import se.inera.intyg.rehabstod.auth.RehabstodUserPreferences.Preference;
import se.inera.intyg.rehabstod.auth.authorities.AuthoritiesConstants;
import se.inera.intyg.rehabstod.auth.exceptions.MissingUnitWithRehabSystemRoleException;
import se.inera.intyg.rehabstod.auth.util.SystemRolesParser;
import se.inera.intyg.rehabstod.common.util.StringUtil;
import se.inera.intyg.rehabstod.persistence.model.AnvandarPreference;
import se.inera.intyg.rehabstod.persistence.repository.AnvandarPreferenceRepository;

/**
 * @author andreaskaltenbach
 */
@Service
public class RehabstodUserDetailsService extends BaseUserDetailsService {

    private static final Logger LOG = LoggerFactory.getLogger(RehabstodUserDetailsService.class);

    public static final String PDL_CONSENT_GIVEN = "user_pdl_consent_given";

    @Autowired
    private AnvandarPreferenceRepository anvandarPreferenceRepository;

    @Autowired
    private RehabstodUnitChangeService rehabstodUnitChangeService;

    // =====================================================================================
    // ~ Protected scope
    // =====================================================================================

    @Override
    public RehabstodUser buildUserPrincipal(String employeeHsaId, String authenticationScheme) {
        return buildUserPrincipal(employeeHsaId, null, authenticationScheme);
    }

    public RehabstodUser buildUserPrincipal(String employeeHsaId, String unitId, String authenticationScheme) {
        // All rehab customization is done in the overridden decorateXXX methods, so just return a new rehabuser
        IntygUser intygUser = super.buildUserPrincipal(employeeHsaId, authenticationScheme);
        RehabstodUser rehabstodUser = new RehabstodUser(intygUser, isPdlConsentGiven(intygUser.getHsaId()), intygUser.isLakare());

        RehabstodUserPreferences preferences = RehabstodUserPreferences
            .fromBackend(anvandarPreferenceRepository.getAnvandarPreference(intygUser.getHsaId()));
        rehabstodUser.setPreferences(preferences);

        final String savedDefaultLoginHsaUnitId = rehabstodUser.getPreferences().get(Preference.DEFAULT_LOGIN_HSA_UNIT_ID);
        boolean usedDefaultUnit = false;
        if (!StringUtil.isNullOrEmpty(savedDefaultLoginHsaUnitId)) {
            if (!rehabstodUnitChangeService.changeValdVardenhet(savedDefaultLoginHsaUnitId, rehabstodUser)) {
                LOG.info("User " + rehabstodUser.getHsaId() + " had " + savedDefaultLoginHsaUnitId
                    + " as default_login_hsa_unit_id - but failed to change to that unit (no longer access to it?). "
                    + "Deleting the faulty preference now.");
                final AnvandarPreference defaultUnitPref = anvandarPreferenceRepository
                    .findByHsaIdAndKey(rehabstodUser.getHsaId(), Preference.DEFAULT_LOGIN_HSA_UNIT_ID.getBackendKeyName());
                anvandarPreferenceRepository.delete(defaultUnitPref);
            } else {
                usedDefaultUnit = true;
                LOG.debug("Setting default_login_hsa_unit_id " + savedDefaultLoginHsaUnitId + " for User " + rehabstodUser.getHsaId());
            }
        }

        if (!StringUtil.isNullOrEmpty(unitId)) {
            rehabstodUnitChangeService.changeValdVardenhet(unitId, rehabstodUser);
        }

        // INTYG-5068: Explicitly changing vardenhet on session creation to possibly appyl REHABKOORDINATOR role for
        // this unit in case the user is LAKARE and has systemRole Rehab- for the current unit.
        // This is only performed if there were a unit selected, e.g. user only has access to a single unit.
        if (!usedDefaultUnit && rehabstodUser.getValdVardenhet() != null) {
            rehabstodUnitChangeService.changeValdVardenhet(rehabstodUser.getValdVardenhet().getId(), rehabstodUser);
        }

        if (rehabstodUser.getValdVardenhet() != null) {
            rehabstodUser.setFeatures(
                commonAuthoritiesResolver.getFeatures(
                    Arrays.asList(rehabstodUser.getValdVardenhet().getId(), rehabstodUser.getValdVardgivare().getId())
                )
            );
        }

        return rehabstodUser;
    }

    private boolean isPdlConsentGiven(String hsaId) {
        AnvandarPreference pdlConsentGiven = anvandarPreferenceRepository.findByHsaIdAndKey(hsaId, PDL_CONSENT_GIVEN);
        return pdlConsentGiven != null && Boolean.valueOf(pdlConsentGiven.getValue());
    }

    @Override
    protected void decorateIntygUserWithDefaultVardenhet(IntygUser intygUser) {
        // Only set a default enhet if there is only one (mottagningar doesnt count).
        // If no default vardenhet can be determined - let it be null and force user to select one.
        if (getTotaltAntalVardenheterExcludingMottagningar(intygUser) == 1) {
            super.decorateIntygUserWithDefaultVardenhet(intygUser);
        }
    }

    private int getTotaltAntalVardenheterExcludingMottagningar(IntygUser intygUser) {
        // count all vardenheter (not including mottagningar under vardenheter)
        return (int) intygUser.getVardgivare().stream().flatMap(vg -> vg.getVardenheter().stream()).count();
    }

    @Override
    protected void decorateIntygUserWithSystemRoles(IntygUser intygUser, UserCredentials userCredentials) {
        super.decorateIntygUserWithSystemRoles(intygUser, userCredentials);

        if (intygUser.getRoles().containsKey(AuthoritiesConstants.ROLE_KOORDINATOR)) {
            // ROLE_KOORDINATOR must have a matching systemrole for each unit, or else it's removed
            removeEnheterMissingRehabKoordinatorRole(intygUser.getVardgivare(), intygUser.getSystemRoles(), intygUser.getHsaId());
        }

    }

    @Override
    protected String getDefaultRole() {
        return AuthoritiesConstants.ROLE_KOORDINATOR;
    }

    void removeEnheterMissingRehabKoordinatorRole(List<Vardgivare> authorizedVardgivare, List<String> systemRoles, String hsaId) {
        long unitsBefore = authorizedVardgivare.stream().mapToInt(vg -> vg.getVardenheter().size()).sum();

        // Get a clean list of enhetsId's that user is authorized to use rehab for
        List<String> rehabAuthorizedEnhetIds = SystemRolesParser.parseEnhetsIdsFromSystemRoles(systemRoles);

        // remove all vardeneheter that's not present in whitelist
        authorizedVardgivare.stream().forEach(vg -> vg.getVardenheter().removeIf(ve -> !rehabAuthorizedEnhetIds.contains(ve.getId())));

        // Also, any vardgivare with no vardenheter left should be removed for the default selection mechanism to work
        authorizedVardgivare.removeIf(vg -> vg.getVardenheter().size() < 1);

        long unitsAfter = authorizedVardgivare.stream().mapToInt(vg -> vg.getVardenheter().size()).sum();

        LOG.debug("removeEnheterMissingRehabKoordinatorRole rehabauthorized units are: [" + String.join(",", rehabAuthorizedEnhetIds)
            + "]. User units before filtering: " + unitsBefore + ", after: " + unitsAfter);

        if (unitsAfter < 1) {
            throw new MissingUnitWithRehabSystemRoleException(hsaId);
        }
    }

    @Override
    protected HttpServletRequest getCurrentRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    }
}
