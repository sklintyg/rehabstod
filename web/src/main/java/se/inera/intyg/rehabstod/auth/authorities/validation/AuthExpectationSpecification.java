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
package se.inera.intyg.rehabstod.auth.authorities.validation;

/**
 * Created by marced on 18/12/15.
 */
public interface AuthExpectationSpecification {

    /**
     * To pass, the user's must have a role matching one of the given validRoles.
     * <p/>
     * If multiple validRoles are given, this is effectively an OR condition. To express an AND condition you can simply
     * chain multiple roles(type1).roles(type2)
     *
     * @param validRoles
     * @return
     */
    AuthExpectationSpecification roles(String... validRoles);

    /**
     * To pass, the user's role must NOT match any of the given invalidRoles.
     *
     * @param invalidRoles
     * @return
     */
    AuthExpectationSpecification notRoles(String... invalidRoles);

    /**
     * Returns true if all added checks passes, otherwise false.
     * @return
     */
    boolean isVerified();

    /**
     * Throws AuthoritiesException if any of the added check fails.
     * @return
     */
    void orThrow();
}
