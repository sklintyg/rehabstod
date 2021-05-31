package se.inera.intyg.rehabstod.service.hsa;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

import java.util.Collections;
import javax.xml.ws.WebServiceException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import se.inera.intyg.infra.integration.hsatk.model.PersonInformation;
import se.inera.intyg.infra.integration.hsatk.services.legacy.HsaEmployeeService;

@RunWith(MockitoJUnitRunner.class)
public class EmployeeNameServiceImplTest {

    @Mock
    private HsaEmployeeService hsaEmployeeService;

    @InjectMocks
    private EmployeeNameServiceImpl employeeNameService;

    @Test
    public void shallReturnNameIfEmployeeExists() {
        final var personInformation = new PersonInformation();
        personInformation.setGivenName("givenName");
        personInformation.setMiddleAndSurName("middleAnd surName");

        doReturn(Collections.singletonList(personInformation)).when(hsaEmployeeService).getEmployee(any(), any(), any());

        final var actualName = employeeNameService.getEmployeeHsaName("employeeId");

        assertEquals("givenName middleAnd surName", actualName);
    }

    @Test
    public void shallReturnHsaIdAsNameIfEmployeeEmpty() {
        doReturn(Collections.emptyList()).when(hsaEmployeeService).getEmployee(any(), any(), any());

        final var actualName = employeeNameService.getEmployeeHsaName("employeeId");

        assertEquals("employeeId", actualName);
    }

    @Test
    public void shallReturnHsaIdAsNameIfExceptionIsThrown() {
        doThrow(new WebServiceException("Something went wrong")).when(hsaEmployeeService).getEmployee(any(), any(), any());

        final var actualName = employeeNameService.getEmployeeHsaName("employeeId");

        assertEquals("employeeId", actualName);
    }
}