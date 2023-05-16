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
import se.inera.intyg.rehabstod.integration.it.dto.CreateSickLeaveRequestDTO;
import se.inera.intyg.rehabstod.integration.it.dto.CreateSickLeaveResponseDTO;
import se.inera.intyg.rehabstod.integration.it.dto.PopulateFiltersRequestDTO;
import se.inera.intyg.rehabstod.integration.it.dto.PopulateFiltersResponseDTO;
import se.inera.intyg.rehabstod.integration.it.dto.SickLeaveLengthInterval;
import se.inera.intyg.rehabstod.integration.it.dto.SickLeavesRequestDTO;
import se.inera.intyg.rehabstod.integration.it.dto.SickLeavesResponseDTO;
import se.inera.intyg.rehabstod.integration.it.dto.TestDataOptionsDTO;
import se.inera.intyg.rehabstod.integration.it.service.IntygstjanstRestIntegrationService;

@Profile("rhs-it-stub")
@Service
public class IntygstjanstRestIntegrationServiceStub implements IntygstjanstRestIntegrationService {

    @Autowired
    private RSTestIntygStub rsTestIntygStub;
    private static final String DEFAULT_TEST_DATA_MESSAGE = "Test data not generated, deactivate stub";
    private static final int[] YEAR_SEPARATOR = {0, 4};
    private static final int[] MONTH_SEPARATOR = {4, 6};
    private static final int[] DAY_SEPARATOR = {6, 8};

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
                        && isDiagnosisCodeIncluded(request.getDiagnosisChapters(), sickLeave.getDiagnosKod().getCleanedCode())
                        && (request.getSickLeaveLengthIntervals().size() == 0
                        || isSickLeaveLengthIncluded(request.getSickLeaveLengthIntervals(), sickLeave.getDagar()))
                        && filterOnPatientAge(request, sickLeave.getPatient().getId())
                )
                .collect(Collectors.toList())
        );
    }

    private boolean filterOnPatientAge(SickLeavesRequestDTO request, String patientId) {
        if (request.getFromPatientAge() == null || request.getToPatientAge() == null) {
            return true;
        }
        final var patientAge = LocalDate.of(
                Integer.parseInt(patientId.substring(YEAR_SEPARATOR[0], YEAR_SEPARATOR[1])),
                Integer.parseInt(patientId.substring(MONTH_SEPARATOR[0], MONTH_SEPARATOR[1])),
                Integer.parseInt(patientId.substring(DAY_SEPARATOR[0], DAY_SEPARATOR[1])))
            .getYear();
        return request.getFromPatientAge() <= patientAge && request.getToPatientAge() >= patientAge;
    }

    @Override
    public PopulateFiltersResponseDTO getPopulatedFiltersForActiveSickLeaves(PopulateFiltersRequestDTO request) {
        final var sickLeaves = rsTestIntygStub.getActiveSickLeaveData();
        final var diagnosisChapters = rsTestIntygStub.getDiagnosisChapterList();
        return new PopulateFiltersResponseDTO(
            getDoctorsFromSickLeaves(sickLeaves), diagnosisChapters);
    }

    @Override
    public String getDefaultTestData() {
        return DEFAULT_TEST_DATA_MESSAGE;
    }

    @Override
    public CreateSickLeaveResponseDTO createSickleave(CreateSickLeaveRequestDTO createSickLeaveRequestDTO) {
        return null;
    }

    @Override
    public TestDataOptionsDTO getTestDataOptions() {
        return null;
    }

    private boolean isDiagnosisCodeIncluded(List<DiagnosKapitel> diagnosisChapters, String diagnosisCode) {
        return diagnosisChapters.size() == 0
            || diagnosisChapters
            .stream().anyMatch(
                (diagnosisChapter) ->
                    diagnosisChapter.getFrom().getLetter() <= diagnosisCode.charAt(0)
                        && diagnosisChapter.getTo().getLetter() >= diagnosisCode.charAt(0)
                        && diagnosisChapter.getFrom().getNumber() <= Integer.parseInt(diagnosisCode.substring(1, 2))
                        && diagnosisChapter.getTo().getNumber() >= Integer.parseInt(diagnosisCode.substring(1, 2))
            );
    }

    private boolean isSickLeaveLengthIncluded(List<SickLeaveLengthInterval> intervals, int days) {
        return intervals
            .stream()
            .anyMatch((interval) -> interval.getFrom() <= days && interval.getTo() >= days);
    }

    private static List<Lakare> getDoctorsFromSickLeaves(List<SjukfallEnhet> sickLeaves) {
        return sickLeaves
            .stream()
            .map(SjukfallEnhet::getLakare)
            .filter(distinctByKey(Lakare::getId))
            .collect(Collectors.toList());
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }
}
