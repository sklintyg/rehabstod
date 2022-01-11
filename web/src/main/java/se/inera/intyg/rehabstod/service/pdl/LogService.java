/*
 * Copyright (C) 2022 Inera AB (http://www.inera.se)
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

import java.util.List;
import java.util.Map;
import se.inera.intyg.infra.logmessages.ActivityType;
import se.inera.intyg.infra.logmessages.ResourceType;
import se.inera.intyg.rehabstod.auth.pdl.PDLActivityEntry;
import se.inera.intyg.rehabstod.web.model.LUCertificate;
import se.inera.intyg.rehabstod.web.model.PatientData;
import se.inera.intyg.rehabstod.web.model.SjukfallEnhet;
import se.inera.intyg.schemas.contract.Personnummer;

/**
 * @author eriklupander on 2016-02-18.
 */
public interface LogService {

    void logSjukfallData(List<SjukfallEnhet> sjukfall,
        ActivityType activityType,
        ResourceType resourceType);

    void logSjukfallData(PatientData patientData,
        ActivityType activityType,
        ResourceType resourceType);

    void logConsentActivity(Personnummer personnummer,
        ActivityType activityType,
        ResourceType resourceType);

    void logCertificate(Personnummer personId,
        ActivityType activityType,
        ResourceType resourceType);

    void logCertificate(List<LUCertificate> luCertificateList, ActivityType readActivityType, ResourceType resourceTypeCertificate,
        Map<String, List<PDLActivityEntry>> storedActivities);
}
