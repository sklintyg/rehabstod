/**
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
 *
 * This file is part of rehabstod (https://github.com/sklintyg/rehabstod).
 *
 * rehabstod is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * rehabstod is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.intyg.rehabstod.web.controller.api;

import com.itextpdf.text.DocumentException;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.inera.intyg.common.logmessages.ActivityType;
import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.auth.authorities.AuthoritiesException;
import se.inera.intyg.rehabstod.service.Urval;
import se.inera.intyg.rehabstod.service.export.pdf.PdfExportService;
import se.inera.intyg.rehabstod.service.export.util.ExportUtil;
import se.inera.intyg.rehabstod.service.export.xlsx.XlsxExportService;
import se.inera.intyg.rehabstod.service.pdl.LogService;
import se.inera.intyg.rehabstod.service.sjukfall.SjukfallService;
import se.inera.intyg.rehabstod.service.sjukfall.dto.SjukfallSummary;
import se.inera.intyg.rehabstod.service.user.UserService;
import se.inera.intyg.rehabstod.web.controller.api.dto.GetSjukfallRequest;
import se.inera.intyg.rehabstod.web.controller.api.dto.PrintSjukfallRequest;
import se.inera.intyg.rehabstod.web.model.InternalSjukfall;
import se.inera.intyg.rehabstod.web.model.Sjukfall;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Magnus Ekstrand on 03/02/16.
 */
@RestController
@RequestMapping("/api/sjukfall")
public class SjukfallController {

    private static final Logger LOG = LoggerFactory.getLogger(SjukfallController.class);

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
    public List<Sjukfall> getSjukfallForCareUnit(@RequestBody GetSjukfallRequest request) {

        // Get user from session
        RehabstodUser user = getRehabstodUser();

        // Fetch sjukfall
        List<InternalSjukfall> sjukfall = getSjukfall(user, request);

        // PDL-logging based on which sjukfall that are about to be displayed to user.
        LOG.debug("PDL logging - log which 'sjukfall' that are going to be displayed to the user.");
        logSjukfallData(user, sjukfall, ActivityType.READ);

        return sjukfall.stream().map(sf -> sf.getSjukfall()).sorted((f1, f2) -> f2.getStart().compareTo(f1.getStart())).collect(Collectors.toList());
    }

    @RequestMapping(value = "/pdf", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<ByteArrayResource> getSjukfallForCareUnitAsPdf(@ModelAttribute PrintSjukfallRequest request) {

        // Get user from session
        RehabstodUser user = getRehabstodUser();

        // Fetch sjukfall
        List<InternalSjukfall> sjukfall = getSjukfall(user, request);
        List<InternalSjukfall> finalList = ExportUtil.sortForExport(request.getPersonnummer(), sjukfall);

        try {
            byte[] pdfData = pdfExportService.export(finalList, request, user, sjukfall.size());

            // PDL-logging based on which sjukfall that are about to be exported. Only perform if PDF export was OK.
            LOG.debug("PDL logging - log which 'sjukfall' that are about to be exported in PDF format.");
            logSjukfallData(user, finalList, ActivityType.PRINT);

            HttpHeaders respHeaders = new HttpHeaders();
            respHeaders.setContentType(MediaType.parseMediaType("application/pdf"));
            respHeaders.setContentLength(pdfData.length);
            respHeaders.setContentDispositionFormData("attachment", getAttachmentFilename(user, ".pdf"));

            return new ResponseEntity<>(new ByteArrayResource(pdfData), respHeaders, HttpStatus.OK);

        } catch (DocumentException | IOException e) {
            LOG.error("Failed to create PDF export", e);
            // Would it be better if we could redirect to error.jsp in this case?
            return new ResponseEntity<>(new ByteArrayResource(e.getMessage().getBytes()), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/xlsx", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<ByteArrayResource> getSjukfallForCareUnitAsXLSX(@ModelAttribute PrintSjukfallRequest request) {

        // Get user from session
        RehabstodUser user = getRehabstodUser();

        // Fetch sjukfall
        List<InternalSjukfall> sjukfall = getSjukfall(user, request);
        List<InternalSjukfall> finalList = ExportUtil.sortForExport(request.getPersonnummer(), sjukfall);

        try {
            byte[] data = xlsxExportService.export(finalList, request, user.getUrval(), sjukfall.size());

            // PDL-logging based on which sjukfall that are about to be exported. Only perform if XLSX export was OK.
            LOG.debug("PDL logging - log which 'sjukfall' that are about to be exported in XLSX format.");
            logSjukfallData(user, finalList, ActivityType.PRINT);

            HttpHeaders respHeaders = new HttpHeaders();
            respHeaders.set("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            respHeaders.setContentLength(data.length);
            respHeaders.setContentDispositionFormData("attachment", getAttachmentFilename(user, ".xlsx"));

            return new ResponseEntity<>(new ByteArrayResource(data), respHeaders, HttpStatus.OK);

        } catch (IOException e) {
            // This should be handled a bit better...
            return new ResponseEntity<>(new ByteArrayResource(e.getMessage().getBytes()), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    protected String getAttachmentFilename(RehabstodUser user, String extension) {
        return "sjukfall-" + user.getValdVardenhet().getNamn() + "-" + LocalDateTime.now().toString("yyyy-MM-dd'T'HH:mm") + extension;
    }

    @RequestMapping(value = "/summary", method = RequestMethod.GET)
    public SjukfallSummary getUnitCertificateSummary() {
        // Get user from session
        RehabstodUser user = getRehabstodUser();

        String enhetsId = getEnhetsId(user);
        String hsaId = user.getHsaId();
        Urval urval = user.getDefaultUrval();

        GetSjukfallRequest request = new GetSjukfallRequest();
        request.setMaxIntygsGlapp(0);

        return sjukfallService.getSummary(enhetsId, hsaId, urval, request);
    }


    // - - - Private scope - - -

    private String getEnhetsId(RehabstodUser user) {
        return user.getValdVardenhet().getId();
    }

    private RehabstodUser getRehabstodUser() {
        RehabstodUser user = userService.getUser();
        if (user == null) {
            throw new AuthoritiesException("No user in session");
        }
        return user;
    }

    private List<InternalSjukfall> getSjukfall(RehabstodUser user, GetSjukfallRequest request) {
        String enhetsId = getEnhetsId(user);
        String hsaId = user.getHsaId();
        Urval urval = user.getUrval();

        LOG.debug("Calling the 'sjukfall' service to get a list of 'sjukfall' from care unit {}.", enhetsId);
        return sjukfallService.getSjukfall(enhetsId, hsaId, urval, request);
    }

    private void logSjukfallData(RehabstodUser user, List<InternalSjukfall> sjukfallList, ActivityType activityType) {
        List<InternalSjukfall> sjukfallToLog = user.getPdlActivityStore().getActivitiesNotInStore(getEnhetsId(user), sjukfallList, activityType);
        logService.logSjukfallData(sjukfallToLog, activityType);
        user.getPdlActivityStore().addActivitiesToStore(getEnhetsId(user), sjukfallToLog, activityType);
    }

}
