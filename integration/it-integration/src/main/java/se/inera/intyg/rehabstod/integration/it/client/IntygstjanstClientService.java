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
package se.inera.intyg.rehabstod.integration.it.client;

// CHECKSTYLE:OFF LineLength

import se.inera.intyg.clinicalprocess.healthcond.rehabilitation.listactivesickleavesforcareunit.v1.ListActiveSickLeavesForCareUnitResponseType;
import se.riv.itintegration.monitoring.v1.PingForConfigurationResponseType;

// CHECKSTYLE:ON LineLength

/**
 * Created by eriklupander on 2016-01-29.
 */
public interface IntygstjanstClientService {

    ListActiveSickLeavesForCareUnitResponseType getSjukfallForUnit(String unitId, int maxAntalDagarSedanSjukfallAvslut);

    ListActiveSickLeavesForCareUnitResponseType getSjukfallForPatient(String unitId, String personId, int maxAntalDagarSedanSjukfallAvslut);

    PingForConfigurationResponseType pingForConfiguration();
}
