package se.inera.privatlakarportal.web.controller.api;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import se.inera.privatlakarportal.service.RegisterService;
import se.inera.privatlakarportal.web.controller.api.dto.CreateRegistrationRequest;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class RegisterControllerTest {
    @Mock
    private RegisterService registerService;

    @InjectMocks
    private RegisterController registerController = new RegisterController();

    @Test
    public void testCreateRegistration() {
        CreateRegistrationRequest request = new CreateRegistrationRequest();
        request.setGodkantMedgivandeVersion(1L);
        registerController.createRegistration(request);

        verify(registerService).createRegistration(request.getRegistration(), 1L);
    }
}
