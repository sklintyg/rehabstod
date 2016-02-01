package se.inera.intyg.rehabstod.web.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.inera.intyg.rehabstod.service.certificate.CertificateService;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.IntygsData;

import java.util.List;

/**
 * Temporary controller, delete this!!
 */
@RestController
@RequestMapping("/api/certificate")
public class CertificateController {

    @Autowired
    private CertificateService certificateService;

    @RequestMapping(value = "/{hsaId}")
    public List<IntygsData> getCertificatesForCareUnit(@PathVariable("hsaId") String hsaId) {
         return certificateService.getIntygsData(hsaId);
    }

}
