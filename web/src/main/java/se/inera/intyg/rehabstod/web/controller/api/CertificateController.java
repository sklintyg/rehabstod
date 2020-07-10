/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
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

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import se.inera.intyg.rehabstod.service.certificate.CertificateService;
import se.inera.intyg.rehabstod.web.controller.api.dto.GetAGCertificatesForPersonRequest;
import se.inera.intyg.rehabstod.web.controller.api.dto.GetLUCertificatesForCareUnitRequest;
import se.inera.intyg.rehabstod.web.controller.api.dto.GetLUCertificatesForPersonRequest;
import se.inera.intyg.rehabstod.web.model.AGCertificate;
import se.inera.intyg.rehabstod.web.model.LUCertificate;

@RestController
@RequestMapping("/api/certificate")
public class CertificateController {

    private static final Logger LOG = LoggerFactory.getLogger(CertificateController.class);

    private final CertificateService certificateService;

    @Autowired
    public CertificateController(CertificateService certificateService) {
        this.certificateService = certificateService;
    }

    @RequestMapping(value = "/lu/unit", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LUCertificate>> getLUForCareUnit(@RequestBody GetLUCertificatesForCareUnitRequest request) {
        LOG.info("Getting LU certificates for care unit");

        var certificateList = certificateService
            .getLUCertificatesForCareUnit(request.getFromDate(), request.getToDate());

        return new ResponseEntity<>(certificateList, HttpStatus.OK);
    }

    @RequestMapping(value = "/lu/person", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LUCertificate>> getLUForPerson(@RequestBody GetLUCertificatesForPersonRequest request) {
        LOG.info("Getting LU certificates for person");

        var certificateList = certificateService
            .getLUCertificatesForPerson(request.getPersonId());

        return new ResponseEntity<>(certificateList, HttpStatus.OK);
    }

    @RequestMapping(value = "/ag/person", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AGCertificate>> getAGForPerson(@RequestBody GetAGCertificatesForPersonRequest request) {
        LOG.info("Getting AG certificates for person");

        var certificateList = certificateService
            .getAGCertificatesForPerson(request.getPersonId());

        return new ResponseEntity<>(certificateList, HttpStatus.OK);
    }
}
