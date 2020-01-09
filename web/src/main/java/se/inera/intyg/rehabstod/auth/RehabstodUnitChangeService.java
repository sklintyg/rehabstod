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
package se.inera.intyg.rehabstod.auth;

import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.integration.hsa.model.Mottagning;
import se.inera.intyg.infra.security.authorities.CommonAuthoritiesResolver;
import se.inera.intyg.infra.security.common.model.Role;
import se.inera.intyg.rehabstod.auth.authorities.AuthoritiesConstants;
import se.inera.intyg.rehabstod.auth.util.SystemRolesParser;

/**
 * This is a helper class that manages user vardenhet changes, basically implementing the requirement
 * that LAKARE having systemRoles "Rehab-[unit id]" shall have REHABKOORDINATOR privileges on those care units,
 * and their normal LAKARE privileges on any other care units they have MIU on.
 *
 * @author erikl
 */
@Service
public class RehabstodUnitChangeService {

    @Autowired
    private CommonAuthoritiesResolver commonAuthoritiesResolver;

    public boolean changeValdVardenhet(String enhetId, RehabstodUser user) {
        boolean ok = user.changeValdVardenhet(enhetId);

        if (ok) {
            // INTYG-5068: We need to check systemRoles for Lakare. If they have systemRole for the selected enhet,
            // we change their role to REHABKOORDINATOR instead. Note use of overridden "isLakare" that does NOT
            // derive lakare status from roles.
            if (user.isLakare() && hasSystemRoles(user)) {
                List<String> enhetIdList = SystemRolesParser.parseEnhetsIdsFromSystemRoles(user.getSystemRoles());

                boolean hasSystemRoleForSelectedUnit = enhetIdList.stream().anyMatch(s -> s.equals(enhetId));
                if (hasSystemRoleForSelectedUnit || hasImplicitAccessToSubUnit(user, enhetIdList)) {
                    updateUsersRoleTo(user, AuthoritiesConstants.ROLE_KOORDINATOR);
                } else {
                    // If no systemRole for this unit, we must change back to ROLE_LAKARE.
                    updateUsersRoleTo(user, AuthoritiesConstants.ROLE_LAKARE);
                }
            }
        }

        return ok;
    }

    // Checks if any we have a Mottagning selected. If so, we check if the parent care unit is in the
    // list of enhetIds designated by systemRoles.
    private boolean hasImplicitAccessToSubUnit(RehabstodUser user, List<String> systemRoleEnhetIdList) {
        if (user.getValdVardenhet() instanceof Mottagning) {
            Mottagning m = (Mottagning) user.getValdVardenhet();
            return systemRoleEnhetIdList.stream()
                .anyMatch(systemRoleEnhetId -> systemRoleEnhetId.equals(m.getParentHsaId()));
        }
        return false;
    }

    // Clears roles, sets new ones and updates active features.
    private void updateUsersRoleTo(RehabstodUser user, String roleKey) {
        user.getRoles().clear();
        Role role = commonAuthoritiesResolver.getRole(roleKey);
        user.getRoles().put(roleKey, role);
        user.setFeatures(
            commonAuthoritiesResolver.getFeatures(Arrays.asList(user.getValdVardenhet().getId(), user.getValdVardgivare().getId())));
    }

    private boolean hasSystemRoles(RehabstodUser user) {
        return user.getSystemRoles() != null && user.getSystemRoles().size() > 0;
    }

}
