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
package se.inera.intyg.rehabstod.infrastructure.integration.it.client;

// CHECKSTYLE:OFF LineLength

import org.springframework.stereotype.Service;
import se.inera.intyg.clinicalprocess.healthcond.rehabilitation.listsickleavesforperson.v1.ListSickLeavesForPersonResponderInterface;
import se.inera.intyg.clinicalprocess.healthcond.rehabilitation.listsickleavesforperson.v1.ListSickLeavesForPersonResponseType;
import se.inera.intyg.clinicalprocess.healthcond.rehabilitation.listsickleavesforperson.v1.ListSickLeavesForPersonType;
import se.inera.intyg.rehabstod.infrastructure.config.properties.AppProperties;
import se.inera.intyg.rehabstod.infrastructure.logging.PerformanceLogging;
import se.inera.intyg.rehabstod.infrastructure.logging.mdc.MdcLogConstants;
import se.riv.clinicalprocess.healthcond.certificate.types.v2.PersonId;

// CHECKSTYLE:ON LineLength

/** Created by eriklupander on 2016-01-29. */
@Service
public class IntygstjanstClientServiceImpl implements IntygstjanstClientService {

  private final ListSickLeavesForPersonResponderInterface personService;
  private final String logicalAddress;

  public IntygstjanstClientServiceImpl(
      ListSickLeavesForPersonResponderInterface personService, AppProperties appProperties) {
    this.personService = personService;
    this.logicalAddress = appProperties.integration().intygstjanst().logicalAddress();
  }

  @Override
  @PerformanceLogging(
      eventAction = "get-sick-leave-for-patient",
      eventType = MdcLogConstants.EVENT_TYPE_ACCESSED)
  public ListSickLeavesForPersonResponseType getAllSjukfallForPatient(String patientId) {
    PersonId pId = new PersonId();
    pId.setExtension(patientId);

    ListSickLeavesForPersonType params = new ListSickLeavesForPersonType();
    params.setPersonId(pId);

    return personService.listSickLeavesForPerson(logicalAddress, params);
  }
}
