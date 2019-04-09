/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.integration.hsa.model.Mottagning;
import se.inera.intyg.infra.integration.hsa.model.Vardenhet;
import se.inera.intyg.infra.integration.hsa.services.HsaOrganizationsService;
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
import se.inera.intyg.rehabstod.service.sjukfall.dto.SjfMetaDataItem;
import se.inera.intyg.rehabstod.service.sjukfall.dto.SjfMetaDataItemType;
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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @Autowired
    private RiskPredictionService riskPredictionService;

    @Autowired
    private SparrtjanstIntegrationService sparrtjanstIntegrationService;

    @Autowired
    private SamtyckestjanstIntegrationService samtyckestjanstIntegrationService;

    @Autowired
    private HsaOrganizationsService hsaOrganizationsService;


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
    // CHECKSTYLE:OFF ParameterNumber
    public SjukfallPatientResponse getByPatient(String currentVardgivarId, String enhetsId, String lakareId,
                                                String patientId, Urval urval, IntygParametrar parameters,
                                                Collection<String> vgHsaIds, Collection<String> veHsaIds) {

        FilteredSjukFallByPatientResult result =
                getFilteredSjukfallByPatient(currentVardgivarId, enhetsId, lakareId, patientId,
                        urval, parameters, vgHsaIds, veHsaIds);

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
    // CHECKSTYLE:ON ParameterNumber

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

    // CHECKSTYLE:OFF ParameterNumber
    private FilteredSjukFallByPatientResult getFilteredSjukfallByPatient(String vardgivareId, String enhetsId, String lakareId,
                                                                         String patientId, Urval urval, IntygParametrar parameters,
                                                                         Collection<String> vgHsaIds, Collection<String> veHsaIds) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(vardgivareId), "vardenhetId may not be null or empty");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(enhetsId), "enhetsId may not be null or empty");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(lakareId), "lakareId may not be null or empty");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(patientId), "patientId may not be null or empty");
        Preconditions.checkArgument(urval != null, "urval may not be null");

        List<se.inera.intyg.infra.sjukfall.dto.SjukfallPatient> sjukfallList;
        Map<String, IntygAccessControlMetaData> intygAccessMetaData = new HashMap<>();

        LOG.debug("Calling HSA - fetching information about the current care unit.");
        Vardenhet currentVardenhet = hsaOrganizationsService.getVardenhet(enhetsId);

        LOG.debug("Calling Intygstjänsten - fetching certificate information about patient.");
        List<IntygsData> intygsData =
                intygstjanstIntegrationService.getAllIntygsDataForPatient(patientId);

        //Map to sjukfall DTO
        List<IntygData> data = intygsData.stream()
                .map(o -> intygstjanstMapper.map(o))
                .collect(Collectors.toList());

        // Remove intyg from other units for patients with sekretess
        LOG.debug("Calling PU - fetching information about patients 'sekretess' status.");
        List<IntygData> filteredData = sjukfallPuService.filterSekretessForPatientHistory(data, vardgivareId, enhetsId);

        boolean haveSekretess = filteredData.size() != data.size();
        data = filteredData;

        //Decorate with VG names
        decorateWithVardgivarNamn(data);

        //Create initial map linked to each intyg by intygsId
        data.forEach(intygData -> intygAccessMetaData.put(intygData.getIntygId(),
                new IntygAccessControlMetaData(
                        intygData,
                        vardgivareId.equals(intygData.getVardgivareId()),
                        isIntygIssuedOnEnhetOrMottagning(currentVardenhet, intygData.getVardenhetId()),
                        shouldBeIncludedInCalculationOfSjukfall(vgHsaIds, veHsaIds, intygData))));

        //Assert that at least one intyg for this patient is issued
        //on the current unit (see INTYG-7686)
        if (intygAccessMetaData.size() > 0
                && intygAccessMetaData.entrySet().stream().noneMatch(intyg -> intyg.getValue().isInomVardenhet())) {
            throw new SjukfallServiceException("At least one intyg must be issued on current unit!");
        }

        SjfMetaData sjfMetaData = new SjfMetaData();
        boolean haveConsent = false;

        // Hoppa över spärr och samtyckestjänsten om det är sekretess
        if (!haveSekretess) {
            List<IntygData> intygOnOtherUnits = filterByOtherUnitsOnly(vardgivareId, enhetsId, data);

            // Decorate intygAccessMetaData with blocking info
            decorateWithBlockStatus(vardgivareId, enhetsId, lakareId, patientId,
                    intygAccessMetaData, intygOnOtherUnits, sjfMetaData);

            // Make an initial calculation using _all_ available intyg...
            sjukfallList = sjukfallEngine.beraknaSjukfallForPatient(data, parameters);

            // ... and check which intyg is contributing to the active sjukfall
            updateAccessMetaDataWithContributingStatus(sjukfallList, intygAccessMetaData, parameters);

            // ...and do we have a consent to fetch data from other care units
            haveConsent = checkForConsent(vardgivareId, enhetsId, lakareId, patientId,
                    intygAccessMetaData, intygOnOtherUnits, sjfMetaData);

        }

        // Skapa listorna med vilka vårdgivare som har intyg som SKULLE funnits i aktivt sjukfall
        // men som inte kommer med pga spärr inre/yttrespärr.
        updateSjfMetaData(sjfMetaData, intygAccessMetaData, haveConsent);

        // Remove all intyg that shouldn't be included in the calculation
        data = filterByAcessMetaData(data, intygAccessMetaData, haveConsent);

        // Make final calculation
        sjukfallList = sjukfallEngine.beraknaSjukfallForPatient(data, parameters);

        LOG.debug("Mapping response from calculation engine to internal objects.");
        List<SjukfallPatient> rehabstodSjukfall = sjukfallList.stream()
                .map(o -> sjukfallEngineMapper.map(o, vardgivareId, enhetsId))
                .collect(Collectors.toList());

        return new FilteredSjukFallByPatientResult(rehabstodSjukfall, sjfMetaData);
    }


    // CHECKSTYLE:ON ParameterNumber

    private void addSjfMetaDataItemToMap(String id, String namn, SjfMetaDataItemType type,
                                         IntygAccessControlMetaData iacm, Map<String, SjfMetaDataItem> dataMap) {
        if (dataMap.containsKey(id)) {
            SjfMetaDataItem sjfMetaDataItem = dataMap.get(id);
            if (!sjfMetaDataItem.isBidrarTillAktivtSjukfall()) {
                sjfMetaDataItem.setBidrarTillAktivtSjukfall(iacm.isBidrarTillAktivtSjukfall());
            }
        } else {
            SjfMetaDataItem sjfMetaDataItem = new SjfMetaDataItem(id, namn, type);
            sjfMetaDataItem.setIncludedInSjukfall(iacm.isInkluderadVidBerakningAvSjukfall());
            sjfMetaDataItem.setBidrarTillAktivtSjukfall(iacm.isBidrarTillAktivtSjukfall());

            dataMap.put(id, sjfMetaDataItem);
        }
    }

    private boolean checkForConsent(String vardgivareId, String enhetsId, String lakareId, String patientId,
                                    Map<String, IntygAccessControlMetaData> intygAccessMetaData,
                                    List<IntygData> intygOnOtherUnits,
                                    SjfMetaData sjfMetaData) {

        boolean haveConsent = false;

        try {
            // Make a call to consent service to see if there is a consent registered
            LOG.debug("Calling Samtyckestjänsten - checking for consent.");
            haveConsent = samtyckestjanstIntegrationService.checkForConsent(patientId, lakareId, vardgivareId, enhetsId);
        } catch (Exception e) {
            LOG.error("INTEGRATION_CONSENT_SERVICE: Fatal error - message is '{}'", e.getMessage());

            // INTYG-8405: Om Samtyckestjänsten inte är tillgänglig ska efterfrågad information hanteras som spärrad.
            declineAccessToSjfData(intygAccessMetaData, intygOnOtherUnits);
            // Tell stakeholders there were a problem when callling Samtyckestjänsten
            sjfMetaData.setConsentServiceError(true);
        }

        return haveConsent;
    }

    private void declineAccessToSjfData(Map<String, IntygAccessControlMetaData> intygAccessMetaData, List<IntygData> intygOnOtherUnits) {
        intygOnOtherUnits.
                forEach(intygData -> {
                    intygAccessMetaData.get(intygData.getIntygId()).setSparr(true);
                });
    }

    private void decorateWithBlockStatus(String vardgivareId, String enhetsId, String lakareId, String patientId,
                                         Map<String, IntygAccessControlMetaData> intygAccessMetaData,
                                         List<IntygData> intygOnOtherUnits,
                                         SjfMetaData sjfMetaData) {
        try {
            LOG.debug("Calling Spärrstjänsten - checking for blocking statuses.");
            sparrtjanstIntegrationService.decorateWithBlockStatus(vardgivareId, enhetsId,
                    lakareId, patientId, intygAccessMetaData, intygOnOtherUnits);
        } catch (Exception e) {
            LOG.error("INTEGRATION_BLOCKING_SERVICE: Fatal error - message is '{}'", e.getMessage());

            // INTYG-8405: Om Spärrtjänsten inte är tillgänglig ska efterfrågad information hanteras som spärrad.
            declineAccessToSjfData(intygAccessMetaData, intygOnOtherUnits);
            // Tell stakeholders there were a problem when callling Spärrtjänsten
            sjfMetaData.setBlockingServiceError(true);
        }
    }

    private void decorateWithVardgivarNamn(List<IntygData> data) {
        data.forEach(
                intygData -> intygData.setVardgivareNamn(getVardgivarNamn(intygData.getVardgivareId())));
    }

    private String getVardgivarNamn(String vardgivareId) {
        try {
            return hsaOrganizationsService.getVardgivareInfo(vardgivareId).getNamn();
        } catch (Exception e) {
            LOG.error("Failed to get VardgivareInfo from HSA for vardgivarId '{}'", vardgivareId, e);
            //Fallback is to show hsaId instead.
            return vardgivareId;
        }
    }

    private List<IntygData> filterByAcessMetaData(List<IntygData> data,
                                                  Map<String, IntygAccessControlMetaData> intygAccessMetaData,
                                                  boolean haveConsent) {
        return data.stream()
                .filter(intygData -> shouldInclude(intygAccessMetaData.get(intygData.getIntygId()), haveConsent))
                .collect(Collectors.toList());
    }

    private List<IntygData> filterByOtherUnitsOnly(String currentVardgivarHsaId,
                                                   String currentVardenhetHsaId,
                                                   List<IntygData> intygList) {
        return intygList.stream()
                .filter(
                        intygData -> !(currentVardgivarHsaId.equals(intygData.getVardgivareId())
                                && currentVardenhetHsaId.equals(intygData.getVardenhetId()))
                )
                .collect(Collectors.toList());
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

    private void updateSjfMetaData(final SjfMetaData sjfMetaData,
                                   final Map<String, IntygAccessControlMetaData> intygAccessMetaData,
                                   boolean haveConsent) {

        Map<String, SjfMetaDataItem> kraverSamtyckeDataMap = new HashMap<>();
        Map<String, SjfMetaDataItem> kraverInteSamtyckeDataMap = new HashMap<>();

        intygAccessMetaData.forEach((intygsId, iacm) -> {
            if (iacm.inreSparr()) {
                sjfMetaData.getVardenheterInomVGMedSparr().add(iacm.getIntygData().getVardenhetNamn());
            } else {
                /*
                 * INTYG-7911:
                 * Intyg som finns på annan vårdenhet men inom samma vårdgivare kräver inte samtycke
                 * men ska inte heller inhämtas automatisk vid beräkning av det aktiva sjukfallet.
                 */
                if (!iacm.isKraverSamtycke() && !iacm.isInomVardenhet()) {
                    String id = iacm.getIntygData().getVardenhetId();
                    String namn = iacm.getIntygData().getVardenhetNamn();
                    SjfMetaDataItemType type = SjfMetaDataItemType.VARDENHET;

                    addSjfMetaDataItemToMap(id, namn, type, iacm, kraverInteSamtyckeDataMap);
                }
            }

            if (iacm.yttreSparr()) {
                sjfMetaData.getAndraVardgivareMedSparr().add(iacm.getIntygData().getVardgivareNamn());
            } else {
                /*
                 * INTYG-7912:
                 * Intyg som finns på annan vårdgivare kräver alltid samtycke.
                 */
                if (iacm.isKraverSamtycke()) {
                    String id = iacm.getIntygData().getVardgivareId();
                    String namn = iacm.getIntygData().getVardgivareNamn();
                    SjfMetaDataItemType type = SjfMetaDataItemType.VARDGIVARE;

                    addSjfMetaDataItemToMap(id, namn, type, iacm, kraverSamtyckeDataMap);
                }
            }
        });

        sjfMetaData.setKraverInteSamtycke(kraverInteSamtyckeDataMap.values());
        sjfMetaData.setKraverSamtycke(kraverSamtyckeDataMap.values());
        sjfMetaData.setSamtyckeFinns(haveConsent);
    }

    private boolean shouldBeIncludedInCalculationOfSjukfall(Collection<String> vgHsaIds, Collection<String> veHsaIds, IntygData intygData) {
        return vgHsaIds.contains(intygData.getVardgivareId()) || veHsaIds.contains(intygData.getVardenhetId());
    }

    private boolean shouldInclude(IntygAccessControlMetaData iacm, boolean haveConsent) {
        // 1. Får inte ha spärr
        if (iacm.isSparr()) {
            return false;
        }

        // 2. Om Samtycke krävs - måste också samtycke vara givet
        if (iacm.isKraverSamtycke() && !haveConsent) {
            return false;
        }

        // 3. Om samtycke finns måste aktivt val för att ta med i sjufakll gjorts
        if (iacm.isKraverSamtycke() && haveConsent && !iacm.isInkluderadVidBerakningAvSjukfall()) {
            return false;
        }

        // 4. Tar bort intyg på andra vårdgivare som inte bidrar till det aktiva sjukfallet
        if (!iacm.isInomVardgivare() && !iacm.isBidrarTillAktivtSjukfall()) {
            return false;
        }

        // 5. Tar bort intyg på samma vårdgivare som är
        //    -- utanför aktuell enhet
        //    -- och som inte lagts till som ett aktivt val
        //    -- och som inte bidrar till det aktiva sjukfallet
        if (iacm.isInomVardgivare() && !iacm.isInomVardenhet()) {
            return iacm.isInkluderadVidBerakningAvSjukfall() && iacm.isBidrarTillAktivtSjukfall();
        }

        return true;
    }

    private boolean isIntygIssuedOnEnhetOrMottagning(Vardenhet vardenhet, String intygEnhetsId) {
        // Do the obvious check
        if (vardenhet.getId().equals(intygEnhetsId)) {
            return true;
        }

        // Search among sub units
        List<Mottagning> mottagningar = vardenhet.getMottagningar();
        if (mottagningar == null || mottagningar.isEmpty()) {
            return false;
        }

        return mottagningar.stream()
                .anyMatch(unit -> unit.getId().equals(intygEnhetsId));
    }

}
