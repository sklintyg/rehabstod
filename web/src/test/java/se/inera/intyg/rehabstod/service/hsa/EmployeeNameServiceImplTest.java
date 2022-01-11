/*
 * Copyright (C) 2022 Inera AB (http://www.inera.se)
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