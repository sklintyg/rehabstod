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
package se.inera.intyg.rehabstod.service.pdl;


import se.inera.intyg.common.support.modules.support.api.dto.Personnummer;
import se.inera.intyg.rehabstod.service.pdl.dto.LogRequest;
import se.inera.intyg.rehabstod.web.model.InternalSjukfall;


public final class LogRequestFactory {

    private LogRequestFactory() {
    }

    public static LogRequest createLogRequestFromSjukfall(InternalSjukfall sjukfall) {

        LogRequest logRequest = new LogRequest();

        Personnummer personnummer = new Personnummer(sjukfall.getSjukfall().getPatient().getId());
        logRequest.setPatientId(personnummer);
        logRequest.setPatientName("", "", sjukfall.getSjukfall().getPatient().getNamn());

        logRequest.setIntygCareUnitId(sjukfall.getVardEnhetId());
        logRequest.setIntygCareUnitName(sjukfall.getVardEnhetNamn());

        logRequest.setIntygCareGiverId(sjukfall.getVardGivareId());
        logRequest.setIntygCareGiverName(sjukfall.getVardGivareNamn());

        return logRequest;
    }
}
