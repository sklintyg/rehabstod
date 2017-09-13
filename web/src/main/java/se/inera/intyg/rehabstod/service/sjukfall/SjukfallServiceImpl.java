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
import se.inera.intyg.infra.sjukfall.services.SjukfallEngineService;
import se.inera.intyg.rehabstod.integration.it.service.IntygstjanstIntegrationService;
import se.inera.intyg.rehabstod.service.Urval;
import se.inera.intyg.rehabstod.service.monitoring.MonitoringLogService;
import se.inera.intyg.rehabstod.service.sjukfall.nameresolver.SjukfallEmployeeNameResolver;
import se.inera.intyg.rehabstod.service.sjukfall.pu.SjukfallPuService;
import se.inera.intyg.rehabstod.service.sjukfall.statistics.StatisticsCalculator;
import se.inera.intyg.rehabstod.web.controller.api.dto.GetSjukfallRequest;
import se.inera.intyg.rehabstod.service.sjukfall.mappers.IntygstjanstMapper;
import se.inera.intyg.rehabstod.service.sjukfall.mappers.SjukfallEngineMapper;
import se.inera.intyg.rehabstod.web.model.SjukfallEnhet;
import se.inera.intyg.rehabstod.web.model.SjukfallPatient;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.IntygsData;

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
    private SjukfallEngineMapper sjukfallEngineMapper;

    @Autowired
    private IntygstjanstMapper intygstjanstMapper;

    @Autowired
    private SjukfallEmployeeNameResolver sjukfallEmployeeNameResolver;

    @Autowired
    private SjukfallPuService sjukfallPuService;

    @Autowired
    private StatisticsCalculator statisticsCalculator;

    @Autowired
    private MonitoringLogService monitoringLogService;


    // api

    @Override
    public List<SjukfallEnhet> getSjukfall(String enhetsId, String mottagningsId,
                                           String lakareId, Urval urval, GetSjukfallRequest request) {

        return getByUnit(enhetsId, mottagningsId, lakareId, urval, request);
    }

    @Override
    public List<SjukfallEnhet> getByUnit(String enhetsId, String mottagningsId,
                                         String lakareId, Urval urval, GetSjukfallRequest request) {

        List<SjukfallEnhet> rehabstodSjukfall = getFilteredSjukfallByUnit(enhetsId, mottagningsId, lakareId, urval, request);

        // Utför sekretess-filtrering innan loggning, vi filtrerar ju ev. bort en del poster.
        sjukfallPuService.enrichWithPatientNamesAndFilterSekretess(rehabstodSjukfall);

        if (rehabstodSjukfall != null) {
            monitoringLogService.logUserViewedSjukfall(lakareId,
                rehabstodSjukfall.size(), resolveIdOfActualUnit(enhetsId, mottagningsId));
        }

        sjukfallEmployeeNameResolver.enrichWithHsaEmployeeNames(rehabstodSjukfall);
        sjukfallEmployeeNameResolver.updateDuplicateDoctorNamesWithHsaId(rehabstodSjukfall);

        return rehabstodSjukfall;
    }

    @Override
    public List<SjukfallPatient> getByPatient(String enhetsId, String lakareId, Urval urval, GetSjukfallRequest request) {

        List<SjukfallPatient> rehabstodSjukfall = getFilteredSjukfallByPatient(enhetsId, urval, request);
        sjukfallPuService.enrichWithPatientNameAndFilterSekretess(rehabstodSjukfall);
        if (rehabstodSjukfall != null) {
            monitoringLogService.logUserViewedSjukfall(lakareId, rehabstodSjukfall.size(), enhetsId);
        }

        return rehabstodSjukfall;
    }

    @Override
    public se.inera.intyg.rehabstod.service.sjukfall.dto.SjukfallSummary getSummary(String enhetsId, String mottagningsId,
                                      String lakareId, Urval urval, GetSjukfallRequest request) {

        List<SjukfallEnhet> sjukfallList = getFilteredSjukfallByUnit(enhetsId, mottagningsId, lakareId, urval, request);
        sjukfallPuService.enrichWithPatientNamesAndFilterSekretess(sjukfallList);
        return statisticsCalculator.getSjukfallSummary(sjukfallList);
    }


    // package scope

    private se.inera.intyg.infra.sjukfall.dto.IntygParametrar map(GetSjukfallRequest from) {
        return new se.inera.intyg.infra.sjukfall.dto.IntygParametrar(from.getMaxIntygsGlapp(), from.getAktivtDatum());
    }


    // private scope

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

    private List<SjukfallEnhet> getFilteredSjukfallByUnit(String enhetsId, String mottagningsId,
                                                          String lakareId, Urval urval, GetSjukfallRequest request) {

        if (urval == null) {
            throw new IllegalArgumentException("Urval must be given to be able to get sjukfall");
        }

        LOG.debug("Calling Intygstjänsten - fetching certificate information.");
        List<IntygsData> intygsData = intygstjanstIntegrationService.getIntygsDataForCareUnit(enhetsId);

        LOG.debug("Calling the calculation engine - calculating and assembling 'sjukfall' by unit.");
        List<se.inera.intyg.infra.sjukfall.dto.IntygData> data = intygsData.stream()
            .map(o -> intygstjanstMapper.map(o)).collect(Collectors.toList());
        se.inera.intyg.infra.sjukfall.dto.IntygParametrar parametrar = map(request);

        List<se.inera.intyg.infra.sjukfall.dto.SjukfallEnhet> sjukfallList = sjukfallEngine.beraknaSjukfallForEnhet(data, parametrar);

        LOG.debug("Mapping response from calculation engine to internal objects.");
        List<SjukfallEnhet> rehabstodSjukfall =
            sjukfallList.stream().map(o -> sjukfallEngineMapper.map(o)).collect(Collectors.toList());

        if (urval.equals(Urval.ISSUED_BY_ME)) {
            LOG.debug("Filtering response - a doctor shall only see patients 'sjukfall' he/she has issued certificates.");
            rehabstodSjukfall = rehabstodSjukfall.stream()
                    .filter(o -> o.getLakare().getHsaId().equals(lakareId))
                    .collect(Collectors.toList());
        }

        if (mottagningsId != null) {
            LOG.debug("Filtering response - query for mottagning, only including 'sjukfall' with active intyg on specified mottagning");
            rehabstodSjukfall = rehabstodSjukfall.stream()
                    .filter(o -> o.getVardEnhetId().equals(mottagningsId))
                    .collect(Collectors.toList());
        }

        return rehabstodSjukfall;
    }

    private List<SjukfallPatient> getFilteredSjukfallByPatient(String enhetsId,
                                                               Urval urval, GetSjukfallRequest request) {
        if (urval == null) {
            throw new IllegalArgumentException("Urval must be given to be able to get sjukfall");
        }

        LOG.debug("Calling Intygstjänsten - fetching certificate information by patient.");
        List<IntygsData> intygsData = intygstjanstIntegrationService.getIntygsDataForPatient(enhetsId, request.getPatientId());

        LOG.debug("Calling the calculation engine - calculating and assembling 'sjukfall' by patient.");
        List<se.inera.intyg.infra.sjukfall.dto.IntygData> data = intygsData.stream()
            .map(o -> intygstjanstMapper.map(o)).collect(Collectors.toList());
        se.inera.intyg.infra.sjukfall.dto.IntygParametrar parametrar = map(request);

        List<se.inera.intyg.infra.sjukfall.dto.SjukfallPatient> sjukfallList = sjukfallEngine.beraknaSjukfallForPatient(data, parametrar);

        LOG.debug("Mapping response from calculation engine to internal objects.");
        List<SjukfallPatient> rehabstodSjukfall
            = sjukfallList.stream().map(o -> sjukfallEngineMapper.map(o)).collect(Collectors.toList());

        return rehabstodSjukfall;
    }

}
