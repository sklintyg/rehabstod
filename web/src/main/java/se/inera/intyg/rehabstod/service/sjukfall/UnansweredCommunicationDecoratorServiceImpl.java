/*
 * Copyright (C) 2023 Inera AB (http://www.inera.se)
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

package se.inera.intyg.rehabstod.service.sjukfall;

import se.inera.intyg.rehabstod.integration.wc.service.WcRestIntegrationService;
import se.inera.intyg.rehabstod.integration.wc.service.dto.UnansweredQAs;
import se.inera.intyg.rehabstod.web.model.SjukfallEnhet;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UnansweredCommunicationDecoratorServiceImpl implements UnansweredCommunicationDecoratorService {

    private final WcRestIntegrationService wcRestIntegrationService;

    public UnansweredCommunicationDecoratorServiceImpl(WcRestIntegrationService wcRestIntegrationService) {
        this.wcRestIntegrationService = wcRestIntegrationService;
    }

    @Override
    public boolean decorate(List<SjukfallEnhet> sickLeaves) {
        final var patientIds = sickLeaves
                .stream()
                .map((sickLeave) -> sickLeave.getPatient().getId())
                .collect(Collectors.toList());

        final var response = wcRestIntegrationService.getUnansweredCommunicationForPatients(patientIds);

        sickLeaves.forEach((sickLeave) -> decorateSickLeave(sickLeave, response.getUnansweredQAsMap()));

        return !response.isUnansweredCommunicationError();
    }

    private void decorateSickLeave(SjukfallEnhet sickLeave, Map<String, UnansweredQAs> unansweredQAsMap) {
        final var certificatesWithQAs = sickLeave.getIntygLista()
                .stream()
                .filter(unansweredQAsMap::containsKey)
                .collect(Collectors.toList());

        if (certificatesWithQAs.size() == 0) {
            return;
        }

        final var unansweredOther = sumQA(
                certificatesWithQAs, (certificateId) -> unansweredQAsMap.get(certificateId).getOthers()
        );
        final var unansweredComplement = sumQA(
                certificatesWithQAs, (certificateId) -> unansweredQAsMap.get(certificateId).getComplement()
        );

        sickLeave.setUnansweredOther(unansweredOther);
        sickLeave.setObesvaradeKompl(unansweredComplement);
    }

    private int sumQA(List<String> certificateIds, Function<String, Integer> function) {
        return certificateIds
                .stream()
                .map(function)
                .mapToInt(Integer::intValue)
                .sum();

    }

}
