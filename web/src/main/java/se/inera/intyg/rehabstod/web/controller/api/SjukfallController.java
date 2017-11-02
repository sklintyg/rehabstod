/*
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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
import se.inera.intyg.infra.integration.hsa.model.Mottagning;
import se.inera.intyg.infra.integration.hsa.model.Vardenhet;
import se.inera.intyg.infra.integration.hsa.model.Vardgivare;
import se.inera.intyg.infra.logmessages.ActivityType;
import se.inera.intyg.infra.logmessages.ResourceType;
import se.inera.intyg.infra.security.authorities.AuthoritiesException;
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
import se.inera.intyg.rehabstod.web.controller.api.dto.GetSjukfallRequest;
import se.inera.intyg.rehabstod.web.controller.api.dto.PrintSjukfallRequest;
import se.inera.intyg.rehabstod.web.model.SjukfallEnhet;
import se.inera.intyg.rehabstod.web.model.SjukfallPatient;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Magnus Ekstrand on 03/02/16.
 */
@RestController
@RequestMapping("/api/sjukfall")
public class SjukfallController {

    private static final Logger LOG = LoggerFactory.getLogger(SjukfallController.class);

    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

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
        RehabstodUser user = getRehabstodUser();

        // Fetch sjukfall
        SjukfallEnhetResponse response = getSjukfallForCareUnit(user, request);
        List<SjukfallEnhet> sjukfall = response.getSjukfallList();

        // PDL-logging based on which sjukfall that are about to be displayed to user.
        LOG.debug("PDL logging - log which 'sjukfall' that will be displayed to the user.");
        logSjukfallData(user, sjukfall, ActivityType.READ, ResourceType.RESOURCE_TYPE_OVERSIKT_SJUKFALL);
        return buildResponse(response.isSrsError(), sjukfall);
    }

    @RequestMapping(value = "/patient", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SjukfallPatient>> getSjukfallForPatient(@RequestBody GetSjukfallRequest request) {

        // Get user from session
        RehabstodUser user = getRehabstodUser();

        // Fetch sjukfall
        SjukfallPatientResponse response = getSjukfallForPatient(user, request);
        List<SjukfallPatient> sjukfall = response.getSjukfallList();
        // PDL-logging based on which sjukfall that are about to be displayed to user.
        LOG.debug("PDL logging - log which detailed 'sjukfall' that will be displayed to the user.");
        logSjukfallData(user, sjukfall.get(0), ActivityType.READ, ResourceType.RESOURCE_TYPE_OVERSIKT_SJUKFALL_HISTORIK);

        return buildResponse(response.isSrsError(), sjukfall);
    }

    private ResponseEntity buildResponse(boolean srsError, List payload) {
        if (!srsError) {
            return new ResponseEntity(payload, HttpStatus.OK);
        } else {
            HttpHeaders headers = new HttpHeaders();
            headers.put("SRS_UNAVAILABLE", Arrays.asList("true"));
            return new ResponseEntity(payload, headers, HttpStatus.OK);
        }
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
            RehabstodUser user = getRehabstodUser();

            // Fetch sjukfall
            List<SjukfallEnhet> sjukfall = getSjukfallForCareUnit(user, request).getSjukfallList();
            List<SjukfallEnhet> finalList = ExportUtil.sortForExport(request.getPersonnummer(), sjukfall);

            byte[] pdfData = pdfExportService.export(finalList, request, user, sjukfall.size());

            // PDL-logging based on which sjukfall that are about to be exported. Only perform if PDF export was OK.
            LOG.debug("PDL logging - log which 'sjukfall' that are about to be exported in PDF format.");
            logSjukfallData(user, finalList, ActivityType.PRINT, ResourceType.RESOURCE_TYPE_OVERSIKT_SJUKFALL);

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
            RehabstodUser user = getRehabstodUser();

            // Fetch sjukfall
            List<SjukfallEnhet> sjukfall = getSjukfallForCareUnit(user, request).getSjukfallList();
            List<SjukfallEnhet> finalList = ExportUtil.sortForExport(request.getPersonnummer(), sjukfall);

            byte[] data = xlsxExportService.export(finalList, request, user.getUrval(), sjukfall.size());

            // PDL-logging based on which sjukfall that are about to be exported. Only perform if XLSX export was OK.
            LOG.debug("PDL logging - log which 'sjukfall' that are about to be exported in XLSX format.");
            logSjukfallData(user, finalList, ActivityType.PRINT, ResourceType.RESOURCE_TYPE_OVERSIKT_SJUKFALL);

            HttpHeaders respHeaders = getHttpHeaders("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                data.length, ".xlsx", user);

            return new ResponseEntity<>(new ByteArrayResource(data), respHeaders, HttpStatus.OK);

        } catch (RuntimeException | IOException e) {
            throw new RehabExportException("Failed to create XLS export", e);
        }
    }

    protected String getAttachmentFilename(RehabstodUser user, String extension) {
        return "sjukfall-" + user.getValdVardenhet().getNamn() + "-" + LocalDateTime.now().format(dateTimeFormatter) + extension;
    }

    @RequestMapping(value = "/summary", method = RequestMethod.GET)
    public SjukfallSummary getUnitCertificateSummary() {
        // Get user from session
        RehabstodUser user = getRehabstodUser();

        String enhetsId = getEnhetsIdForQueryingIntygstjansten(user);
        String mottagningsId = getMottagningsId(user);
        String hsaId = user.getHsaId();
        Urval urval = user.getDefaultUrval();

        GetSjukfallRequest request = new GetSjukfallRequest();
        request.setMaxIntygsGlapp(0);

        return sjukfallService.getSummary(enhetsId, mottagningsId, hsaId, urval, request);
    }

    // - - - private scope - - -

    private HttpHeaders getHttpHeaders(String contentType, long contentLength, String filenameExtension, RehabstodUser user) {
        HttpHeaders respHeaders = new HttpHeaders();
        respHeaders.set(HttpHeaders.CONTENT_TYPE, contentType);
        respHeaders.setContentLength(contentLength);
        respHeaders.setContentDispositionFormData("attachment", getAttachmentFilename(user, filenameExtension));
        return respHeaders;
    }

    private SjukfallEnhetResponse getSjukfallForCareUnit(RehabstodUser user, GetSjukfallRequest request) {
        String enhetsId = getEnhetsIdForQueryingIntygstjansten(user);
        String mottagningsId = getMottagningsId(user);
        String lakarId = user.getHsaId();
        Urval urval = user.getUrval();

        LOG.debug("Calling the 'sjukfall' service to get a list of 'sjukfall' from care unit {}.", enhetsId);
        return sjukfallService.getByUnit(enhetsId, mottagningsId, lakarId, urval, request);
    }

    private SjukfallPatientResponse getSjukfallForPatient(RehabstodUser user, GetSjukfallRequest request) {
        String enhetsId = getEnhetsIdForQueryingIntygstjansten(user);
        String lakarId = user.getHsaId();
        Urval urval = user.getUrval();

        LOG.debug("Calling the 'sjukfall' service to get a list of detailed 'sjukfall' for one patient.");
        return sjukfallService.getByPatient(enhetsId, lakarId, urval, request);
    }

    private void logSjukfallData(RehabstodUser user, List<SjukfallEnhet> sjukfallList,
                                 ActivityType activityType, ResourceType resourceType) {

        String enhetsId = getEnhetsIdForQueryingIntygstjansten(user);
        if (enhetsId == null) {
            throw new IllegalArgumentException("Cannot create PDL log statements, enhetsId was null");
        }

        List<SjukfallEnhet> sjukfallToLog =
            PDLActivityStore.getActivitiesNotInStore(enhetsId, sjukfallList, activityType, resourceType, user.getStoredActivities());
        logService.logSjukfallData(sjukfallToLog, activityType, resourceType);
        PDLActivityStore.addActivitiesToStore(enhetsId, sjukfallToLog, activityType, resourceType, user.getStoredActivities());
    }

    private void logSjukfallData(RehabstodUser user, SjukfallPatient sjukfallPatient,
                                 ActivityType activityType, ResourceType resourceType) {

        String enhetsId = getEnhetsIdForQueryingIntygstjansten(user);
        if (enhetsId == null) {
            throw new IllegalArgumentException("Cannot create PDL log statements, enhetsId was null");
        }

        boolean isInStore =
            PDLActivityStore.isActivityInStore(enhetsId, sjukfallPatient, activityType, resourceType, user.getStoredActivities());

        if (!isInStore) {
            logService.logSjukfallData(sjukfallPatient, activityType, resourceType);
            PDLActivityStore.addActivityToStore(enhetsId, sjukfallPatient, activityType, resourceType, user.getStoredActivities());
        }
    }

    private String getEnhetsIdForQueryingIntygstjansten(RehabstodUser user) {
        if (user.isValdVardenhetMottagning()) {
            // Must return PARENT id if selected unit is an underenhet aka mottagning.
            for (Vardgivare vg : user.getVardgivare()) {
                for (Vardenhet ve : vg.getVardenheter()) {
                    for (Mottagning m : ve.getMottagningar()) {
                        if (m.getId().equals(user.getValdVardenhet().getId())) {
                            return ve.getId();
                        }
                    }
                }
            }
            throw new IllegalStateException("User object is in invalid state. "
                    + "Current selected enhet is an underenhet, but no ID for the parent enhet was found.");
        } else {
            return user.getValdVardenhet().getId();
        }
    }

    private RehabstodUser getRehabstodUser() {
        RehabstodUser user = userService.getUser();
        if (user == null) {
            throw new AuthoritiesException("No user in session");
        }
        return user;
    }

    private String getMottagningsId(RehabstodUser user) {
        if (user.isValdVardenhetMottagning()) {
            return user.getValdVardenhet().getId();
        }
        return null;
    }
}
