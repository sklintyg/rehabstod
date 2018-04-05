/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.integration.it.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import se.inera.intyg.clinicalprocess.healthcond.rehabilitation.listactivesickleavesforcareunit.v1.ListActiveSickLeavesForCareUnitResponseType;
import se.inera.intyg.clinicalprocess.healthcond.rehabilitation.listactivesickleavesforcareunit.v1.ResultCodeEnum;
import se.inera.intyg.rehabstod.integration.it.client.IntygstjanstClientService;
import se.inera.intyg.rehabstod.integration.it.exception.IntygstjanstIntegrationException;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.IntygsData;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.IntygsLista;

import java.util.List;

/**
 * Simple test.
 *
 * Created by eriklupander on 2016-02-01.
 */
@RunWith(MockitoJUnitRunner.class)
public class IntygstjanstIntegrationServiceTest {

    private static final String PATIENT_ID = "191212121212";
    private static final String HSA_ID = "123";
    private static final String HSA_ID_UNKNOWN = "456";

    @Mock
    private IntygstjanstClientService intygstjanstClientService;

    @InjectMocks
    private IntygstjanstIntegrationServiceImpl testee;

    @Test
    public void testGetIntygsDataForCareUnit() throws Exception {
        when(intygstjanstClientService.getSjukfallForUnit(HSA_ID)).thenReturn(buildResponse());
        List<IntygsData> intygsDataForCareUnit = testee.getIntygsDataForCareUnit(HSA_ID);
        assertEquals(1, intygsDataForCareUnit.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetIntygsDataForCareUnitEmptyArgumentThrowsException() throws Exception {
        testee.getIntygsDataForCareUnit("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetIntygsDataForCareUnitNullArgumentThrowsException() throws Exception {
        testee.getIntygsDataForCareUnit(null);
    }


    @Test
    public void testGetIntygsDataForPatient() throws Exception {
        when(intygstjanstClientService.getSjukfallForPatient(HSA_ID, PATIENT_ID)).thenReturn(buildResponse());
        List<IntygsData> intygsDataForPatient = testee.getIntygsDataForPatient(HSA_ID, PATIENT_ID);
        assertEquals(1, intygsDataForPatient.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetIntygsDataForPatientNullParametersThrowsException() throws Exception {
        testee.getIntygsDataForPatient(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetIntygsDataForPatientEmptyParametersThrowsException() throws Exception {
        testee.getIntygsDataForPatient("", "");
    }

    @Test(expected = IntygstjanstIntegrationException.class)
    public void testGetIntygsDataForCareUnitThrowsExceptionOnError() throws Exception {
        when(intygstjanstClientService.getSjukfallForUnit(HSA_ID_UNKNOWN)).thenReturn(buildErrorResponse());
        testee.getIntygsDataForCareUnit(HSA_ID_UNKNOWN);
    }

    @Test(expected = IntygstjanstIntegrationException.class)
    public void testGetIntygsDataForCareUnitThrowsExceptionWhenErrorCodeMissing() throws Exception {
        when(intygstjanstClientService.getSjukfallForUnit(HSA_ID_UNKNOWN)).thenReturn(new ListActiveSickLeavesForCareUnitResponseType());
        testee.getIntygsDataForCareUnit(HSA_ID_UNKNOWN);
    }

    private ListActiveSickLeavesForCareUnitResponseType buildResponse() {
        ListActiveSickLeavesForCareUnitResponseType response = new ListActiveSickLeavesForCareUnitResponseType();
        response.setResultCode(ResultCodeEnum.OK);

        IntygsLista intygsLista = new IntygsLista();
        intygsLista.getIntygsData().add(new IntygsData());

        response.setIntygsLista(intygsLista);
        return response;
    }

    private ListActiveSickLeavesForCareUnitResponseType buildErrorResponse() {
        ListActiveSickLeavesForCareUnitResponseType response = new ListActiveSickLeavesForCareUnitResponseType();
        response.setResultCode(ResultCodeEnum.ERROR);
        return response;
    }
}
