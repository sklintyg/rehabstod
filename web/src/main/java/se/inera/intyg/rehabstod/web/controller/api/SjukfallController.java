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
package se.inera.intyg.rehabstod.web.controller.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import se.inera.intyg.infra.logmessages.ActivityType;
import se.inera.intyg.infra.logmessages.ResourceType;
import se.inera.intyg.infra.sjukfall.dto.IntygParametrar;
import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.auth.pdl.PDLActivityStore;
import se.inera.intyg.rehabstod.service.Urval;
import se.inera.intyg.rehabstod.service.export.pdf.PdfExportService;
import se.inera.intyg.rehabstod.service.export.pdf.PdfExportServiceException;
import se.inera.intyg.rehabstod.service.export.util.ExportUtil;
import se.inera.intyg.rehabstod.service.export.xlsx.XlsxExportService;
import se.inera.intyg.rehabstod.service.pdl.LogService;
import se.inera.intyg.rehabstod.service.sjukfall.SjukfallService;
import se.inera.intyg.rehabstod.service.sjukfall.dto.SjukfallEnhetResponse;
import se.inera.intyg.rehabstod.service.sjukfall.dto.SjukfallPatientResponse;
import se.inera.intyg.rehabstod.service.sjukfall.dto.SjukfallSummary;
import se.inera.intyg.rehabstod.service.user.UserService;
import se.inera.intyg.rehabstod.web.controller.api.dto.AddVgToPatientViewRequest;
import se.inera.intyg.rehabstod.web.controller.api.dto.GetSjukfallForPatientRequest;
import se.inera.intyg.rehabstod.web.controller.api.dto.GetSjukfallRequest;
import se.inera.intyg.rehabstod.web.controller.api.dto.PrintSjukfallRequest;
import se.inera.intyg.rehabstod.web.controller.api.util.ControllerUtil;
import se.inera.intyg.rehabstod.web.model.PatientData;
import se.inera.intyg.rehabstod.web.model.SjukfallEnhet;
import se.inera.intyg.rehabstod.web.model.SjukfallPatient;
import se.inera.intyg.schemas.contract.Personnummer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by Magnus Ekstrand on 03/02/16.
 */
@RestController
@RequestMapping("/api/sjukfall")
public class SjukfallController {

    private static final Logger LOG = LoggerFactory.getLogger(SjukfallController.class);

    private static final String SRS_UNAVAILABLE_HEADER = "SRS_UNAVAILABLE";

    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH-mm");

    @Autowired
    private SjukfallService sjukfallService;

    @Autowired
    private UserService userService;

    @Autowired
    private LogService logService;

    @Autowired
    private XlsxExportService xlsxExportService;

    @Autowired
    private PdfExportService pdfExportService;

    @RequestMapping(value = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SjukfallEnhet>> getSjukfallForCareUnit(@RequestBody GetSjukfallRequest request) {

        // Get user from session
        RehabstodUser user = userService.getUser();

        // Fetch sjukfall
        SjukfallEnhetResponse response = getSjukfallForCareUnit(user, request);
        List<SjukfallEnhet> sjukfall = response.getSjukfallList();

        // PDL-logging based on which sjukfall that are about to be displayed to user.
        LOG.debug("PDL logging - log which 'sjukfall' that will be displayed to the user.");
        logSjukfallData(user, sjukfall, ActivityType.READ, ResourceType.RESOURCE_TYPE_SJUKFALL);
        logSjukfallData(user, filterHavingRiskSignal(sjukfall), ActivityType.READ, ResourceType.RESOURCE_TYPE_PREDIKTION_SRS);
        return buildSjukfallEnhetResponse(response.isSrsError(), sjukfall);
    }

    @RequestMapping(value = "/patient", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SjukfallPatientResponse> getSjukfallForPatient(@RequestBody GetSjukfallForPatientRequest request) {

        // Get user from session
        RehabstodUser user = userService.getUser();

        Personnummer personnummer = Personnummer.createPersonnummer(request.getPatientId())
                .orElseThrow(() -> new IllegalArgumentException("Could not parse personnummer: " + request.getPatientId()));

        Collection<String> vardgivareIds = user.getSjfPatientVardgivareForPatient(personnummer.getPersonnummer());

        // Fetch sjukfall
        SjukfallPatientResponse response = getSjukfallForPatient(user, request.getPatientId(), request.getAktivtDatum(), vardgivareIds);
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
        if (sjukfall.stream().flatMap(sf -> sf.getIntyg().stream()).anyMatch(anyIntygHasRiskSignal())) {
            logSjukfallData(user, sjukfall.get(0).getIntyg().get(0), ActivityType.READ, ResourceType.RESOURCE_TYPE_PREDIKTION_SRS);
        }

        return buildSjukfallPatientResponse(response.isSrsError(), response);
    }

    /**
     * Custom errorhandler for export error handling. For the export requests, we don't want the generic json response
     * error handling, this handler will instead redirect the client to our generic error page with a custom reason
     * code.
     *
     * @param request
     * @param response
     * @param ex
     * @throws IOException
     */
    @ExceptionHandler(RehabExportException.class)
    public void handleExportException(HttpServletRequest request, HttpServletResponse response,
            RehabExportException ex) throws IOException {

        LOG.error("RehabExportException caught - redirecting to errorpage", ex.getException());
        response.sendRedirect(request.getContextPath() + "/error.jsp?reason=exporterror");
    }

    @RequestMapping(value = "/pdf", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<ByteArrayResource> getSjukfallForCareUnitAsPdf(@ModelAttribute PrintSjukfallRequest request) {
        try {
            // Get user from session
            RehabstodUser user = userService.getUser();

            // Fetch sjukfall
            List<SjukfallEnhet> sjukfall = getSjukfallForCareUnit(user, request).getSjukfallList();
            List<SjukfallEnhet> finalList = ExportUtil.sortForExport(request.getPersonnummer(), sjukfall);

            byte[] pdfData = pdfExportService.export(finalList, request, user, sjukfall.size());

            // PDL-logging based on which sjukfall that are about to be exported. Only perform if PDF export was OK.
            LOG.debug("PDL logging - log which 'sjukfall' that are about to be exported in PDF format.");
            logSjukfallData(user, finalList, ActivityType.PRINT, ResourceType.RESOURCE_TYPE_SJUKFALL);
            logSjukfallData(user, filterHavingRiskSignal(finalList), ActivityType.PRINT, ResourceType.RESOURCE_TYPE_PREDIKTION_SRS);

            HttpHeaders respHeaders = getHttpHeaders("application/pdf",
                    pdfData.length, ".pdf", user);

            return new ResponseEntity<>(new ByteArrayResource(pdfData), respHeaders, HttpStatus.OK);

        } catch (PdfExportServiceException e) {
            throw new RehabExportException("Failed to create PDF export", e);
        }
    }

    @RequestMapping(value = "/xlsx", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<ByteArrayResource> getSjukfallForCareUnitAsXLSX(@ModelAttribute PrintSjukfallRequest request) {
        try {
            // Get user from session
            RehabstodUser user = userService.getUser();

            // Fetch sjukfall
            List<SjukfallEnhet> sjukfall = getSjukfallForCareUnit(user, request).getSjukfallList();
            List<SjukfallEnhet> finalList = ExportUtil.sortForExport(request.getPersonnummer(), sjukfall);
            byte[] data = xlsxExportService.export(finalList, request, user, sjukfall.size());

            // PDL-logging based on which sjukfall that are about to be exported. Only perform if XLSX export was OK.
            LOG.debug("PDL logging - log which 'sjukfall' that are about to be exported in XLSX format.");
            logSjukfallData(user, finalList, ActivityType.PRINT, ResourceType.RESOURCE_TYPE_SJUKFALL);
            logSjukfallData(user, filterHavingRiskSignal(finalList), ActivityType.PRINT, ResourceType.RESOURCE_TYPE_PREDIKTION_SRS);

            HttpHeaders respHeaders = getHttpHeaders("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    data.length, ".xlsx", user);

            return new ResponseEntity<>(new ByteArrayResource(data), respHeaders, HttpStatus.OK);

        } catch (RuntimeException | IOException e) {
            throw new RehabExportException("Failed to create XLS export", e);
        }
    }

    @RequestMapping(value = "/summary", method = RequestMethod.GET)
    public SjukfallSummary getUnitCertificateSummary() {
        // Get user from session
        RehabstodUser user = userService.getUser();

        String enhetsId = ControllerUtil.getEnhetsIdForQueryingIntygstjansten(user);
        String mottagningsId = getMottagningsId(user);
        String hsaId = user.getHsaId();
        Urval urval = user.getDefaultUrval();

        IntygParametrar parameters = new IntygParametrar(0, LocalDate.now());
        return sjukfallService.getSummary(enhetsId, mottagningsId, hsaId, urval, parameters);
    }

    /**
     * Register vardgivare to be include in patient view sjukfall calculations during session.
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/patient/addVardgivare", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<String>> addVgToPatientView(@RequestBody AddVgToPatientViewRequest request) {

        // Get logged in user
        RehabstodUser user = userService.getUser();

        try {
            Optional<Personnummer> personnummer = Personnummer.createPersonnummer(request.getPatientId());

            if (!personnummer.isPresent()) {
                throw new RuntimeException("error parsing personnummer");
            }

            user.addSjfPatientVardgivare(personnummer.get().getPersonnummer(), request.getVardgivareId());

            return ResponseEntity.ok(user.getSjfPatientVardgivare().get(personnummer.get().getPersonnummer()));

        } catch (Exception e) {
            LOG.error("Error giving consent", e);
            return  ResponseEntity.unprocessableEntity().body(new ArrayList<>());
        }
    }

    // - - - private scope - - -

    private String getAttachmentFilename(RehabstodUser user, String extension) {
        return "sjukfall-" + user.getValdVardenhet().getNamn() + "-" + LocalDateTime.now().format(dateTimeFormatter) + extension;
    }

    private List<SjukfallEnhet> filterHavingRiskSignal(List<SjukfallEnhet> finalList) {
        return finalList.stream()
                .filter(hasRiskSignal())
                .collect(Collectors.toList());
    }

    // For SjukfallEnhet
    private Predicate<SjukfallEnhet> hasRiskSignal() {
        return se -> se.getRiskSignal() != null && se.getRiskSignal().getRiskKategori() > 1;
    }

    // For PatientData
    private Predicate<PatientData> anyIntygHasRiskSignal() {
        return pd -> pd.getRiskSignal() != null && pd.getRiskSignal().getRiskKategori() > 1;
    }

    private Predicate<PatientData> isNotBlocked(RehabstodUser user, SjukfallPatientResponse response) {
        return pd -> user.getValdVardgivare().getId().equals(pd.getVardgivareId())
                && !user.getValdVardenhet().getId().equals(pd.getVardenhetId())
                && !response.getSjfMetaData().getVardenheterInomVGMedSparr().contains(pd.getVardenhetId());
    }

    private HttpHeaders getHttpHeaders(String contentType, long contentLength, String filenameExtension, RehabstodUser user) {
        HttpHeaders respHeaders = new HttpHeaders();
        respHeaders.set(HttpHeaders.CONTENT_TYPE, contentType);
        respHeaders.setContentLength(contentLength);
        respHeaders.setContentDispositionFormData("attachment", getAttachmentFilename(user, filenameExtension));
        return respHeaders;
    }

    private SjukfallEnhetResponse getSjukfallForCareUnit(RehabstodUser user, GetSjukfallRequest request) {
        String enhetsId = ControllerUtil.getEnhetsIdForQueryingIntygstjansten(user);
        String mottagningsId = getMottagningsId(user);
        String lakarId = user.getHsaId();

        Urval urval = user.getUrval();
        IntygParametrar parameters = new IntygParametrar(ControllerUtil.getMaxGlapp(user),
                        ControllerUtil.getMaxDagarSedanSjukfallAvslut(user), request.getAktivtDatum());

        LOG.debug("Calling the 'sjukfall' service to get a list of 'sjukfall' from care unit {}.", enhetsId);
        return sjukfallService.getByUnit(enhetsId, mottagningsId, lakarId, urval, parameters);
    }

    private SjukfallPatientResponse getSjukfallForPatient(RehabstodUser user, String patientId, LocalDate date,
                                                          Collection<String> vgHsaId) {

        String enhetsId = ControllerUtil.getEnhetsIdForQueryingIntygstjansten(user);
        String currentVardgivarHsaId = user.getValdVardgivare().getId();
        String lakareId = user.getHsaId();
        Urval urval = user.getUrval();

        IntygParametrar parameters = new IntygParametrar(
                ControllerUtil.getMaxGlapp(user), ControllerUtil.getMaxDagarSedanSjukfallAvslut(user), date);

        LOG.debug("Calling the 'sjukfall' service to get a list of detailed 'sjukfall' for one patient.");
        return sjukfallService.getByPatient(currentVardgivarHsaId, enhetsId, lakareId, patientId, urval, parameters, vgHsaId);
    }

    private void logSjukfallData(RehabstodUser user, List<SjukfallEnhet> sjukfallList,
            ActivityType activityType, ResourceType resourceType) {

        String enhetsId = ControllerUtil.getEnhetsIdForQueryingIntygstjansten(user);
        if (enhetsId == null) {
            throw new IllegalArgumentException("Cannot create PDL log statements, enhetsId was null");
        }

        List<SjukfallEnhet> sjukfallToLog = PDLActivityStore.getActivitiesNotInStore(enhetsId, sjukfallList, activityType, resourceType,
                user.getStoredActivities());
        logService.logSjukfallData(sjukfallToLog, activityType, resourceType);
        PDLActivityStore.addActivitiesToStore(enhetsId, sjukfallToLog, activityType, resourceType, user.getStoredActivities());
    }

    private void logSjukfallData(RehabstodUser user, PatientData patientData,
                                 ActivityType activityType, ResourceType resourceType) {

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

    private String getMottagningsId(RehabstodUser user) {
        if (user.isValdVardenhetMottagning()) {
            return user.getValdVardenhet().getId();
        }
        return null;
    }

    // -- Response builders
    private ResponseEntity<List<SjukfallEnhet>> buildSjukfallEnhetResponse(boolean srsError, List<SjukfallEnhet> payload) {
        if (!srsError) {
            return new ResponseEntity<>(payload, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(payload, buildSrsUnavailableHeader(), HttpStatus.OK);
        }
    }

    private ResponseEntity<SjukfallPatientResponse> buildSjukfallPatientResponse(boolean srsError, SjukfallPatientResponse payload) {
        if (!srsError) {
            return new ResponseEntity<>(payload, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(payload, buildSrsUnavailableHeader(), HttpStatus.OK);
        }
    }

    private HttpHeaders buildSrsUnavailableHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.put(SRS_UNAVAILABLE_HEADER, Collections.singletonList("true"));
        return headers;
    }
}
