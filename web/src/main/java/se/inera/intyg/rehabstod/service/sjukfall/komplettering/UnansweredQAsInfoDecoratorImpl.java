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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import se.inera.intyg.rehabstod.integration.wc.service.WcRestIntegrationService;
import se.inera.intyg.rehabstod.integration.wc.service.dto.UnansweredCommunicationRequest;
import se.inera.intyg.rehabstod.web.model.SjukfallPatient;

@Service
@RequiredArgsConstructor
public class UnansweredQAsInfoDecoratorImpl implements UnansweredQAsInfoDecorator {

    @Value("${wc.getadditions.max.age.days:90}")
    private int maxDaysOfUnansweredCommunication;

    private final WcRestIntegrationService wcRestIntegrationService;

    @Override
    public void updateSjukfallPatientWithQAs(List<SjukfallPatient> patientSjukfallList, String patientId) {

      final var unansweredQAsResponse = wcRestIntegrationService.getUnansweredCommunicationForPatients(
          UnansweredCommunicationRequest.builder()
              .patientIds(List.of(patientId))
              .maxDaysOfUnansweredCommunication(maxDaysOfUnansweredCommunication)
              .build());


        patientSjukfallList.stream()
            .flatMap(sjukfallPatient -> sjukfallPatient.getIntyg().stream())
            .forEach(patientData -> {
                    var unAnsweredQAs = unansweredQAsResponse.getUnansweredQAsMap().get(patientData.getIntygsId());
                    if (!patientData.isOtherVardgivare() && !patientData.isOtherVardenhet() && unAnsweredQAs != null) {
                        patientData.setObesvaradeKompl(unAnsweredQAs.getComplement());
                        patientData.setUnansweredOther(unAnsweredQAs.getOthers());
                    } else {
                        patientData.setObesvaradeKompl(0);
                        patientData.setUnansweredOther(0);
                    }
                });

    }
}
