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

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import se.inera.intyg.common.logmessages.ActivityType;
import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.auth.authorities.AuthoritiesException;
import se.inera.intyg.rehabstod.service.Urval;
import se.inera.intyg.rehabstod.service.export.util.ExportProcessorUtil;
import se.inera.intyg.rehabstod.service.export.xlsx.XlsxExportService;
import se.inera.intyg.rehabstod.service.pdl.LogService;
import se.inera.intyg.rehabstod.service.sjukfall.SjukfallService;
import se.inera.intyg.rehabstod.service.sjukfall.dto.SjukfallSummary;
import se.inera.intyg.rehabstod.service.user.UserService;
import se.inera.intyg.rehabstod.web.controller.api.dto.GetSjukfallRequest;
import se.inera.intyg.rehabstod.web.controller.api.dto.PrintSjukfallRequest;
import se.inera.intyg.rehabstod.web.model.InternalSjukfall;
import se.inera.intyg.rehabstod.web.model.Sjukfall;

/**
 * Created by Magnus Ekstrand on 03/02/16.
 */
@RestController
@RequestMapping("/api/sjukfall")
public class SjukfallController {

    @Autowired
    private SjukfallService sjukfallService;

    @Autowired
    private UserService userService;

    @Autowired
    private LogService logService;

    @Autowired
    private XlsxExportService xlsxExportService;

    @RequestMapping(value = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<Sjukfall> getSjukfallForCareUnit(@RequestBody GetSjukfallRequest request) {

        RehabstodUser user = userService.getUser();
        if (user == null) {
            throw new AuthoritiesException("No user in session");
        }

        String enhetsId = user.getValdVardenhet().getId();
        String hsaId = user.getHsaId();
        Urval urval = user.getUrval();
        List<InternalSjukfall> sjukfall = sjukfallService.getSjukfall(enhetsId, hsaId, urval, request);

        // PDL-logging based on which sjukfall that are about to be displayed to user.
        List<InternalSjukfall> sjukfallToLog = user.getPdlActivityStore().getActivitiesNotInStore(enhetsId, sjukfall, ActivityType.READ);
        logService.logSjukfallData(sjukfallToLog, ActivityType.READ);
        user.getPdlActivityStore().addActivitiesToStore(enhetsId, sjukfallToLog, ActivityType.READ);

        return sjukfall.stream().map(sf -> sf.getSjukfall()).collect(Collectors.toList());
    }

    @RequestMapping(value = "/pdf", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<Sjukfall> getSjukfallForCareUnitAsPDF(@RequestBody PrintSjukfallRequest request) {

        RehabstodUser user = userService.getUser();
        if (user == null) {
            throw new AuthoritiesException("No user in session");
        }

        String enhetsId = user.getValdVardenhet().getId();
        String hsaId = user.getHsaId();
        Urval urval = user.getUrval();
        List<InternalSjukfall> sjukfall = sjukfallService.getSjukfall(enhetsId, hsaId, urval, request);

        // PDL-logging based on which sjukfall that are about to be displayed to user.
        List<InternalSjukfall> sjukfallToLog = user.getPdlActivityStore().getActivitiesNotInStore(enhetsId, sjukfall, ActivityType.PRINT);
        logService.logSjukfallData(sjukfallToLog, ActivityType.PRINT);
        user.getPdlActivityStore().addActivitiesToStore(enhetsId, sjukfallToLog, ActivityType.PRINT);

        return sjukfall.stream().map(sf -> sf.getSjukfall()).collect(Collectors.toList());
    }

    @RequestMapping(value = "/xlsx", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<ByteArrayResource> getSjukfallForCareUnitAsXLSX(@ModelAttribute PrintSjukfallRequest request) {

        RehabstodUser user = userService.getUser();
        if (user == null) {
            throw new AuthoritiesException("No user in session");
        }

        String enhetsId = user.getValdVardenhet().getId();
        String hsaId = user.getHsaId();
        Urval urval = user.getUrval();
        List<InternalSjukfall> sjukfall = sjukfallService.getSjukfall(enhetsId, hsaId, urval, request);

        List<InternalSjukfall> finalList = ExportProcessorUtil.processForExport(request.getPersonnummer(), sjukfall);


        try {
            byte[] data = xlsxExportService.export(finalList, request);

            // PDL-logging based on which sjukfall that are about to be exported. Only perform if XLSX export was OK.
            List<InternalSjukfall> sjukfallToLog = user.getPdlActivityStore().getActivitiesNotInStore(enhetsId, finalList, ActivityType.PRINT);
            logService.logSjukfallData(sjukfallToLog, ActivityType.PRINT);
            user.getPdlActivityStore().addActivitiesToStore(enhetsId, sjukfallToLog, ActivityType.PRINT);

            HttpHeaders respHeaders = new HttpHeaders();
            respHeaders.set("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            respHeaders.setContentLength(data.length);
            respHeaders.setContentDispositionFormData("attachment", "sjukskrivningar-" + user.getValdVardenhet().getNamn() + "-" + LocalDateTime.now().toString("yyyy-MM-dd'T'HH:mm") + ".xlsx");

            return new ResponseEntity<>(new ByteArrayResource(data), respHeaders, HttpStatus.OK);

        } catch (IOException e) {
            // This should be handled a bit better...
            return new ResponseEntity<>(new ByteArrayResource(e.getMessage().getBytes()), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    @RequestMapping(value = "/summary", method = RequestMethod.GET)
    public SjukfallSummary getUnitCertificateSummary() {
        RehabstodUser user = userService.getUser();
        if (user == null) {
            throw new AuthoritiesException("No user in session");
        }

        String enhetsId = user.getValdVardenhet().getId();
        return sjukfallService.getSummary(enhetsId);
    }

}
