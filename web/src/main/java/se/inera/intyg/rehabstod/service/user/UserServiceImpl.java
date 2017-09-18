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
package se.inera.intyg.rehabstod.service.user;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.integration.hsa.model.Mottagning;
import se.inera.intyg.infra.integration.hsa.model.SelectableVardenhet;
import se.inera.intyg.infra.integration.hsa.model.Vardenhet;
import se.inera.intyg.infra.integration.hsa.model.Vardgivare;
import se.inera.intyg.rehabstod.auth.RehabstodUser;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by eriklupander on 2016-01-19.
 */
@Service
public class UserServiceImpl implements UserService {
    @Override
    public RehabstodUser getUser() {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            return null;
        }

        return (RehabstodUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Override
    public boolean isUserLoggedInOnEnhetOrUnderenhet(String enhetsId) {
        RehabstodUser user = getUser();

        SelectableVardenhet valdVardenhet = user.getValdVardenhet();
        Set<String> allowedEnhetsId = new HashSet<>();
        if (valdVardenhet instanceof Vardenhet) {
            Vardenhet vardenhet = (Vardenhet) valdVardenhet;
            allowedEnhetsId.add(vardenhet.getId());
            vardenhet.getMottagningar().stream().forEach(m -> allowedEnhetsId.add(m.getId()));
        } else if (valdVardenhet instanceof Mottagning) {
            Mottagning mottagning = (Mottagning) valdVardenhet;
            for (Vardgivare vg : user.getVardgivare()) {
                for (Vardenhet ve : vg.getVardenheter()) {
                    if (ve.getId().equals(mottagning.getParentHsaId())) {
                        allowedEnhetsId.add(ve.getId());
                        ve.getMottagningar().stream().forEach(m -> allowedEnhetsId.add(m.getId()));
                    }
                }
            }
        }

        return allowedEnhetsId.contains(enhetsId);
    }

}
