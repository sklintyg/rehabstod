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
package se.inera.intyg.rehabstod.service.pdl;

import se.inera.intyg.infra.integration.hsa.model.SelectableVardenhet;
import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.auth.authorities.AuthoritiesConstants;
import se.inera.intyg.rehabstod.service.pdl.dto.LogPatient;
import se.inera.intyg.rehabstod.service.pdl.dto.LogUser;
import se.inera.intyg.rehabstod.web.model.PatientData;

/**
 * @author Magnus Ekstrand on 2018-11-14.
 */
public final class PdlLogUtil {

    public static final String PDL_TITEL_LAKARE = "Läkare";
    public static final String PDL_TITEL_REHABSTOD = "Rehabkoordinator";


    private PdlLogUtil() {
    }

    public static LogPatient getLogPatient(PatientData patientData) {
        return new LogPatient.Builder(
                patientData.getPatient().getId(), patientData.getVardenhetId(), patientData.getVardgivareId())
                .enhetsNamn(patientData.getVardenhetNamn())
                .vardgivareNamn(patientData.getVardgivareNamn())
                .patientNamn(patientData.getPatient().getNamn())
                .build();
    }

    public static LogUser getLogUser(RehabstodUser user) {
        SelectableVardenhet valdVardgivare = user.getValdVardgivare();
        SelectableVardenhet valdVardenhet = user.getValdVardenhet();

        return new LogUser.Builder(user.getHsaId(), valdVardenhet.getId(), valdVardgivare.getId())
                .userName(user.getNamn())
                .userAssignment(user.getSelectedMedarbetarUppdragNamn())
                .userTitle(resolveUserTitle(user))
                .enhetsNamn(valdVardenhet.getNamn())
                .vardgivareNamn(valdVardgivare.getNamn())
                .build();
    }

    /**
     * LAKARE if user has LAKARE as current ROLE AND isLakare() is true.
     * REHABKOORDINATOR if user has REHABKOORDINATOR as current role and isLakare is true.
     * REHABKOORDINATOR if user has REHABKOORDINATOR as current role and isLakare is false.
     */
    private static String resolveUserTitle(RehabstodUser user) {
        return user.isLakare() && user.getRoles().containsKey(AuthoritiesConstants.ROLE_LAKARE)
                ? PDL_TITEL_LAKARE : PDL_TITEL_REHABSTOD;
    }

}
