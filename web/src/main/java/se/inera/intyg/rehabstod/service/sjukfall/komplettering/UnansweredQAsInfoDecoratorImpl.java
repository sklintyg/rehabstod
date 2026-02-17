/*
 * Copyright (C) 2025 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.service.sjukfall.komplettering;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.inera.intyg.rehabstod.integration.wc.service.WcRestIntegrationService;
import se.inera.intyg.rehabstod.integration.wc.service.dto.UnansweredCommunicationRequest;
import se.inera.intyg.rehabstod.web.model.SjukfallPatient;

@Service
@RequiredArgsConstructor
public class UnansweredQAsInfoDecoratorImpl implements UnansweredQAsInfoDecorator {

  private static final int MAX_DAYS_OF_UNANSWERED_COMMUNICATION = 90;

    private final WcRestIntegrationService wcRestIntegrationService;

    @Override
    public void updateSjukfallPatientWithQAs(List<SjukfallPatient> patientSjukfallList, String patientId) {

      final var perIntyg = wcRestIntegrationService.getUnansweredCommunicationForPatients(
          UnansweredCommunicationRequest.builder()
              .patientIds(List.of(patientId))
              .maxDaysOfUnansweredCommunication(MAX_DAYS_OF_UNANSWERED_COMMUNICATION)
              .build());

        patientSjukfallList
            .forEach(sjukfallPatient -> sjukfallPatient.getIntyg()
                .stream()
                .filter(patientData -> !patientData.isOtherVardgivare() && !patientData.isOtherVardenhet())
                .forEach(
                patientData -> {
                    var unAnsweredQAs = perIntyg.getUnansweredQAsMap().get(patientData.getIntygsId());
                    if (unAnsweredQAs != null) {
                        patientData.setObesvaradeKompl(unAnsweredQAs.getComplement());
                        patientData.setUnansweredOther(unAnsweredQAs.getOthers());
                    } else {
                        patientData.setObesvaradeKompl(0);
                        patientData.setUnansweredOther(0);
                    }
                }));

    }
}
