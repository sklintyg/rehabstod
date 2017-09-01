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
import se.inera.intyg.infra.sjukfall.dto.Formaga;
import se.inera.intyg.infra.sjukfall.dto.IntygData;
import se.inera.intyg.infra.sjukfall.dto.IntygParametrar;
import se.inera.intyg.infra.sjukfall.dto.SjukfallEnhet;
import se.inera.intyg.infra.sjukfall.dto.SjukfallPatient;
import se.inera.intyg.infra.sjukfall.services.SjukfallEngineService;
import se.inera.intyg.rehabstod.integration.it.service.IntygstjanstIntegrationService;
import se.inera.intyg.rehabstod.service.Urval;
import se.inera.intyg.rehabstod.service.diagnos.DiagnosFactory;
import se.inera.intyg.rehabstod.service.monitoring.MonitoringLogService;
import se.inera.intyg.rehabstod.service.sjukfall.dto.SjukfallSummary;
import se.inera.intyg.rehabstod.service.sjukfall.nameresolver.SjukfallEmployeeNameResolver;
import se.inera.intyg.rehabstod.service.sjukfall.statistics.StatisticsCalculator;
import se.inera.intyg.rehabstod.web.controller.api.dto.GetSjukfallRequest;
import se.inera.intyg.rehabstod.web.model.Diagnos;
import se.inera.intyg.rehabstod.web.model.InternalSjukfall;
import se.inera.intyg.rehabstod.web.model.Lakare;
import se.inera.intyg.rehabstod.web.model.Patient;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.IntygsData;

import java.util.ArrayList;
import java.util.List;
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

    @Override
    public List<InternalSjukfall> getSjukfall(String enhetsId, String mottagningsId, String hsaId, Urval urval, GetSjukfallRequest request) {
        return getByUnit(enhetsId, mottagningsId, hsaId, urval, request);
    }

    @Override
    public List<InternalSjukfall> getByUnit(String enhetsId, String mottagningsId, String hsaId, Urval urval, GetSjukfallRequest request) {
        List<InternalSjukfall> internalSjukfallList = getFilteredSjukfallByEnhet(enhetsId, mottagningsId, hsaId, urval, request);
        if (internalSjukfallList != null) {
            monitoringLogService.logUserViewedSjukfall(hsaId, internalSjukfallList.size(), resolveIdOfActualUnit(enhetsId, mottagningsId));
        }

        sjukfallEmployeeNameResolver.enrichWithHsaEmployeeNames(internalSjukfallList);
        sjukfallEmployeeNameResolver.updateDuplicateDoctorNamesWithHsaId(internalSjukfallList);

        return internalSjukfallList;
    }

    @Override
    public List<InternalSjukfall> getByPatient(String patientId, String enhetsId, String mottagningsId, String hsaId, Urval urval, GetSjukfallRequest request) {
        List<InternalSjukfall> internalSjukfallList = getFilteredSjukfallByPatient(patientId, enhetsId, mottagningsId, hsaId, urval, request);
        if (internalSjukfallList != null) {
            monitoringLogService.logUserViewedSjukfall(hsaId, internalSjukfallList.size(), resolveIdOfActualUnit(enhetsId, mottagningsId));
        }

        return internalSjukfallList;
    }

    @Override
    public SjukfallSummary getSummary(String enhetsId, String mottagningsId, String hsaId, Urval urval, GetSjukfallRequest request) {
        return statisticsCalculator.getSjukfallSummary(getFilteredSjukfallByEnhet(enhetsId, mottagningsId, hsaId, urval, request));
    }


    // - - - package scope - - -

    /**
     * Mapping from Sjukfall Engine format to Rehabstod internal format.
     */
    InternalSjukfall map(SjukfallEnhet from) {
        InternalSjukfall  to = new InternalSjukfall();

        try {
            to.setVardGivareId(from.getVardgivare().getId());
            to.setVardGivareNamn(from.getVardgivare().getNamn());
            to.setVardEnhetId(from.getVardenhet().getId());
            to.setVardEnhetNamn(from.getVardenhet().getNamn());
            to.setLakare(map(from.getLakare()));
            to.setPatient(map(from.getPatient()));
            to.setDiagnos(map(from.getDiagnosKod()));
            to.setBiDiagnoser(map(from.getBiDiagnoser()));
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
     * Mapping to Sjukfall Engine format from Rehabstod internal format.
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

            to.setBiDiagnoser(from.getBidiagnoser());
            to.setSysselsattning(from.getSysselsattning().stream()
                    .map(s -> s.getTypAvSysselsattning().getCode())
                    .collect(Collectors.toList()));

        } catch (Exception e) {
            throw new SjukfallServiceException("Error mapping internal format to SjukfallEngine format", e);
        }

        return to;
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

    List<Diagnos> map(List<se.inera.intyg.infra.sjukfall.dto.DiagnosKod> from) {
        if (from == null) {
            return new ArrayList<>();
        }

        return from.stream()
            .map(diagnosKod -> map(diagnosKod))
            .collect(Collectors.toList());
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

    private List<InternalSjukfall> getFilteredSjukfallByEnhet(String enhetsId, String mottagningsId,
                                                              String hsaId, Urval urval, GetSjukfallRequest request) {

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
        List<InternalSjukfall> internalSjukfallList = sjukfallList.stream().map(o -> map(o)).collect(Collectors.toList());

        if (urval.equals(Urval.ISSUED_BY_ME)) {
            LOG.debug("Filtering response - a doctor shall only see patients 'sjukfall' he/she has issued certificates.");
            internalSjukfallList = internalSjukfallList.stream()
                    .filter(o -> o.getLakare().getHsaId().equals(hsaId))
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

    private List<InternalSjukfall> getFilteredSjukfallByPatient(String patientId, String enhetsId, String mottagningsId, String hsaId, Urval urval, GetSjukfallRequest request) {
        if (urval == null) {
            throw new IllegalArgumentException("Urval must be given to be able to get sjukfall");
        }

        LOG.debug("Calling Intygstjänsten - fetching certificate information by patient.");
        List<IntygsData> intygsData = intygstjanstIntegrationService.getIntygsDataForPatient(patientId, enhetsId);

        LOG.debug("Calling the calculation engine - calculating and assembling 'sjukfall' by patient.");
        List<IntygData> data = intygsData.stream().map(o -> map(o)).collect(Collectors.toList());
        IntygParametrar parametrar = map(request);

        List<SjukfallPatient> sjukfallList = sjukfallEngine.beraknaSjukfallForPatient(data, parametrar);

        LOG.debug("Mapping response from calculation engine to internal objects.");
        //List<InternalSjukfall> internalSjukfallList = sjukfallList.stream().map(o -> map(o)).collect(Collectors.toList());
        //return internalSjukfallList;
        return new ArrayList<>();
    }

}
