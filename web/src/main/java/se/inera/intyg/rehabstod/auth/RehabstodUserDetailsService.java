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
package se.inera.intyg.rehabstod.auth;

// import static se.inera.intyg.common.integration.hsa.stub.Medarbetaruppdrag.VARD_OCH_BEHANDLING;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.opensaml.saml2.core.Assertion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;
import org.springframework.stereotype.Service;

import se.inera.intyg.common.integration.hsa.model.Vardgivare;
import se.inera.intyg.common.security.common.model.IntygUser;
import se.inera.intyg.common.security.siths.BaseSakerhetstjanstAssertion;
import se.inera.intyg.common.security.siths.BaseUserDetailsService;
import se.inera.intyg.rehabstod.auth.authorities.AuthoritiesConstants;
import se.inera.intyg.rehabstod.auth.exceptions.MissingUnitWithRehabSystemRoleException;

/**
 * @author andreaskaltenbach
 */
@Service
public class RehabstodUserDetailsService extends BaseUserDetailsService implements SAMLUserDetailsService {

    private static final Logger LOG = LoggerFactory.getLogger(RehabstodUserDetailsService.class);
    static final String HSA_SYSTEMROLE_REHAB_UNIT_PREFIX = "INTYG;Rehab-";

    // The part after prefix is assumed to be a hsa-enhetsid, this will be extracted and compared.
    private static final Pattern HSA_SYSTEMROLE_REHAB_UNIT_PATTERN = Pattern.compile("^" + HSA_SYSTEMROLE_REHAB_UNIT_PREFIX + "(.*)");

    // =====================================================================================
    // ~ Protected scope
    // =====================================================================================

    @Override
    protected void decorateIntygUserWithDefaultVardenhet(IntygUser intygUser) {
        // Only set a default enhet if there is only one. Otherwise let it be null so that the user must select one.
        if (intygUser.getTotaltAntalVardenheter() == 1) {
            super.decorateIntygUserWithDefaultVardenhet(intygUser);
        }
    }

    @Override
    protected RehabstodUser buildUserPrincipal(SAMLCredential credential) {
        IntygUser intygUser = super.buildUserPrincipal(credential);
        if (intygUser.getRoles().containsKey(AuthoritiesConstants.ROLE_KOORDINATOR)) {
            removeEnheterMissingRehabKoordinatorRole(intygUser.getVardgivare(), intygUser.getSystemRoles(), intygUser.getHsaId());
        }
        return new RehabstodUser(intygUser);
    }

    @Override
    protected String getDefaultRole() {
        return AuthoritiesConstants.ROLE_KOORDINATOR;
    }

    @Override
    protected BaseSakerhetstjanstAssertion getAssertion(Assertion assertion) {
        return super.getAssertion(assertion);
    }

    void removeEnheterMissingRehabKoordinatorRole(List<Vardgivare> authorizedVardgivare, List<String> systemRoles, String hsaId) {
        long unitsBefore = authorizedVardgivare.stream().mapToInt(vg -> vg.getVardenheter().size()).sum();

        // Get a clean list of enhetsId's that user is authorized to use rehab for
        List<String> rehabAuthorizedEnhetIds = parseEnhetsIdsFromSystemRoles(systemRoles);

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

    private List<String> parseEnhetsIdsFromSystemRoles(List<String> systemRoles) {
        List<String> idList = new ArrayList<>();
        for (String s : systemRoles) {
            Matcher matcher = HSA_SYSTEMROLE_REHAB_UNIT_PATTERN.matcher(s);
            if (matcher.find()) {
                idList.add(matcher.group(1));
            }
        }
        return idList;
    }

}
