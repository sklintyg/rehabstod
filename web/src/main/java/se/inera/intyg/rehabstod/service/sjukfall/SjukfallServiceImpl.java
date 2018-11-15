/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import se.inera.intyg.infra.monitoring.annotation.PrometheusTimeMethod;
import se.inera.intyg.infra.sjukfall.dto.IntygData;
import se.inera.intyg.infra.sjukfall.dto.IntygParametrar;
import se.inera.intyg.infra.sjukfall.services.SjukfallEngineService;
import se.inera.intyg.rehabstod.common.model.IntygAccessControlMetaData;
import se.inera.intyg.rehabstod.integration.it.service.IntygstjanstIntegrationService;
import se.inera.intyg.rehabstod.integration.samtyckestjanst.service.SamtyckestjanstIntegrationService;
import se.inera.intyg.rehabstod.integration.sparrtjanst.service.SparrtjanstIntegrationService;
import se.inera.intyg.rehabstod.service.Urval;
import se.inera.intyg.rehabstod.service.exceptions.SRSServiceException;
import se.inera.intyg.rehabstod.service.monitoring.MonitoringLogService;
import se.inera.intyg.rehabstod.service.sjukfall.dto.FilteredSjukFallByPatientResult;
import se.inera.intyg.rehabstod.service.sjukfall.dto.SjfMetaData;
import se.inera.intyg.rehabstod.service.sjukfall.dto.SjfSamtyckeFinnsMetaData;
import se.inera.intyg.rehabstod.service.sjukfall.dto.SjukfallEnhetResponse;
import se.inera.intyg.rehabstod.service.sjukfall.dto.SjukfallPatientResponse;
import se.inera.intyg.rehabstod.service.sjukfall.dto.SjukfallSummary;
import se.inera.intyg.rehabstod.service.sjukfall.mappers.IntygstjanstMapper;
import se.inera.intyg.rehabstod.service.sjukfall.mappers.SjukfallEngineMapper;
import se.inera.intyg.rehabstod.service.sjukfall.nameresolver.SjukfallEmployeeNameResolver;
import se.inera.intyg.rehabstod.service.sjukfall.pu.SjukfallPuService;
import se.inera.intyg.rehabstod.service.sjukfall.srs.RiskPredictionService;
import se.inera.intyg.rehabstod.service.sjukfall.statistics.StatisticsCalculator;
import se.inera.intyg.rehabstod.web.model.SjukfallEnhet;
import se.inera.intyg.rehabstod.web.model.SjukfallPatient;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.IntygsData;

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

    @Autowired
    private RiskPredictionService riskPredictionService;

    @Autowired
    private SparrtjanstIntegrationService sparrtjanstIntegrationService;

    @Autowired
    private SamtyckestjanstIntegrationService samtyckestjanstIntegrationService;

    @Override
    @PrometheusTimeMethod
    public SjukfallEnhetResponse getByUnit(String enhetsId, String mottagningsId, String lakareId, Urval urval,
            IntygParametrar parameters) {

        List<SjukfallEnhet> rehabstodSjukfall = getFilteredSjukfallByUnit(enhetsId, mottagningsId, lakareId, urval, parameters);

        // Utför sekretess-filtrering innan loggning, vi filtrerar ju ev. bort en del poster.
        sjukfallPuService.enrichWithPatientNamesAndFilterSekretess(rehabstodSjukfall);

        if (rehabstodSjukfall != null) {
            monitoringLogService.logUserViewedSjukfall(lakareId,
                    rehabstodSjukfall.size(), resolveIdOfActualUnit(enhetsId, mottagningsId));
        }

        sjukfallEmployeeNameResolver.enrichWithHsaEmployeeNames(rehabstodSjukfall);
        sjukfallEmployeeNameResolver.updateDuplicateDoctorNamesWithHsaId(rehabstodSjukfall);
        boolean srsError = false;
        try {
            riskPredictionService.updateWithRiskPredictions(rehabstodSjukfall);
        } catch (SRSServiceException e) {
            srsError = true;
        }
        return new SjukfallEnhetResponse(rehabstodSjukfall, srsError);
    }

    @Override
    @PrometheusTimeMethod
    public SjukfallPatientResponse getByPatient(String currentVardgivarHsaId, String enhetsId, String lakareId,
                                                String patientId, Urval urval, IntygParametrar parameters,
                                                Collection<String> vgHsaIds) {

        FilteredSjukFallByPatientResult result =
                getFilteredSjukfallByPatient(currentVardgivarHsaId, enhetsId, lakareId, patientId, urval, parameters, vgHsaIds);

        final List<SjukfallPatient> rehabstodSjukfall = result.getRehabstodSjukfall();

        sjukfallPuService.enrichWithPatientNameAndFilterSekretess(rehabstodSjukfall);
        boolean srsError = false;
        try {
            riskPredictionService.updateSjukfallPatientListWithRiskPredictions(rehabstodSjukfall);
        } catch (SRSServiceException e) {
            srsError = true;
        }

        if (rehabstodSjukfall != null) {
            monitoringLogService.logUserViewedSjukfall(lakareId, rehabstodSjukfall.size(), enhetsId);
        }

        return new SjukfallPatientResponse(rehabstodSjukfall, result.getSjfMetaData(), srsError);
    }

    @Override
    @PrometheusTimeMethod
    public SjukfallSummary getSummary(String enhetsId, String mottagningsId,
                                      String lakareId, Urval urval, IntygParametrar parameters) {

        List<SjukfallEnhet> sjukfallList = getFilteredSjukfallByUnit(enhetsId, mottagningsId, lakareId, urval, parameters);
        sjukfallPuService.filterSekretessForSummary(sjukfallList);
        return statisticsCalculator.getSjukfallSummary(sjukfallList);
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

    private List<SjukfallEnhet> getFilteredSjukfallByUnit(String enhetsId, String mottagningsId, String lakareId, Urval urval,
            IntygParametrar parameters) {

        if (urval == null) {
            throw new IllegalArgumentException("Urval must be given to be able to get sjukfall");
        }

        LOG.debug("Calling Intygstjänsten - fetching certificate information.");
        List<IntygsData> intygsData = intygstjanstIntegrationService.getIntygsDataForCareUnit(enhetsId,
                parameters.getMaxAntalDagarSedanSjukfallAvslut());

        LOG.debug("Calling the calculation engine - calculating and assembling 'sjukfall' by unit.");
        List<se.inera.intyg.infra.sjukfall.dto.IntygData> data = intygsData.stream()
                .map(o -> intygstjanstMapper.map(o)).collect(Collectors.toList());

        List<se.inera.intyg.infra.sjukfall.dto.SjukfallEnhet> sjukfallList = sjukfallEngine.beraknaSjukfallForEnhet(data, parameters);

        LOG.debug("Mapping response from calculation engine to internal objects.");
        List<SjukfallEnhet> rehabstodSjukfall = sjukfallList.stream()
                .map(o -> sjukfallEngineMapper.map(o, parameters.getMaxAntalDagarSedanSjukfallAvslut(), parameters.getAktivtDatum()))
                .collect(Collectors.toList());

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

    private FilteredSjukFallByPatientResult getFilteredSjukfallByPatient(String currentVardgivarHsaId, String enhetsId, String lakareId,
                                                                         String patientId, Urval urval, IntygParametrar parameters,
                                                                         Collection<String> vgHsaIds) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(currentVardgivarHsaId), "currentVardgivarHsaId may not be null or empty");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(enhetsId), "enhetsId may not be null or empty");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(lakareId), "lakareId may not be null or empty");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(patientId), "patientId may not be null or empty");
        Preconditions.checkArgument(urval != null, "urval may not be null");

        List<se.inera.intyg.infra.sjukfall.dto.SjukfallPatient> sjukfallList;
        Map<String, IntygAccessControlMetaData> intygAccessMetaData = new HashMap<>();

        LOG.debug("Calling Intygstjänsten - fetching certificate information by patient.");
        List<IntygsData> intygsData =
                intygstjanstIntegrationService.getAllIntygsDataForPatient(patientId);

        List<IntygData> data = intygsData.stream()
                .map(o -> intygstjanstMapper.map(o))
                .collect(Collectors.toList());

        // Create initial map linked to each intyg by intygsId
        data.forEach(intygData -> intygAccessMetaData.put(intygData.getIntygId(),
                new IntygAccessControlMetaData(intygData, currentVardgivarHsaId.equals(intygData.getVardgivareId()),
                        vgHsaIds.contains(intygData.getVardgivareId()))));

        // Decorate intygAccessMetaData with "spärr" info
        sparrtjanstIntegrationService.decorateWithBlockStatus(currentVardgivarHsaId, enhetsId, lakareId, patientId, intygAccessMetaData,
                data);

        // Make an initial calculation using _all_ available intyg...
        sjukfallList = sjukfallEngine.beraknaSjukfallForPatient(data, parameters);
        // ... and check which intyg is contributing to the aktive sjukfall
        updateAccessMetaDataWithContributingStatus(sjukfallList, intygAccessMetaData, parameters);

        // Make a call to the check for consent service
        // Decorate intygAccessMetaData with the consent info
        boolean haveConsent = samtyckestjanstIntegrationService.checkForConsent(patientId, lakareId, currentVardgivarHsaId, enhetsId);

        // skapa listorna med vilka vårdgivare som hade intyg SKULLE funnits i aktivt sjukfall men som inte kommer med
        // pga spärr inre/yttrespärr.
        SjfMetaData sjfMetaData = createSjfMetaData(intygAccessMetaData, haveConsent);

        // Final run, where all intyg not cleared (sparr/samtycke) to be included have been removed
        data = filterByAcessMetaData(data, intygAccessMetaData, haveConsent);
        sjukfallList = sjukfallEngine.beraknaSjukfallForPatient(data, parameters);

        LOG.debug("Mapping response from calculation engine to internal objects.");
        List<SjukfallPatient> rehabstodSjukfall = sjukfallList.stream()
                .map(o -> sjukfallEngineMapper.map(o, currentVardgivarHsaId, enhetsId))
                .collect(Collectors.toList());

        return new FilteredSjukFallByPatientResult(rehabstodSjukfall, sjfMetaData);
    }

    private SjfMetaData createSjfMetaData(Map<String, IntygAccessControlMetaData> intygAccessMetaData, boolean haveConsent) {
        SjfMetaData metadata = new SjfMetaData();

        Map<String, SjfSamtyckeFinnsMetaData> vardgivareSamtycke = new HashMap<>();

        intygAccessMetaData.forEach((intygsId, iacm) -> {
            if (iacm.isBidrarTillAktivtSjukfall()) {
                String vardgivareId = iacm.getIntygData().getVardgivareId();
                String vardgivareNamn = iacm.getIntygData().getVardgivareNamn();

                if (iacm.inreSparr()) {
                    metadata.getVardenheterInomVGMedSparr().add(iacm.getIntygData().getVardenhetNamn());
                }
                if (iacm.yttreSparr()) {
                    metadata.getAndraVardgivareMedSparr().add(vardgivareNamn);
                } else if (iacm.isKraverSamtycke()) {
                    if (!vardgivareSamtycke.containsKey(vardgivareId)) {
                        SjfSamtyckeFinnsMetaData sjfSamtyckeFinnsMetaData = new SjfSamtyckeFinnsMetaData();
                        sjfSamtyckeFinnsMetaData.setVardgivareId(vardgivareId);
                        sjfSamtyckeFinnsMetaData.setVardgivareNamn(vardgivareNamn);
                        sjfSamtyckeFinnsMetaData.setIncludedInSjukfall(iacm.isIncludeBasedOnSamtycke());

                        vardgivareSamtycke.put(vardgivareId, sjfSamtyckeFinnsMetaData);
                    }
                }
            }
        });

        metadata.setKraverSamtycke(vardgivareSamtycke.values());
        metadata.setSamtyckeFinns(haveConsent);

        return metadata;
    }

    private void updateAccessMetaDataWithContributingStatus(List<se.inera.intyg.infra.sjukfall.dto.SjukfallPatient> sjukfallList,
            Map<String, IntygAccessControlMetaData> intygAccessMetaData, IntygParametrar parameters) {
        if (sjukfallList.isEmpty()) {
            return;
        }

        // Update BidrarTillAktivtSjukfall for all that are part of active sjukfall.
        final se.inera.intyg.infra.sjukfall.dto.SjukfallPatient aktivtSjukfall = sjukfallList.get(0);

        if (aktivtSjukfall.getSlut().isAfter(parameters.getAktivtDatum())) {
            aktivtSjukfall.getSjukfallIntygList()
                    .forEach(sjukfallIntyg -> intygAccessMetaData.get(sjukfallIntyg.getIntygId()).setBidrarTillAktivtSjukfall(true));
        }

    }

    private List<IntygData> filterByAcessMetaData(List<IntygData> data, Map<String, IntygAccessControlMetaData> intygAccessMetaData,
                                                  boolean haveConsent) {
        return data.stream()
                .filter(intygData -> shouldInclude(intygAccessMetaData.get(intygData.getIntygId()), haveConsent))
                .collect(Collectors.toList());
    }

    private boolean shouldInclude(IntygAccessControlMetaData intygAccessControlMetaData, boolean haveConsent) {
        // 1. Får inte ha spärr
        if (intygAccessControlMetaData.isSparr()) {
            return false;
        }

        // 2. Tar bort intyg på andra vårdgivare som inte bidrar till det aktiva sjukfallet
        if (!intygAccessControlMetaData.isInomVardgivare() && !intygAccessControlMetaData.isBidrarTillAktivtSjukfall()) {
            return false;
        }

        // 3. Om Samtycke krävs - måste också samtycke vara givet
        if (intygAccessControlMetaData.isKraverSamtycke() && !haveConsent) {
            return false;
        }

        // 4. Om samtycke finns måste aktivt val för att ta med i sjufakll gjorts
        if (intygAccessControlMetaData.isKraverSamtycke() && haveConsent && !intygAccessControlMetaData.isIncludeBasedOnSamtycke()) {
            return false;
        }

        return true;
    }

}
