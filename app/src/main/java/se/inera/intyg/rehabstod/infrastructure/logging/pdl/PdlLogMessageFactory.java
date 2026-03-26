/*
 * Copyright (C) 2026 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.infrastructure.logging.pdl;

import java.util.List;
import java.util.Map;
import se.inera.intyg.rehabstod.application.api.model.LUCertificate;
import se.inera.intyg.rehabstod.application.api.model.SjukfallEnhet;
import se.inera.intyg.rehabstod.infrastructure.security.auth.pdl.PDLActivityEntry;
import se.inera.intyg.rehabstod.infrastructure.logging.logmessages.ActivityType;
import se.inera.intyg.rehabstod.infrastructure.logging.logmessages.PdlLogMessage;
import se.inera.intyg.rehabstod.infrastructure.logging.logmessages.ResourceType;
import se.inera.intyg.rehabstod.infrastructure.logging.pdl.dto.LogPatient;
import se.inera.intyg.rehabstod.infrastructure.logging.pdl.dto.LogUser;

/**
 * @author eriklupander on 2016-03-03.
 */
public interface PdlLogMessageFactory {

  PdlLogMessage buildLogMessage(
      List<SjukfallEnhet> sjukfallList,
      LogUser logUser,
      ActivityType activityType,
      ResourceType resourceType);

  PdlLogMessage buildLogMessage(
      LogPatient logPatient, LogUser logUser, ActivityType activityType, ResourceType resourceType);

  PdlLogMessage buildLogMessage(
      List<LUCertificate> luCertificateList,
      LogUser logUser,
      ActivityType activityType,
      ResourceType resourceType,
      Map<String, List<PDLActivityEntry>> storedActivities);
}