/*
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.auth.authorities;

/**
 * Created by mango on 25/11/15.
 */
public final class AuthoritiesConstants {

    // Titles, a.k.a 'legitimerad yrkesgrupp', has a coding system governing these titles. See:
    // HSA Innehåll Legitimerad yrkesgrupp
    // http://www.inera.se/TJANSTER--PROJEKT/HSA/Dokument/HSA-kodverk/
    public static final String TITLE_LAKARE = "Läkare";

    // Title codes, a.k.a 'befattningskod', has a coding system governing these codes. See:
    // HSA Innehåll Befattning
    // http://www.inera.se/TJANSTER--PROJEKT/HSA/Dokument/HSA-kodverk/
    public static final String TITLECODE_AT_LAKARE = "204010";

    // Known roles (these roles are copied from authorities.yaml which is the master authorities configuration)
    public static final String ROLE_LAKARE = "LAKARE";
    public static final String ROLE_KOORDINATOR = "REHABKOORDINATOR";

    public static final String FEATURE_SRS = "SRS";


    // constructors

    private AuthoritiesConstants() {
    }

}
