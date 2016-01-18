package se.inera.privatlakarportal.web.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import se.inera.privatlakarportal.common.model.RegistrationStatus;
import se.inera.privatlakarportal.service.RegisterService;
import se.inera.privatlakarportal.service.model.RegistrationWithHospInformation;
import se.inera.privatlakarportal.service.model.SaveRegistrationResponseStatus;
import se.inera.privatlakarportal.service.postnummer.PostnummerService;
import se.inera.privatlakarportal.web.controller.api.dto.CreateRegistrationRequest;
import se.inera.privatlakarportal.web.controller.api.dto.CreateRegistrationResponse;
import se.inera.privatlakarportal.web.controller.api.dto.GetHospInformationResponse;
import se.inera.privatlakarportal.web.controller.api.dto.GetOmradeResponse;
import se.inera.privatlakarportal.web.controller.api.dto.GetRegistrationResponse;
import se.inera.privatlakarportal.web.controller.api.dto.SaveRegistrationRequest;
import se.inera.privatlakarportal.web.controller.api.dto.SaveRegistrationResponse;

/**
 * Created by pebe on 2015-06-25.
 */
@RestController
@RequestMapping("/api/registration")
public class RegisterController {

    @Autowired
    private RegisterService registerService;

    @Autowired
    private PostnummerService postnummerService;

    @RequestMapping(value = "")
    public GetRegistrationResponse getRegistration() {
        RegistrationWithHospInformation registrationWithHospInformation = registerService.getRegistration();
        return new GetRegistrationResponse(registrationWithHospInformation.getRegistration(), registrationWithHospInformation.getHospInformation());
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST, consumes = "application/json")
    public CreateRegistrationResponse createRegistration(@RequestBody CreateRegistrationRequest request) {
        RegistrationStatus status = registerService.createRegistration(request.getRegistration(), request.getGodkantMedgivandeVersion());
        return new CreateRegistrationResponse(status);
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST, consumes = "application/json")
    public SaveRegistrationResponse createRegistration(@RequestBody SaveRegistrationRequest request) {
        SaveRegistrationResponseStatus status = registerService.saveRegistration(request.getRegistration());
        return new SaveRegistrationResponse(status);
    }

    @RequestMapping(value = "/hospInformation")
    public GetHospInformationResponse getHospInformation() {
        return new GetHospInformationResponse(registerService.getHospInformation());
    }

    @RequestMapping(value = "/omrade/{postnummer}")
    public GetOmradeResponse getOmrade(@PathVariable("postnummer") String postnummer) {
        return new GetOmradeResponse(postnummerService.getOmradeByPostnummer(postnummer));
    }
}
