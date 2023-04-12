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
package se.inera.intyg.rehabstod.integration.it.stub;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.certificate.dto.BaseCertificate;
import se.inera.intyg.infra.certificate.dto.DiagnosedCertificate;
import se.inera.intyg.infra.certificate.dto.SickLeaveCertificate;
import se.inera.intyg.infra.sjukfall.dto.DiagnosKapitel;
import se.inera.intyg.infra.sjukfall.dto.Lakare;
import se.inera.intyg.infra.sjukfall.dto.SjukfallEnhet;
import se.inera.intyg.rehabstod.integration.it.dto.PopulateFiltersRequestDTO;
import se.inera.intyg.rehabstod.integration.it.dto.PopulateFiltersResponseDTO;
import se.inera.intyg.rehabstod.integration.it.dto.SickLeavesRequestDTO;
import se.inera.intyg.rehabstod.integration.it.dto.SickLeavesResponseDTO;
import se.inera.intyg.rehabstod.integration.it.service.IntygstjanstRestIntegrationService;

@Profile("rhs-it-stub")
@Service
public class IntygstjanstRestIntegrationServiceStub implements IntygstjanstRestIntegrationService {

    @Autowired
    private RSTestIntygStub rsTestIntygStub;

    @Override
    public List<DiagnosedCertificate> getDiagnosedCertificatesForCareUnit(List<String> units, List<String> certificateTypes,
        LocalDate fromDate, LocalDate toDate, List<String> doctorIds) {
        return rsTestIntygStub.getLUCertificateData();
    }

    @Override
    public List<DiagnosedCertificate> getDiagnosedCertificatesForPerson(String personId, List<String> certificateTypes,
        List<String> units) {
        return getDiagnosedCertificatesForPerson(personId, certificateTypes, null, null, units);
    }

    @Override
    public List<DiagnosedCertificate> getDiagnosedCertificatesForPerson(String personId, List<String> certificateTypes, LocalDate fromDate,
        LocalDate toDate, List<String> units) {

        var luCertificateData = rsTestIntygStub.getLUCertificateData();
        return luCertificateData.stream().filter(c -> personId.equals(c.getPersonId())).collect(Collectors.toList());
    }

    @Override
    public List<SickLeaveCertificate> getSickLeaveCertificatesForPerson(String personId, List<String> certificateTypes,
        List<String> units) {
        return getSickLeaveCertificatesForPerson(personId, certificateTypes, null, null, units);
    }

    @Override
    public List<SickLeaveCertificate> getSickLeaveCertificatesForPerson(String personId, List<String> certificateTypes, LocalDate fromDate,
        LocalDate toDate, List<String> units) {
        var agCertificateData = rsTestIntygStub.getAGCertificateData();
        return agCertificateData.stream().filter(c -> personId.equals(c.getPersonId())).collect(Collectors.toList());
    }

    @Override
    public List<String> getSigningDoctorsForUnit(List<String> units, List<String> certificateTypes) {
        return rsTestIntygStub.getLUCertificateData().stream().map(BaseCertificate::getPersonalFullName).distinct()
            .collect(Collectors.toList());
    }

    @Override
    public SickLeavesResponseDTO getActiveSickLeaves(SickLeavesRequestDTO request) {
        return new SickLeavesResponseDTO(
            rsTestIntygStub.getActiveSickLeaveData()
            .stream()
            .filter(
                (sickLeave) -> (request.getDoctorIds().size() == 0
                    || request.getDoctorIds().contains(sickLeave.getLakare().getId()))
                    && sickLeave.getDagar() >= request.getFromSickLeaveLength()
                    && sickLeave.getDagar() <= request.getToSickLeaveLength()
                    && isDiagnosisCodeIncluded(request.getDiagnosisChapters(), sickLeave.getDiagnosKod().getCleanedCode())
            )
            .collect(Collectors.toList())
        );
    }

    @Override
    public PopulateFiltersResponseDTO getPopulatedFiltersForActiveSickLeaves(PopulateFiltersRequestDTO request) {
        final var sickLeaves = rsTestIntygStub.getActiveSickLeaveData();
        return new PopulateFiltersResponseDTO(
            getDoctorsFromSickLeaves(sickLeaves), Collections.emptyList());
    }

    private boolean isDiagnosisCodeIncluded(List<DiagnosKapitel> diagnosisChapters, String diagnosisCode) {
        return diagnosisChapters.size() == 0
            || diagnosisChapters
            .stream().anyMatch(
                (diagnosisChapter) ->
                    diagnosisChapter.getFrom().getLetter() == diagnosisCode.charAt(0)
                    && diagnosisChapter.getFrom().getNumber() <= Integer.parseInt(diagnosisCode.substring(1))
                    && diagnosisChapter.getTo().getNumber() >= Integer.parseInt(diagnosisCode.substring(1))
            );
    }

    private static List<Lakare> getDoctorsFromSickLeaves(List<SjukfallEnhet> sickLeaves) {
        return sickLeaves
            .stream()
            .map(SjukfallEnhet::getLakare)
            .filter(distinctByKey(Lakare::getId))
            .collect(Collectors.toList());
    }

    /*private static List<DiagnosKapitel> getDiagnosesFromSickLeaves(List<SjukfallEnhet> sickLeaves) {
        return sickLeaves
            .stream()
            .map(SjukfallEnhet::getDiagnosKod)
            .filter(distinctByKey(DiagnosKod::getCleanedCode))
            .collect(Collectors.toList());
    }*/

    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }
}
