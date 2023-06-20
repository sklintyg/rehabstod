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

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import se.inera.intyg.rehabstod.integration.wc.service.WcRestIntegrationService;
import se.inera.intyg.rehabstod.integration.wc.service.dto.UnansweredCommunicationRequest;
import se.inera.intyg.rehabstod.integration.wc.service.dto.UnansweredQAs;
import se.inera.intyg.rehabstod.logging.SickLeaveLogMessageFactory;
import se.inera.intyg.rehabstod.web.model.SjukfallEnhet;

@Service
public class UnansweredCommunicationDecoratorServiceImpl implements UnansweredCommunicationDecoratorService {

    private final WcRestIntegrationService wcRestIntegrationService;
    private static final Logger LOG = LoggerFactory.getLogger(UnansweredCommunicationDecoratorServiceImpl.class);

    @Value("${wc.getadditions.max.age.days:90}")
    private int maxDaysOfUnansweredCommunication;

    public UnansweredCommunicationDecoratorServiceImpl(WcRestIntegrationService wcRestIntegrationService) {
        this.wcRestIntegrationService = wcRestIntegrationService;
    }

    @Override
    public boolean decorate(List<SjukfallEnhet> sickLeaves) {
        final var logFactory = new SickLeaveLogMessageFactory(System.currentTimeMillis());
        logFactory.setStartTimer(System.currentTimeMillis());
        final var patientIds = sickLeaves
            .stream()
            .map((sickLeave) -> sickLeave.getPatient().getId())
            .collect(Collectors.toList());

        final var response = wcRestIntegrationService.getUnansweredCommunicationForPatients(
            new UnansweredCommunicationRequest(maxDaysOfUnansweredCommunication, patientIds)
        );

        if (response.isUnansweredCommunicationError()) {
            return false;
        }

        sickLeaves.forEach((sickLeave) -> decorateSickLeave(sickLeave, response.getUnansweredQAsMap()));
        LOG.info(logFactory.message(SickLeaveLogMessageFactory.ADD_UNANSWERED_COMMUNICATION, sickLeaves.size()));
        return true;
    }

    private void decorateSickLeave(SjukfallEnhet sickLeave, Map<String, UnansweredQAs> unansweredQAsMap) {
        final var totalUnansweredQAs = sickLeave.getIntygLista()
            .stream()
            .filter(unansweredQAsMap::containsKey)
            .map(unansweredQAsMap::get)
            .reduce(
                (a, b) -> new UnansweredQAs(
                    a.getComplement() + b.getComplement(),
                    a.getOthers() + b.getOthers()
                )
            );

        if (totalUnansweredQAs.isEmpty()) {
            return;
        }

        sickLeave.setUnansweredOther(totalUnansweredQAs.get().getOthers());
        sickLeave.setObesvaradeKompl(totalUnansweredQAs.get().getComplement());
    }
}
