/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import se.inera.intyg.rehabstod.integration.wc.service.WcIntegrationService;
import se.inera.intyg.rehabstod.web.model.SjukfallEnhet;
import se.inera.intyg.rehabstod.web.model.SjukfallPatient;

@Service
public class KompletteringInfoDecoratorImpl implements KompletteringInfoDecorator {

    @Autowired
    private WcIntegrationService wcIntegrationService;

    @Override
    public void updateSjukfallEnhetKompletteringar(List<SjukfallEnhet> sjukfallList) {

        final List<String> idList = sjukfallList.stream()
                .flatMap(sfe -> sfe.getIntygLista().stream())
                .collect(Collectors.toList());

        final Map<String, Integer> perIntyg = wcIntegrationService.getCertificateAdditionsForIntyg(idList);

        sjukfallList.stream().forEach(
                sjukfallEnhet -> sjukfallEnhet.setObesvaradeKompl(
                        sjukfallEnhet.getIntygLista().stream()
                                .mapToInt(intygsId -> Optional.ofNullable(perIntyg.get(intygsId)).orElse(0))
                                .sum()));
    }

    @Override
    public void updateSjukfallPatientKompletteringar(List<SjukfallPatient> patientSjukfallList) {

        // Get all intygsidn to query for kompletteringsinfo, excluding sjf intyg
        final List<String> idList = patientSjukfallList.stream()
                .flatMap(sjukfallPatient -> sjukfallPatient.getIntyg().stream())
                .filter(patientData -> !patientData.isOtherVardgivare() && !patientData.isOtherVardenhet())
                .map(patientData -> patientData.getIntygsId())
                .collect(Collectors.toList());

        final Map<String, Integer> perIntyg = wcIntegrationService.getCertificateAdditionsForIntyg(idList);

        patientSjukfallList.stream()
                .forEach(sjukfallPatient -> sjukfallPatient.getIntyg()
                        .stream().forEach(
                                patientData -> patientData.setObesvaradeKompl(perIntyg.get(patientData.getIntygsId()))));

    }

}