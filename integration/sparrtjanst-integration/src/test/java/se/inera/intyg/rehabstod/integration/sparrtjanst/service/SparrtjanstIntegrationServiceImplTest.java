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
package se.inera.intyg.rehabstod.integration.sparrtjanst.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import se.inera.intyg.infra.sjukfall.dto.IntygData;
import se.inera.intyg.rehabstod.common.model.IntygAccessControlMetaData;
import se.inera.intyg.rehabstod.integration.sparrtjanst.client.SparrtjanstClientService;
import se.inera.intyg.rehabstod.integration.sparrtjanst.exception.SparrtjanstIntegrationException;
import se.riv.informationsecurity.authorization.blocking.CheckBlocksResponder.v4.CheckBlocksResponseType;
import se.riv.informationsecurity.authorization.blocking.v4.CheckBlocksResultType;
import se.riv.informationsecurity.authorization.blocking.v4.CheckResultType;
import se.riv.informationsecurity.authorization.blocking.v4.CheckStatusType;
import se.riv.informationsecurity.authorization.blocking.v4.ResultCodeType;
import se.riv.informationsecurity.authorization.blocking.v4.ResultType;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;

/**
 * Created by marced on 2018-10-12.
 */
@RunWith(MockitoJUnitRunner.class)
public class SparrtjanstIntegrationServiceImplTest {

    private static final String VG_HSA_ID = "vg1";
    private static final String VE_HSA_ID = "ve1.1";
    private static final String USER_HSA_ID = "hsa123";
    private static final String PATIENT_ID = "191212121212";
    private static final String INTYGS_ID_1 = "intyg1";
    private static final String INTYGS_ID_2 = "intyg2";
    private Map<String, IntygAccessControlMetaData> aclList;
    private List<IntygData> intygList;

    @Mock
    private SparrtjanstClientService sparrtjanstClientService;

    @InjectMocks
    private SparrtjanstIntegrationServiceImpl testee;

    @Before
    public void setup() {
        aclList = new HashMap<>();
        intygList = new ArrayList<>();

        IntygData intygData1 = new IntygData();
        intygData1.setIntygId(INTYGS_ID_1);
        IntygData intygData2 = new IntygData();
        intygData2.setIntygId(INTYGS_ID_2);

        aclList.put(INTYGS_ID_1, new IntygAccessControlMetaData(intygData1, false, false));
        aclList.put(INTYGS_ID_2, new IntygAccessControlMetaData(intygData2, false, false));

        intygList.add(intygData1);
        intygList.add(intygData2);
    }

    @Test
    public void decorateDoesNotHaveSparr() throws Exception {

        CheckBlocksResponseType response = createResponse(ResultCodeType.OK, CheckStatusType.OK);
        when(sparrtjanstClientService.getCheckBlocks(VG_HSA_ID, VE_HSA_ID, USER_HSA_ID, PATIENT_ID,
                intygList)).thenReturn(response);

        testee.decorateWithBlockStatus(VG_HSA_ID, VE_HSA_ID, USER_HSA_ID, PATIENT_ID, aclList, intygList);

        assertFalse(aclList.get(INTYGS_ID_1).isSparr());
        assertFalse(aclList.get(INTYGS_ID_2).isSparr());
    }

    @Test
    public void decorateHasSparr() throws Exception {

        CheckBlocksResponseType response = createResponse(ResultCodeType.OK, CheckStatusType.BLOCKED);
        when(sparrtjanstClientService.getCheckBlocks(VG_HSA_ID, VE_HSA_ID, USER_HSA_ID, PATIENT_ID,
                intygList)).thenReturn(response);

        testee.decorateWithBlockStatus(VG_HSA_ID, VE_HSA_ID, USER_HSA_ID, PATIENT_ID, aclList, intygList);

        assertTrue(aclList.get(INTYGS_ID_1).isSparr());
        assertTrue(aclList.get(INTYGS_ID_2).isSparr());
    }

    @Test(expected = SparrtjanstIntegrationException.class)
    public void shouldThrowSparrtjanstIntegrationExceptionOnErrorResponse() throws Exception {

        CheckBlocksResponseType response = createResponse(ResultCodeType.ERROR, CheckStatusType.BLOCKED);
        when(sparrtjanstClientService.getCheckBlocks(VG_HSA_ID, VE_HSA_ID, USER_HSA_ID, PATIENT_ID,
                intygList)).thenReturn(response);

        testee.decorateWithBlockStatus(VG_HSA_ID, VE_HSA_ID, USER_HSA_ID, PATIENT_ID, aclList, intygList);

    }

    @Test(expected = SparrtjanstIntegrationException.class)
    public void shouldThrowSparrtjanstIntegrationExceptionWhenResponseRowsMismatch() throws Exception {

        CheckBlocksResponseType response = createResponse(ResultCodeType.OK, CheckStatusType.BLOCKED);
        response.getCheckBlocksResult().getCheckResults().remove(0);
        when(sparrtjanstClientService.getCheckBlocks(VG_HSA_ID, VE_HSA_ID, USER_HSA_ID, PATIENT_ID,
                intygList)).thenReturn(response);

        testee.decorateWithBlockStatus(VG_HSA_ID, VE_HSA_ID, USER_HSA_ID, PATIENT_ID, aclList, intygList);

    }

    private CheckBlocksResponseType createResponse(ResultCodeType code, CheckStatusType rowResult) {
        CheckBlocksResponseType response = new CheckBlocksResponseType();
        CheckBlocksResultType resultType = new CheckBlocksResultType();
        ResultType result = new ResultType();
        result.setResultCode(code);
        result.setResultText("All is good");

        CheckResultType crt = new CheckResultType();
        crt.setRowNumber(0);
        crt.setStatus(rowResult);
        resultType.getCheckResults().add(crt);

        CheckResultType crt1 = new CheckResultType();
        crt1.setRowNumber(1);
        crt1.setStatus(rowResult);
        resultType.getCheckResults().add(crt1);

        resultType.setResult(result);
        response.setCheckBlocksResult(resultType);
        return response;

    }

}
