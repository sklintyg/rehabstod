/**
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
 *
 * This file is part of rehabstod (https://github.com/sklintyg/rehabstod).
 *
 * Rehabstod is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Rehabstod is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.intyg.rehabstod.service.sjukfall;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.sjukfall.dto.DiagnosKod;
import se.inera.intyg.infra.sjukfall.dto.Formaga;
import se.inera.intyg.infra.sjukfall.dto.IntygData;
import se.inera.intyg.infra.sjukfall.dto.IntygParametrar;
import se.inera.intyg.infra.sjukfall.dto.SjukfallEnhet;
import se.inera.intyg.infra.sjukfall.dto.SjukfallIntyg;
import se.inera.intyg.infra.sjukfall.dto.SjukfallPatient;
import se.inera.intyg.infra.sjukfall.services.SjukfallEngineService;
import se.inera.intyg.rehabstod.integration.it.service.IntygstjanstIntegrationService;
import se.inera.intyg.rehabstod.service.Urval;
import se.inera.intyg.rehabstod.service.diagnos.DiagnosFactory;
import se.inera.intyg.rehabstod.service.monitoring.MonitoringLogService;
import se.inera.intyg.rehabstod.service.sjukfall.dto.SjukfallSummary;
import se.inera.intyg.rehabstod.service.sjukfall.nameresolver.SjukfallEmployeeNameResolver;
import se.inera.intyg.rehabstod.service.sjukfall.pu.SjukfallPuService;
import se.inera.intyg.rehabstod.service.sjukfall.statistics.StatisticsCalculator;
import se.inera.intyg.rehabstod.web.controller.api.dto.GetSjukfallRequest;
import se.inera.intyg.rehabstod.web.model.Diagnos;
import se.inera.intyg.rehabstod.web.model.Lakare;
import se.inera.intyg.rehabstod.web.model.Patient;
import se.inera.intyg.rehabstod.web.model.PatientData;
import se.inera.intyg.rehabstod.web.model.SjukfallEnhetRS;
import se.inera.intyg.rehabstod.web.model.SjukfallPatientRS;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.IntygsData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by eriklupander on 2016-02-01.
 */

@Service("sjukfallService")
public class SjukfallServiceImpl implements SjukfallService {

    private static final Logger LOG = LoggerFactory.getLogger(SjukfallServiceImpl.class);

    @Autowired
    private IntygstjanstIntegrationService intygstjanstIntegrationService;

    @Autowired
    private SjukfallEngineService sjukfallEngine;

    @Autowired
    private StatisticsCalculator statisticsCalculator;

    @Autowired
    private MonitoringLogService monitoringLogService;

    @Autowired
    private DiagnosFactory diagnosFactory;

    @Autowired
    private SjukfallEmployeeNameResolver sjukfallEmployeeNameResolver;

    @Autowired
    private SjukfallPuService sjukfallPuService;

    @Override
    public List<SjukfallEnhetRS> getSjukfall(String enhetsId, String mottagningsId,
                                             String lakareId, Urval urval, GetSjukfallRequest request) {

        return getByUnit(enhetsId, mottagningsId, lakareId, urval, request);
    }

    @Override
    public List<SjukfallEnhetRS> getByUnit(String enhetsId, String mottagningsId,
                                           String lakareId, Urval urval, GetSjukfallRequest request) {

        List<SjukfallEnhetRS> internalSjukfallList = getFilteredSjukfallByUnit(enhetsId, mottagningsId, lakareId, urval, request);

        // Utför sekretess-filtrering innan loggning, vi filtrerar ju ev. bort en del poster.
        sjukfallPuService.enrichWithPatientNamesAndFilterSekretess(internalSjukfallList);

        if (internalSjukfallList != null) {
            monitoringLogService.logUserViewedSjukfall(lakareId,
                internalSjukfallList.size(), resolveIdOfActualUnit(enhetsId, mottagningsId));
        }

        sjukfallEmployeeNameResolver.enrichWithHsaEmployeeNames(internalSjukfallList);
        sjukfallEmployeeNameResolver.updateDuplicateDoctorNamesWithHsaId(internalSjukfallList);


        return internalSjukfallList;
    }

    @Override
    public List<SjukfallPatientRS> getByPatient(String enhetsId, String mottagningsId,
                                                String lakareId, Urval urval, GetSjukfallRequest request) {

        List<SjukfallPatientRS> internalSjukfallList = getFilteredSjukfallByPatient(enhetsId, mottagningsId, urval, request);
        sjukfallPuService.enrichWithPatientNameAndFilterSekretess(internalSjukfallList);
        if (internalSjukfallList != null) {
            monitoringLogService.logUserViewedSjukfall(lakareId,
                internalSjukfallList.size(), resolveIdOfActualUnit(enhetsId, mottagningsId));
        }

        return internalSjukfallList;
    }

    @Override
    public SjukfallSummary getSummary(String enhetsId, String mottagningsId,
                                      String lakareId, Urval urval, GetSjukfallRequest request) {

        List<SjukfallEnhetRS> sjukfallList = getFilteredSjukfallByUnit(enhetsId, mottagningsId, lakareId, urval, request);
        sjukfallPuService.enrichWithPatientNamesAndFilterSekretess(sjukfallList);
        return statisticsCalculator.getSjukfallSummary(sjukfallList);
    }


    // - - - package scope - - -

    /**
     * Mapping from SjukfallEngine's format to Rehabstod internal format.
     */
    SjukfallEnhetRS map(SjukfallEnhet from) {
        SjukfallEnhetRS to = new SjukfallEnhetRS();

        try {
            to.setVardGivareId(from.getVardgivare().getId());
            to.setVardGivareNamn(from.getVardgivare().getNamn());
            to.setVardEnhetId(from.getVardenhet().getId());
            to.setVardEnhetNamn(from.getVardenhet().getNamn());
            to.setLakare(map(from.getLakare()));
            to.setPatient(map(from.getPatient()));
            to.setDiagnos(map(from.getDiagnosKod()));
            to.setBiDiagnoser(mapDiagnosList(from.getBiDiagnoser()));
            to.setStart(from.getStart());
            to.setSlut(from.getSlut());
            to.setDagar(from.getDagar());
            to.setIntyg(from.getIntyg());
            to.setAktivGrad(from.getAktivGrad());
            to.setGrader(from.getGrader());

        } catch (Exception e) {
            throw new SjukfallServiceException("Error mapping SjukfallEngine format to internal format", e);
        }

        return to;
    }

    /**
     * Mapping from SjukfallEngine's format to Rehabstod internal format.
     */
    SjukfallPatientRS map(SjukfallPatient from) {
        SjukfallPatientRS to = new SjukfallPatientRS();

        try {
            to.setDiagnos(map(from.getDiagnosKod()));
            to.setStart(from.getStart());
            to.setSlut(from.getSlut());

            List<PatientData> patientData = mapIntygList(from.getSjukfallIntygList());

            // Sort patientData by signeringsTidpunkt with descending order
            Comparator<PatientData> dateComparator
                = Comparator.comparing(PatientData::getSigneringsTidpunkt, Comparator.reverseOrder());

            patientData = patientData.stream().sorted(dateComparator).collect(Collectors.toList());
            to.setIntyg(patientData);

        } catch (Exception e) {
            throw new SjukfallServiceException("Error mapping SjukfallEngine format to internal format", e);
        }

        return to;
    }

    /**
     * Mapping from SjukfallEngine's format to Rehabstod internal format.
     */
    PatientData map(SjukfallIntyg from) {
        PatientData to = new PatientData();

        try {
            to.setPatient(new Patient(from.getPatientId(), from.getPatientNamn()));
            to.setDiagnos(map(from.getDiagnosKod()));
            to.setBidiagnoser(mapDiagnosList(from.getBiDiagnoser()));
            to.setStart(from.getStartDatum());
            to.setSlut(from.getSlutDatum());
            to.setSigneringsTidpunkt(from.getSigneringsTidpunkt());
            to.setDagar(from.getDagar());
            to.setGrader(from.getGrader());
            to.setLakare(from.getLakareNamn());
            to.setSysselsattning(getSysselsattningAsCsv(from.getSysselsattning()));
            to.setAktivtIntyg(from.isAktivtIntyg());
            to.setEnhetId(from.getVardenhetId());
        } catch (Exception e) {
            throw new SjukfallServiceException("Error mapping SjukfallEngine format to internal format", e);
        }

        return to;
    }

    private String getSysselsattningAsCsv(List<String> sysselsattning) {
        return sysselsattning.stream().collect(Collectors.joining(","));
    }

    /**
     * Mapping from Intygstjänsten's format to SjukfallEngine format.
     */
    IntygData map(IntygsData from) {
        IntygData to = new IntygData();

        try {
            to.setIntygId(from.getIntygsId());
            to.setPatientId(from.getPatient().getPersonId().getExtension());
            to.setPatientNamn(from.getPatient().getFullstandigtNamn());
            to.setLakareId(from.getSkapadAv().getPersonalId().getExtension());
            to.setLakareNamn(from.getSkapadAv().getFullstandigtNamn());
            to.setVardenhetId(from.getSkapadAv().getEnhet().getEnhetsId().getExtension());
            to.setVardenhetNamn(from.getSkapadAv().getEnhet().getEnhetsnamn());
            to.setVardgivareId(from.getSkapadAv().getEnhet().getVardgivare().getVardgivarId().getExtension());
            to.setVardenhetNamn(from.getSkapadAv().getEnhet().getVardgivare().getVardgivarnamn());
            to.setDiagnosKod(from.getDiagnoskod());

            List<Formaga> formagor = from.getArbetsformaga().getFormaga().stream()
                    .map(f -> map(f)).collect(Collectors.toList());

            to.setFormagor(formagor);
            to.setSigneringsTidpunkt(from.getSigneringsTidpunkt());
            to.setEnkeltIntyg(from.isEnkeltIntyg());

            to.setBiDiagnoser(getBidiagnoser(from.getBidiagnoser()));
            to.setSysselsattning(from.getSysselsattning().stream()
                    .map(s -> s.getTypAvSysselsattning().getCode())
                    .collect(Collectors.toList()));

        } catch (Exception e) {
            throw new SjukfallServiceException("Error mapping Intygstjänsten's format to SjukfallEngine format", e);
        }

        return to;
    }

    private List<DiagnosKod> getBidiagnoser(List<String> bidiagnoser) {
        return Optional.ofNullable(bidiagnoser).orElse(Collections.emptyList()).stream()
            .map(DiagnosKod::new)
            .collect(Collectors.toList());
    }

    private Lakare map(se.inera.intyg.infra.sjukfall.dto.Lakare from) {
        return new Lakare(from.getId(), from.getNamn());
    }

    private Patient map(se.inera.intyg.infra.sjukfall.dto.Patient from) {
        return new Patient(from.getId(), from.getNamn());
    }

    Diagnos map(se.inera.intyg.infra.sjukfall.dto.DiagnosKod from) {
        return diagnosFactory.getDiagnos(from.getOriginalCode(), from.getCleanedCode(), from.getName());
    }

    List<Diagnos> mapDiagnosList(List<se.inera.intyg.infra.sjukfall.dto.DiagnosKod> from) {
        if (from == null) {
            return new ArrayList<>();
        }

        return from.stream()
            .map(diagnosKod -> map(diagnosKod))
            .collect(Collectors.toList());
    }

    private List<PatientData> mapIntygList(List<SjukfallIntyg> from) {
        return from.stream().map(o -> map(o)).collect(Collectors.toList());
    }

    private IntygParametrar map(GetSjukfallRequest from) {
        return new IntygParametrar(from.getMaxIntygsGlapp(), from.getAktivtDatum());
    }

    private Formaga map(se.riv.clinicalprocess.healthcond.rehabilitation.v1.Formaga from) {
        return new Formaga(from.getStartdatum(), from.getSlutdatum(), from.getNedsattning());
    }


    // - - - private scope - - -

    /**
     * Since we always use the vardenhetsId to query intygstjänsten (even when the
     * selected unit is a mottagning), we must take care to use the mottagningsId
     * when logging when present). Note that this does NOT apply to PDL logging, only monitoring logging.
     *
     * For PDL logging we always specify the enhetsId, never a mottagningsId.
     */
    private String resolveIdOfActualUnit(String enhetsId, String mottagningsId) {
        return mottagningsId != null ? mottagningsId : enhetsId;
    }

    private List<SjukfallEnhetRS> getFilteredSjukfallByUnit(String enhetsId, String mottagningsId,
                                                            String lakareId, Urval urval, GetSjukfallRequest request) {

        if (urval == null) {
            throw new IllegalArgumentException("Urval must be given to be able to get sjukfall");
        }

        LOG.debug("Calling Intygstjänsten - fetching certificate information.");
        List<IntygsData> intygsData = intygstjanstIntegrationService.getIntygsDataForCareUnit(enhetsId);

        LOG.debug("Calling the calculation engine - calculating and assembling 'sjukfall' by unit.");
        List<IntygData> data = intygsData.stream().map(o -> map(o)).collect(Collectors.toList());
        IntygParametrar parametrar = map(request);

        List<SjukfallEnhet> sjukfallList = sjukfallEngine.beraknaSjukfallForEnhet(data, parametrar);

        LOG.debug("Mapping response from calculation engine to internal objects.");
        List<SjukfallEnhetRS> internalSjukfallList = sjukfallList.stream().map(o -> map(o)).collect(Collectors.toList());

        if (urval.equals(Urval.ISSUED_BY_ME)) {
            LOG.debug("Filtering response - a doctor shall only see patients 'sjukfall' he/she has issued certificates.");
            internalSjukfallList = internalSjukfallList.stream()
                    .filter(o -> o.getLakare().getHsaId().equals(lakareId))
                    .collect(Collectors.toList());
        }

        if (mottagningsId != null) {
            LOG.debug("Filtering response - query for mottagning, only including 'sjukfall' with active intyg on specified mottagning");
            internalSjukfallList = internalSjukfallList.stream()
                    .filter(o -> o.getVardEnhetId().equals(mottagningsId))
                    .collect(Collectors.toList());
        }

        return internalSjukfallList;
    }

    private List<SjukfallPatientRS> getFilteredSjukfallByPatient(String enhetsId, String mottagningsId,
                                                                 Urval urval, GetSjukfallRequest request) {
        if (urval == null) {
            throw new IllegalArgumentException("Urval must be given to be able to get sjukfall");
        }

        LOG.debug("Calling Intygstjänsten - fetching certificate information by patient.");
        List<IntygsData> intygsData = intygstjanstIntegrationService.getIntygsDataForPatient(enhetsId, request.getPatientId());

        LOG.debug("Calling the calculation engine - calculating and assembling 'sjukfall' by patient.");
        List<IntygData> data = intygsData.stream().map(o -> map(o)).collect(Collectors.toList());
        IntygParametrar parametrar = map(request);

        if (mottagningsId != null) {
            LOG.debug("Filtering response - query for mottagning, only including 'sjukfall' with intyg on specified mottagning");
            data = data.stream()
                .filter(o -> !o.getVardenhetId().equals(mottagningsId))
                .collect(Collectors.toList());
        }

        List<SjukfallPatient> sjukfallList = sjukfallEngine.beraknaSjukfallForPatient(data, parametrar);

        LOG.debug("Mapping response from calculation engine to internal objects.");
        List<SjukfallPatientRS> internalSjukfallList = sjukfallList.stream().map(o -> map(o)).collect(Collectors.toList());

        return internalSjukfallList;
    }

}
