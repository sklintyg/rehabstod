/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
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
import se.inera.intyg.rehabstod.integration.wc.service.dto.UnansweredQAs;
import se.inera.intyg.rehabstod.web.model.AGCertificate;
import se.inera.intyg.rehabstod.web.model.LUCertificate;
import se.inera.intyg.rehabstod.web.model.PatientData;
import se.inera.intyg.rehabstod.web.model.SjukfallEnhet;
import se.inera.intyg.rehabstod.web.model.SjukfallPatient;

@Service
public class UnansweredQAsInfoDecoratorImpl implements UnansweredQAsInfoDecorator {

    @Autowired
    private WcIntegrationService wcIntegrationService;

    @Override
    public void updateSjukfallEnhetQAs(List<SjukfallEnhet> sjukfallList) {

        final List<String> idList = sjukfallList.stream()
            .flatMap(sfe -> sfe.getIntygLista().stream())
            .collect(Collectors.toList());

        final Map<String, UnansweredQAs> perIntyg = wcIntegrationService.getCertificateAdditionsForIntyg(idList);

        var dummy = new UnansweredQAs(0, 0);
        sjukfallList.forEach(
            sjukfallEnhet -> {
                sjukfallEnhet.setObesvaradeKompl(
                sjukfallEnhet.getIntygLista().stream()
                    .mapToInt(intygsId -> Optional.ofNullable(perIntyg.get(intygsId)).orElse(dummy).getComplement())
                    .sum());
                sjukfallEnhet.setUnansweredOther(
                    sjukfallEnhet.getIntygLista().stream()
                        .mapToInt(intygsId -> Optional.ofNullable(perIntyg.get(intygsId)).orElse(dummy).getOthers())
                        .sum());
            });
    }

    @Override
    public void updateSjukfallPatientWithQAs(List<SjukfallPatient> patientSjukfallList) {

        // Get all intygsidn to query for kompletteringsinfo, excluding sjf intyg
        final List<String> idList = patientSjukfallList.stream()
            .flatMap(sjukfallPatient -> sjukfallPatient.getIntyg().stream())
            .filter(patientData -> !patientData.isOtherVardgivare() && !patientData.isOtherVardenhet())
            .map(PatientData::getIntygsId)
            .collect(Collectors.toList());

        final Map<String, UnansweredQAs> perIntyg = wcIntegrationService.getCertificateAdditionsForIntyg(idList);

        patientSjukfallList
            .forEach(sjukfallPatient -> sjukfallPatient.getIntyg().forEach(
                patientData -> {
                    var unAnsweredQAs = Optional.ofNullable(perIntyg.get(patientData.getIntygsId())).orElse(null);
                    if (unAnsweredQAs != null) {
                        patientData.setObesvaradeKompl(unAnsweredQAs.getComplement());
                        patientData.setUnansweredOther(unAnsweredQAs.getOthers());
                    } else {
                        patientData.setObesvaradeKompl(0);
                        patientData.setUnansweredOther(0);
                    }
                }));

    }

    @Override
    public void updateLUCertificatesWithQAs(List<LUCertificate> luCertificate) {

        final var idList = luCertificate.stream().map(LUCertificate::getCertificateId).collect(Collectors.toList());

        final var certificateAdditions = wcIntegrationService.getCertificateAdditionsForIntyg(idList);

        luCertificate
            .forEach(
                cert -> {
                    var unAnsweredQAs = Optional.ofNullable(certificateAdditions.get(cert.getCertificateId())).orElse(null);
                    if (unAnsweredQAs != null) {
                        cert.setUnAnsweredComplement(unAnsweredQAs.getComplement());
                        cert.setUnAnsweredOther(unAnsweredQAs.getOthers());
                    } else {
                        cert.setUnAnsweredComplement(0);
                        cert.setUnAnsweredOther(0);
                    }
                });
    }

    @Override
    public void updateAGCertificatesWithQAs(List<AGCertificate> agCertificate) {

        final var idList = agCertificate.stream().map(AGCertificate::getCertificateId).collect(Collectors.toList());

        final var certificateAdditions = wcIntegrationService.getCertificateAdditionsForIntyg(idList);

        agCertificate
            .forEach(
                cert -> {
                    var unAnsweredQAs = Optional.ofNullable(certificateAdditions.get(cert.getCertificateId())).orElse(null);
                    if (unAnsweredQAs != null) {
                        cert.setUnAnsweredComplement(unAnsweredQAs.getComplement());
                        cert.setUnAnsweredOther(unAnsweredQAs.getOthers());
                    } else {
                        cert.setUnAnsweredComplement(0);
                        cert.setUnAnsweredOther(0);
                    }
                });
    }

}
