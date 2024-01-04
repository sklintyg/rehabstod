/*
 * Copyright (C) 2024 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.service.user;

import se.inera.intyg.rehabstod.auth.RehabstodUser;

/**
 * @author eriklupander on 2016-01-19.
 */
public interface UserService {

    RehabstodUser getUser();

    /**
     * Method determine if the supplied enhetsId is a Vardenhet or Mottagning identifier.
     *
     * <ul>
     *     <li>If the selectedVardenhet is a Vardenhet: The supplied enhetsId is for the Vardenhet or one of its Mottagningar.</li>
     *     <li>If the selcetedVardenhet is a Mottagning: The supplied enhetsId is the Mottagning,
     *     its parent Vardenhet or one of the sibling Mottagningar.</li>
     * </ul>
     *
     * @param enhetsId HSA-id of a vardenhet or mottagning.
     * @return true if match is found.
     */
    boolean isUserLoggedInOnEnhetOrUnderenhet(String enhetsId);
}
