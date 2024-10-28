/*
 * Copyright (C) 2024 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.web.controller.api;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.inera.intyg.infra.logmessages.ActivityType;
import se.inera.intyg.infra.logmessages.ResourceType;
import se.inera.intyg.infra.sjukfall.dto.IntygParametrar;
import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.auth.authorities.AuthoritiesConstants;
import se.inera.intyg.rehabstod.auth.pdl.PDLActivityStore;
import se.inera.intyg.rehabstod.logging.MdcLogConstants;
import se.inera.intyg.rehabstod.logging.PerformanceLogging;
import se.inera.intyg.rehabstod.service.Urval;
import se.inera.intyg.rehabstod.service.pdl.LogService;
import se.inera.intyg.rehabstod.service.sjukfall.SjukfallService;
import se.inera.intyg.rehabstod.service.sjukfall.dto.SjukfallPatientResponse;
import se.inera.intyg.rehabstod.service.sjukfall.util.PatientIdEncryption;
import se.inera.intyg.rehabstod.service.user.UserService;
import se.inera.intyg.rehabstod.web.controller.api.dto.AddVeToPatientViewRequest;
import se.inera.intyg.rehabstod.web.controller.api.dto.AddVgToPatientViewRequest;
import se.inera.intyg.rehabstod.web.controller.api.dto.GetSjukfallForPatientRequest;
import se.inera.intyg.rehabstod.web.controller.api.util.ControllerUtil;
import se.inera.intyg.rehabstod.web.model.PatientData;
import se.inera.intyg.rehabstod.web.model.SjukfallPatient;
import se.inera.intyg.schemas.contract.Personnummer;

/**
 * Created by Magnus Ekstrand on 03/02/16.
 */
@RestController
@RequestMapping("/api/sjukfall")
public class SjukfallController {

    private static final Logger LOG = LoggerFactory.getLogger(SjukfallController.class);

    private static final String SRS_UNAVAILABLE_HEADER = "SRS_UNAVAILABLE";
    private static final String KOMPLETTERING_INFO_UNAVAILABLE_HEADER = "KOMPLETTERING_INFO_UNAVAILABLE";
    private final SjukfallService sjukfallService;
    private final UserService userService;
    private final LogService logService;
    private final PatientIdEncryption patientIdEncryption;

    public SjukfallController(SjukfallService sjukfallService, UserService userService, LogService logService,
        PatientIdEncryption patientIdEncryption) {
        this.sjukfallService = sjukfallService;
        this.userService = userService;
        this.logService = logService;
        this.patientIdEncryption = patientIdEncryption;
    }

    @PostMapping(value = "/patient", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PerformanceLogging(eventAction = "get-sick-leave-for-patient", eventType = MdcLogConstants.EVENT_TYPE_ACCESSED)
    public ResponseEntity<SjukfallPatientResponse> getSjukfallForPatient(@RequestBody GetSjukfallForPatientRequest request) {

        // Get user from session
        RehabstodUser user = userService.getUser();
        Personnummer personnummer = Personnummer.createPersonnummer(
                request.getEncryptedPatientId() != null ? patientIdEncryption.decrypt(request.getEncryptedPatientId())
                    : request.getPatientId())
            .orElseThrow(() -> new IllegalArgumentException("Could not parse personnummer: " + request.getPatientId()));

        Collection<String> vardgivareIds = user.getSjfPatientVardgivare(personnummer.getPersonnummer());
        Collection<String> vardenhetIds = user.getSjfPatientVardenhet(personnummer.getPersonnummer());

        // Fetch sjukfall
        SjukfallPatientResponse response =
            getSjukfallForPatient(user, personnummer.getPersonnummer(), request.getAktivtDatum(), vardgivareIds, vardenhetIds);
        List<SjukfallPatient> sjukfall = response.getSjukfallList();

        // All filtrering av samtycke och spärrar har redan gjorts! De sjukfall vi har tillgång till
        // är de vi har rätt att ta del av. PDL-loggning behöver endast ske för den patientvy som har öppnats.
        // Loggning behöver bara ske första gången som patientens patientvy visats under den pågående
        // inloggningen för användaren.

        // PDL-logging based on which sjukfall that are about to be displayed to user.
        LOG.debug("PDL logging - log which 'sjukfall' information that will be displayed to the user.");
        Consumer<PatientData> logPatientData = pd -> logSjukfallData(user, pd, ActivityType.READ, ResourceType.RESOURCE_TYPE_INTYG);
        sjukfall.stream()
            .flatMap(sp -> sp.getIntyg().stream())
            .forEach(logPatientData);

        // If at least one intyg for the patient has an SRS prediction, log this.
        if (sjukfall.stream().flatMap(sf -> sf.getIntyg().stream()).anyMatch(anyIntygHasRiskSignal()) && isActivatedForSRS(user)) {
            logSjukfallData(user, sjukfall.get(0).getIntyg().get(0), ActivityType.READ, ResourceType.RESOURCE_TYPE_PREDIKTION_SRS);
        }

        return buildSjukfallPatientResponse(response.isSrsError(), response);
    }

    /**
     * Register a 'vardgivare' to be included in the calculation of the patient sjukfall view.
     */
    @PostMapping(value = "/patient/addVardgivare", consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @PerformanceLogging(eventAction = "add-care-provider-to-patient-view", eventType = MdcLogConstants.EVENT_TYPE_CHANGE)
    public ResponseEntity<Collection<String>> addVgToPatientView(@RequestBody AddVgToPatientViewRequest request) {
        // Get logged in user
        RehabstodUser user = userService.getUser();

        try {
            Optional<Personnummer> personnummer = Personnummer.createPersonnummer(request.getPatientId());
            if (personnummer.isEmpty()) {
                throw new RuntimeException("error parsing personnummer");
            }

            user.addSjfPatientVardgivare(personnummer.get().getPersonnummer(), request.getVardgivareId());
            return ResponseEntity.ok(user.getSjfPatientVardgivare().get(personnummer.get().getPersonnummer()));

        } catch (Exception e) {
            LOG.error("Error adding 'vardgivare' to be included in calculation of a patients sjukfall", e);
            return ResponseEntity.unprocessableEntity().body(new ArrayList<>());
        }
    }

    /**
     * Register a 'vardenhet' to be included in the calculation of the patient sjukfall view.
     */
    @PostMapping(value = "/patient/addVardenhet", consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @PerformanceLogging(eventAction = "add-care-unit-to-patient-view", eventType = MdcLogConstants.EVENT_TYPE_CHANGE)
    public ResponseEntity<Collection<String>> addVeToPatientView(@RequestBody AddVeToPatientViewRequest request) {
        // Get logged in user
        RehabstodUser user = userService.getUser();

        try {
            Optional<Personnummer> personnummer = Personnummer.createPersonnummer(request.getPatientId());
            if (personnummer.isEmpty()) {
                throw new RuntimeException("error parsing personnummer");
            }

            user.addSjfPatientVardenhet(personnummer.get().getPersonnummer(), request.getVardenhetId());
            return ResponseEntity.ok(user.getSjfPatientVardenhet().get(personnummer.get().getPersonnummer()));

        } catch (Exception e) {
            LOG.error("Error adding 'vardenhet' to be included in calculation of a patients sjukfall", e);
            return ResponseEntity.unprocessableEntity().body(new ArrayList<>());
        }
    }

    // For PatientData
    private Predicate<PatientData> anyIntygHasRiskSignal() {
        return pd -> pd.getRiskSignal() != null && pd.getRiskSignal().getRiskKategori() >= 1;
    }

    private SjukfallPatientResponse getSjukfallForPatient(RehabstodUser user, String patientId, LocalDate date,
        Collection<String> vgHsaId, Collection<String> veHsaId) {

        String currentVardgivarId = user.getValdVardgivare().getId();
        String enhetsId = ControllerUtil.getEnhetsIdForQueryingIntygstjansten(user);
        String lakareId = user.getHsaId();
        Urval urval = user.getUrval();

        IntygParametrar parameters = new IntygParametrar(
            ControllerUtil.getMaxGlapp(user), ControllerUtil.getMaxDagarSedanSjukfallAvslut(user), date);

        LOG.debug("Calling the 'sjukfall' service to get a list of detailed 'sjukfall' for one patient.");
        return sjukfallService.getByPatient(currentVardgivarId, enhetsId, lakareId, patientId, urval, parameters, vgHsaId, veHsaId);
    }

    private void logSjukfallData(RehabstodUser user, PatientData patientData, ActivityType activityType, ResourceType resourceType) {

        if (patientData == null) {
            throw new IllegalArgumentException("Cannot create PDL log statements, sjukfallPatient was null");
        }
        logSjukfallData(user, patientData, patientData.getVardenhetId(), activityType, resourceType);
    }

    private void logSjukfallData(RehabstodUser user, PatientData patientData, String enhetsId,
        ActivityType activityType, ResourceType resourceType) {

        if (patientData == null) {
            throw new IllegalArgumentException("Cannot create PDL log statements, patientData was null");
        }
        if (enhetsId == null) {
            throw new IllegalArgumentException("Cannot create PDL log statements, enhetsId was null");
        }

        String patientId = patientData.getPatient().getId();
        boolean isInStore =
            PDLActivityStore.isActivityInStore(enhetsId, patientId, activityType, resourceType, user.getStoredActivities());

        if (!isInStore) {
            logService.logSjukfallData(patientData, activityType, resourceType);
            PDLActivityStore.addActivityToStore(enhetsId, patientId, activityType, resourceType, user.getStoredActivities());
        }
    }

    private ResponseEntity<SjukfallPatientResponse> buildSjukfallPatientResponse(boolean srsError, SjukfallPatientResponse payload) {
        if (!srsError) {
            return new ResponseEntity<>(payload, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(payload, buildServicesUnavailableHeader(srsError, false), HttpStatus.OK);
        }
    }

    private HttpHeaders buildServicesUnavailableHeader(boolean srsError, boolean kompletteringInfoError) {
        HttpHeaders headers = new HttpHeaders();
        if (srsError) {
            headers.put(SRS_UNAVAILABLE_HEADER, Collections.singletonList("true"));
        }
        if (kompletteringInfoError) {
            headers.put(KOMPLETTERING_INFO_UNAVAILABLE_HEADER, Collections.singletonList("true"));
        }
        return headers;
    }

    private boolean isActivatedForSRS(RehabstodUser user) {
        return user.isFeatureActive(AuthoritiesConstants.FEATURE_SRS);
    }
}
