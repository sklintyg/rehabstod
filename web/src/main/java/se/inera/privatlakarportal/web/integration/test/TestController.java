package se.inera.privatlakarportal.web.integration.test;

import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;
import se.inera.privatlakarportal.hsa.stub.HsaHospPerson;
import se.inera.privatlakarportal.hsa.stub.HsaServiceStub;
import se.inera.privatlakarportal.integration.privatepractioner.services.IntegrationService;
import se.inera.privatlakarportal.persistence.model.Privatlakare;
import se.inera.privatlakarportal.persistence.repository.PrivatlakareRepository;
import se.inera.privatlakarportal.hsa.services.HospUpdateService;
import se.inera.privatlakarportal.service.CleanupService;
import se.inera.privatlakarportal.service.RegisterService;
import se.riv.infrastructure.directory.privatepractitioner.validateprivatepractitionerresponder.v1.ValidatePrivatePractitionerResponseType;

/**
 * Created by pebe on 2015-09-02.
 */
@RestController
@RequestMapping("/api/test")
@Profile({"dev", "testability-api"})
public class TestController {

    private static final Logger LOG = LoggerFactory.getLogger(TestController.class);

    @Autowired
    private RegisterService registerService;

    @Autowired
    private PrivatlakareRepository privatlakareRepository;

    @Autowired(required=false)
    private HsaServiceStub hsaServiceStub;

    @Autowired
    private HospUpdateService hospUpdateService;

    @Autowired
    private IntegrationService integrationService;

    @Autowired
    private CleanupService cleanupService;

    public TestController() {
        LOG.error("testability-api enabled. DO NOT USE IN PRODUCTION");
    }

    @RequestMapping(value = "/registration/{id}", method = RequestMethod.GET)
    public @ResponseBody
    Privatlakare getPrivatlakare(@PathVariable("id") String personId) {
        return privatlakareRepository.findByPersonId(personId);
    }

    @RequestMapping(value = "/registration/remove/{id}", method = RequestMethod.DELETE)
    public @ResponseBody
    boolean removePrivatlakare(@PathVariable("id") String personId) {
        return registerService.removePrivatlakare(personId);
    }

    @RequestMapping(value = "/registration/setname/{id}", method = RequestMethod.POST)
    public boolean setNamePrivatlakare(@PathVariable("id") String personId, @RequestBody String name) {
        Privatlakare privatlakare = privatlakareRepository.findByPersonId(personId);
        if (privatlakare == null) {
           LOG.error("Unable to find privatlakare with personId '{}'", personId);
           return false;
        }
        privatlakare.setFullstandigtNamn(name);
        privatlakareRepository.save(privatlakare);
        return true;
    }

    @RequestMapping(value = "/registration/setregistrationdate/{id}", method = RequestMethod.POST)
    public boolean setRegistrationDatePrivatlakare(@PathVariable("id") String personId, @RequestBody String date) {
        Privatlakare privatlakare = privatlakareRepository.findByPersonId(personId);
        if (privatlakare == null) {
            LOG.error("Unable to find privatlakare with personId '{}'", personId);
            return false;
        }
        privatlakare.setRegistreringsdatum(LocalDateTime.parse(date));
        privatlakareRepository.save(privatlakare);
        return true;
    }

    @Profile("hsa-stub")
    @RequestMapping(value = "/hosp/add", method = RequestMethod.POST)
    public void addHospPerson(@RequestBody HsaHospPerson hsaHospPerson) {
        hsaServiceStub.addHospPerson(hsaHospPerson);
    }

    @Profile("hsa-stub")
    @RequestMapping(value = "/hosp/remove/{id}", method = RequestMethod.DELETE)
    public void removeHospPerson(@PathVariable("id") String id) {
        hsaServiceStub.removeHospPerson(id);
    }

    @RequestMapping(value = "/hosp/update", method = RequestMethod.POST)
    public void updateHospInformation() {
        hospUpdateService.updateHospInformation();
    }

    @RequestMapping(value = "/cleanup/trigger", method = RequestMethod.POST)
    public void startCleanup() {
        cleanupService.cleanupPrivatlakare();
    }

    @RequestMapping(value = "/webcert/validatePrivatePractitioner/{id}", method = RequestMethod.POST)
    public ValidatePrivatePractitionerResponseType validatePrivatePractitioner(@PathVariable("id") String id) {
        return integrationService.validatePrivatePractitionerByPersonId(id);
    }
}
