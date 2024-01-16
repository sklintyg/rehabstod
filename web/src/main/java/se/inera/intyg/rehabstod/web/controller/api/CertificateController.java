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
import se.inera.intyg.rehabstod.service.sjukfall.util.PatientIdEncryption;
import se.inera.intyg.rehabstod.web.controller.api.dto.GetAGCertificatesForPersonRequest;
import se.inera.intyg.rehabstod.web.controller.api.dto.GetAGCertificatesForPersonResponse;
import se.inera.intyg.rehabstod.web.controller.api.dto.GetDoctorsForUnitResponse;
import se.inera.intyg.rehabstod.web.controller.api.dto.GetLUCertificatesForCareUnitRequest;
import se.inera.intyg.rehabstod.web.controller.api.dto.GetLUCertificatesForCareUnitResponse;
import se.inera.intyg.rehabstod.web.controller.api.dto.GetLUCertificatesForPersonRequest;
import se.inera.intyg.rehabstod.web.controller.api.dto.GetLUCertificatesForPersonResponse;

@RestController
@RequestMapping("/api/certificate")
public class CertificateController {

    private static final Logger LOG = LoggerFactory.getLogger(CertificateController.class);

    private final PatientIdEncryption patientIdEncryption;

    private final CertificateService certificateService;

    @Autowired
    public CertificateController(PatientIdEncryption patientIdEncryption, CertificateService certificateService) {
        this.patientIdEncryption = patientIdEncryption;
        this.certificateService = certificateService;
    }

    @RequestMapping(value = "/lu/unit", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetLUCertificatesForCareUnitResponse> getLUForCareUnit(@RequestBody GetLUCertificatesForCareUnitRequest request) {
        LOG.info("Getting LU certificates for care unit");

        final var response = certificateService.getLUCertificatesForCareUnit(request);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/lu/person", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetLUCertificatesForPersonResponse> getLUForPerson(@RequestBody GetLUCertificatesForPersonRequest request) {
        LOG.info("Getting LU certificates for person");

        var response = certificateService.getLUCertificatesForPerson(
            request.getEncryptedPatientId() != null
                ? patientIdEncryption.decrypt(request.getEncryptedPatientId()) : request.getPersonId()
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/lu/doctors", method = RequestMethod.GET)
    public ResponseEntity<GetDoctorsForUnitResponse> getDoctorsForUnit() {
        LOG.info("Getting LU signing doctors for unit");

        var response = certificateService.getDoctorsForUnit();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/ag/person", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetAGCertificatesForPersonResponse> getAGForPerson(@RequestBody GetAGCertificatesForPersonRequest request) {
        LOG.info("Getting AG certificates for person");

        var response = certificateService.getAGCertificatesForPerson(request.getEncryptedPatientId() != null
            ? patientIdEncryption.decrypt(request.getEncryptedPatientId()) : request.getPersonId()
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
