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

package se.inera.intyg.rehabstod.integration.samtyckestjanst.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import se.inera.intyg.infra.sjukfall.dto.IntygData;
import se.inera.intyg.rehabstod.common.model.IntygAccessControlMetaData;
import se.inera.intyg.rehabstod.integration.samtyckestjanst.client.SamtyckestjanstClientService;
import se.riv.informationsecurity.authorization.consent.CheckConsentResponder.v2.CheckConsentResponseType;
import se.riv.informationsecurity.authorization.consent.v2.CheckResultType;
import se.riv.informationsecurity.authorization.consent.v2.ResultCodeType;
import se.riv.informationsecurity.authorization.consent.v2.ResultType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;

/**
 * @author Magnus Ekstrand on 2018-10-11.
 */
@RunWith(MockitoJUnitRunner.class)
public class SamtyckestjanstIntegrationServiceImplTest {

    private static final String USER_HSA_ID = UUID.randomUUID().toString();
    private static final String PATIENT_ID = "20121212-1212";
    private static final String VG_HSAID_2 = "vgHsaId-2";
    private static final String VE_HSAID_2 = "veHsaId-2";

    @Mock
    private SamtyckestjanstClientService samtyckestjanstClientService;

    @InjectMocks
    private SamtyckestjanstIntegrationServiceImpl testee = new SamtyckestjanstIntegrationServiceImpl();


    @Before
    public void setUp() {
        doAnswer(new Answer<CheckConsentResponseType>(){
            @Override
            public CheckConsentResponseType answer(InvocationOnMock invocation){
                if (invocation.getArgumentAt(0, String.class).equals(VG_HSAID_2)
                        && invocation.getArgumentAt(1, String.class).equals(VE_HSAID_2)) {
                    return createCheckConsentResponseType(true);
                } else {
                    return createCheckConsentResponseType(false);
                }
            }}).when(samtyckestjanstClientService).checkConsent(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    public void testCheckForConsent() {
        Map<String, IntygAccessControlMetaData> metaDataMap = createMetaDataMap();

        testee.checkForConsent(PATIENT_ID, USER_HSA_ID, metaDataMap);

        for (IntygAccessControlMetaData metaData : metaDataMap.values()) {
            IntygData data = metaData.getIntygData();
            if (data.getVardgivareId().equals(VG_HSAID_2) && data.getVardenhetId().equals(VE_HSAID_2)) {
                assertEquals(true, metaData.isHarSamtycke());
            } else {
                assertEquals(false, metaData.isHarSamtycke());
            }
        }
    }

    @Test
    public void testDistinctList() {
        List<IntygAccessControlMetaData> list = testee.getDistinctMetaDataList(createMetaDataMap());
        assertEquals(5, list.size());
    }

    @Test
    public void testUpdateConsent() {
        Map<String, IntygAccessControlMetaData> map = createMetaDataMap();
                testee.updateConsentStatus("vgHsaId-2", "veHsaId-2", true, map);

        for (IntygAccessControlMetaData metaData : map.values()) {
            IntygData data = metaData.getIntygData();
            if (data.getVardgivareId().equals("vgHsaId-2") && data.getVardenhetId().equals("veHsaId-2")) {
                assertEquals(true, metaData.isHarSamtycke());
            } else {
                assertEquals(false, metaData.isHarSamtycke());
            }

        }
    }

    private Map<String,IntygAccessControlMetaData> createMetaDataMap() {
        Map<String, IntygAccessControlMetaData> map = new HashMap<>();
        map.put("A", createMetaData("A","vgHsaId-1", "veHsaId-1"));
        map.put("B", createMetaData("B","vgHsaId-1", "veHsaId-1"));
        map.put("C", createMetaData("C","vgHsaId-1", "veHsaId-2"));
        map.put("D", createMetaData("D","vgHsaId-1", "veHsaId-3"));
        map.put("E", createMetaData("E","vgHsaId-2", "veHsaId-1"));
        map.put("F", createMetaData("F","vgHsaId-2", "veHsaId-2"));
        map.put("G", createMetaData("G","vgHsaId-2", "veHsaId-2"));
        map.put("H", createMetaData("H","vgHsaId-2", "veHsaId-2"));
        return map;
    }

    private IntygAccessControlMetaData createMetaData(String intygId, String vgHsaId, String veHsaId) {
        return new IntygAccessControlMetaData(createIntygData(intygId, vgHsaId, veHsaId), false);
    }

    private IntygData createIntygData(String intygId, String vgHsaId, String veHsaId) {
        IntygData data = new IntygData();
        data.setIntygId(intygId);
        data.setVardgivareId(vgHsaId);
        data.setVardenhetId(veHsaId);
        return data;
    }

    private CheckConsentResponseType createCheckConsentResponseType(boolean hasConsent) {
        ResultType resultType = new ResultType();
        resultType.setResultCode(ResultCodeType.OK);

        CheckResultType checkResultType = new CheckResultType();
        checkResultType.setHasConsent(hasConsent);
        checkResultType.setResult(resultType);

        CheckConsentResponseType response = new CheckConsentResponseType();
        response.setCheckResult(checkResultType);
        return response;
    }
}