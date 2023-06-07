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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.logmessages.ActivityType;
import se.inera.intyg.infra.logmessages.ResourceType;
import se.inera.intyg.infra.sjukfall.dto.IntygParametrar;
import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.integration.it.dto.SickLeaveLengthInterval;
import se.inera.intyg.rehabstod.integration.it.dto.SickLeavesRequestDTO;
import se.inera.intyg.rehabstod.integration.it.service.IntygstjanstRestIntegrationService;
import se.inera.intyg.rehabstod.logging.SickLeaveLogMessageFactory;
import se.inera.intyg.rehabstod.service.Urval;
import se.inera.intyg.rehabstod.service.diagnos.dto.DiagnosKapitel;
import se.inera.intyg.rehabstod.service.diagnos.dto.DiagnosKategori;
import se.inera.intyg.rehabstod.service.exceptions.SRSServiceException;
import se.inera.intyg.rehabstod.service.monitoring.MonitoringLogService;
import se.inera.intyg.rehabstod.service.pu.PuService;
import se.inera.intyg.rehabstod.service.sjukfall.dto.GetActiveSickLeavesResponseDTO;
import se.inera.intyg.rehabstod.service.sjukfall.dto.UnansweredCommunicationFilterType;
import se.inera.intyg.rehabstod.service.sjukfall.mappers.SjukfallEngineMapper;
import se.inera.intyg.rehabstod.service.sjukfall.nameresolver.SjukfallEmployeeNameResolver;
import se.inera.intyg.rehabstod.service.sjukfall.srs.RiskPredictionService;
import se.inera.intyg.rehabstod.service.sjukfall.util.PatientIdEncryption;
import se.inera.intyg.rehabstod.service.user.UserService;
import se.inera.intyg.rehabstod.web.controller.api.dto.SickLeavesFilterRequestDTO;
import se.inera.intyg.rehabstod.web.controller.api.util.ControllerUtil;
import se.inera.intyg.rehabstod.web.model.SjukfallEnhet;

@Service
public class GetActiveSickLeavesServiceImpl implements GetActiveSickLeavesService {

    private final UserService userService;
    private final MonitoringLogService monitoringLogService;
    private final SjukfallEngineMapper sjukfallEngineMapper;
    private final PdlLogSickLeavesService pdlLogSickLeavesService;
    private final IntygstjanstRestIntegrationService intygstjanstRestIntegrationService;
    private final SjukfallEmployeeNameResolver sjukfallEmployeeNameResolver;
    private final PatientIdEncryption patientIdEncryption;
    private final PuService puService;
    private final RiskPredictionService riskPredictionService;
    private final UnansweredCommunicationDecoratorService unansweredCommunicationDecoratorService;

    private static final Logger LOG = LoggerFactory.getLogger(GetActiveSickLeavesServiceImpl.class);

    @Autowired
    public GetActiveSickLeavesServiceImpl(UserService userService, MonitoringLogService monitoringLogService,
                                          SjukfallEngineMapper sjukfallEngineMapper, PdlLogSickLeavesService pdlLogSickLeavesService,
                                          IntygstjanstRestIntegrationService intygstjanstRestIntegrationService,
                                          SjukfallEmployeeNameResolver sjukfallEmployeeNameResolver,
                                          PatientIdEncryption patientIdEncryption, PuService puService,
                                          RiskPredictionService riskPredictionService,
                                          UnansweredCommunicationDecoratorService unansweredCommunicationDecoratorService) {
        this.userService = userService;
        this.monitoringLogService = monitoringLogService;
        this.sjukfallEngineMapper = sjukfallEngineMapper;
        this.pdlLogSickLeavesService = pdlLogSickLeavesService;
        this.intygstjanstRestIntegrationService = intygstjanstRestIntegrationService;
        this.sjukfallEmployeeNameResolver = sjukfallEmployeeNameResolver;
        this.riskPredictionService = riskPredictionService;
        this.patientIdEncryption = patientIdEncryption;
        this.puService = puService;
        this.unansweredCommunicationDecoratorService = unansweredCommunicationDecoratorService;
    }

    @Override
    public GetActiveSickLeavesResponseDTO get(SickLeavesFilterRequestDTO filterRequest, boolean includeParameters) {
        final var user = userService.getUser();
        final var careUnitId = ControllerUtil.getEnhetsIdForQueryingIntygstjansten(user);
        final var unitId = user.isValdVardenhetMottagning() ? user.getValdVardenhet().getId() : null;
        final var certificateParameters = getCertificateParameters(user);
        final var request = getRequest(user, unitId, careUnitId, filterRequest, includeParameters);

        LOG.debug("Getting sick leaves for unit {}", careUnitId);
        final var logFactory = new SickLeaveLogMessageFactory(System.currentTimeMillis());
        final var response = intygstjanstRestIntegrationService.getActiveSickLeaves(request);
        LOG.info(logFactory.message(SickLeaveLogMessageFactory.GET_ACTIVE_SICK_LEAVES, response.getContent().size()));
        final var convertedSickLeaves = convertSickLeaves(response.getContent(), certificateParameters);

        logFactory.setStartTimer(System.currentTimeMillis());
        sjukfallEmployeeNameResolver.enrichWithHsaEmployeeNames(convertedSickLeaves);
        sjukfallEmployeeNameResolver.updateDuplicateDoctorNamesWithHsaId(convertedSickLeaves);
        LOG.info(logFactory.message(SickLeaveLogMessageFactory.ADD_DOCTOR_NAMES, convertedSickLeaves.size()));

        final var filteredSickLeaves = filterSickLeaves(convertedSickLeaves, filterRequest.getUnansweredCommunicationFilterTypeId());

        logFactory.setStartTimer(System.currentTimeMillis());
        final var hasDecoratedWithUnansweredCommunications = unansweredCommunicationDecoratorService.decorate(convertedSickLeaves);
        LOG.info(logFactory.message(SickLeaveLogMessageFactory.ADD_UNANSWERED_COMMUNICATION, convertedSickLeaves.size()));

        logFactory.setStartTimer(System.currentTimeMillis());
        final var hasDecoratedWithSRSInfo = decorateWithSRSInfo(filteredSickLeaves);
        LOG.info(logFactory.message(SickLeaveLogMessageFactory.ADD_SRS_RISK, filteredSickLeaves.size()));

        LOG.debug("Logging that sick leaves have been fetched");
        performMonitorLogging(filteredSickLeaves, user.getHsaId(), unitId != null ? unitId : careUnitId);
        pdlLogSickLeavesService.log(filteredSickLeaves, ActivityType.READ, ResourceType.RESOURCE_TYPE_SJUKFALL);
        filteredSickLeaves.forEach(
            sickLeave -> sickLeave.setEncryptedPatientId(patientIdEncryption.encrypt(sickLeave.getPatient().getId())));

        return new GetActiveSickLeavesResponseDTO(
                convertedSickLeaves,
                !hasDecoratedWithSRSInfo,
                !hasDecoratedWithUnansweredCommunications
        );
    }

    private List<SjukfallEnhet> filterSickLeaves(List<SjukfallEnhet> sickLeaves, String unansweredCommunicationFilterTypeId) {
        return sickLeaves
                .stream()
                .filter((sickLeave) -> filterOnUnansweredCommunication(sickLeave, unansweredCommunicationFilterTypeId))
                .collect(Collectors.toList());
    }

    private boolean filterOnUnansweredCommunication(SjukfallEnhet sickLeave, String filterTypeId) {
        if (filterTypeId == null) {
            return false;
        }

        switch (UnansweredCommunicationFilterType.fromId(filterTypeId)) {
            case UNANSWERED_COMMUNICATION_FILTER_TYPE_1:
                return sickLeave.getUnansweredOther() + sickLeave.getObesvaradeKompl() == 0;
            case UNANSWERED_COMMUNICATION_FILTER_TYPE_2:
                return sickLeave.getUnansweredOther() + sickLeave.getObesvaradeKompl() > 0;
            case UNANSWERED_COMMUNICATION_FILTER_TYPE_3:
                return sickLeave.getObesvaradeKompl() > 0;
            case UNANSWERED_COMMUNICATION_FILTER_TYPE_4:
                return sickLeave.getUnansweredOther() > 0;
            default:
                return false;
        }

    }

    private boolean decorateWithSRSInfo(List<SjukfallEnhet> sickLeaves) {
        try {
            riskPredictionService.updateWithRiskPredictions(sickLeaves);
        } catch (SRSServiceException e) {
            return false;
        }
        return true;
    }

    private SickLeavesRequestDTO getRequest(
        RehabstodUser user, String unitId, String careUnitId, SickLeavesFilterRequestDTO filterRequest, boolean includeParameters
    ) {
        final var request = new SickLeavesRequestDTO();

        request.setMaxCertificateGap(includeParameters ? ControllerUtil.getMaxGlapp(user) : 0);
        request.setMaxDaysSinceSickLeaveCompleted(includeParameters ? ControllerUtil.getMaxDagarSedanSjukfallAvslut(user) : 0);
        request.setUnitId(unitId);
        request.setCareUnitId(careUnitId);
        request.setDoctorIds(getFilteringDoctorIds(user, filterRequest.getDoctorIds()));
        request.setSickLeaveLengthIntervals(convertSickLeaveLengthIntervals(filterRequest.getSickLeaveLengthIntervals()));
        request.setDiagnosisChapters(convertDiagnosisChapters(filterRequest.getDiagnosisChapters()));
        request.setFromPatientAge(filterRequest.getFromPatientAge());
        request.setToPatientAge(filterRequest.getToPatientAge());
        request.setProtectedPersonFilterId(puService.shouldFilterSickLeavesOnProtectedPerson(user) ? null : user.getHsaId());
        request.setFromSickLeaveEndDate(filterRequest.getFromSickLeaveEndDate());
        request.setToSickLeaveEndDate(filterRequest.getToSickLeaveEndDate());
        request.setRekoStatusTypeIds(filterRequest.getRekoStatusTypeIds());
        request.setOccupationTypeIds(filterRequest.getOccupationTypeIds());
        request.setTextSearch(filterRequest.getTextSearch());
        return request;
    }

    private List<String> getFilteringDoctorIds(RehabstodUser user, List<String> filterDoctorIds) {
        final var list = new ArrayList<>(filterDoctorIds);
        if (user.getUrval().equals(Urval.ISSUED_BY_ME)) {
            list.add(user.getHsaId());
        }
        return list;
    }

    private List<SickLeaveLengthInterval> convertSickLeaveLengthIntervals(
        List<se.inera.intyg.rehabstod.service.sjukfall.dto.SickLeaveLengthInterval> intervals) {
        return intervals.stream()
            .map((interval) -> new SickLeaveLengthInterval(interval.getFrom(), interval.getTo()))
            .collect(Collectors.toList());
    }

    private List<se.inera.intyg.infra.sjukfall.dto.DiagnosKapitel> convertDiagnosisChapters(List<DiagnosKapitel> diagnosisChapters) {
        return diagnosisChapters
            .stream()
            .map(
                (diagnosisChapter) ->
                    new se.inera.intyg.infra.sjukfall.dto.DiagnosKapitel(
                        convertDiagnosisCategory(diagnosisChapter.getFrom()),
                        convertDiagnosisCategory(diagnosisChapter.getTo()),
                        diagnosisChapter.getName()
                    )
            )
            .collect(Collectors.toList());
    }

    private se.inera.intyg.infra.sjukfall.dto.DiagnosKategori convertDiagnosisCategory(DiagnosKategori diagnosisCategory) {
        return new se.inera.intyg.infra.sjukfall.dto.DiagnosKategori(diagnosisCategory.getLetter(), diagnosisCategory.getNumber());
    }

    private List<SjukfallEnhet> convertSickLeaves(
        List<se.inera.intyg.infra.sjukfall.dto.SjukfallEnhet> sickLeavesFromIT, IntygParametrar certificateParameters
    ) {
        return sickLeavesFromIT.stream()
            .map(sickLeave -> sjukfallEngineMapper.mapToSjukfallEnhetDto(
                    sickLeave,
                    certificateParameters.getMaxAntalDagarSedanSjukfallAvslut(),
                    certificateParameters.getAktivtDatum()
                )
            )
            .collect(Collectors.toList());
    }

    private IntygParametrar getCertificateParameters(RehabstodUser user) {
        return new IntygParametrar(
            ControllerUtil.getMaxGlapp(user),
            ControllerUtil.getMaxDagarSedanSjukfallAvslut(user),
            LocalDate.now()
        );
    }

    private void performMonitorLogging(List<SjukfallEnhet> sickLeaves, String hsaId, String unitId) {
        if (sickLeaves == null) {
            return;
        }

        monitoringLogService.logUserViewedSjukfall(
            hsaId,
            sickLeaves.size(),
            unitId
        );
    }
}
