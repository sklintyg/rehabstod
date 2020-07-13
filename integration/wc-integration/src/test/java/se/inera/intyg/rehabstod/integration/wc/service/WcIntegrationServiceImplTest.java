/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.integration.wc.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import se.inera.clinicalprocess.healthcond.certificate.types.v3.IntygId;
import se.inera.clinicalprocess.healthcond.certificate.v3.ResultCodeType;
import se.inera.intyg.clinicalprocess.healthcond.certificate.getcertificateadditions.v1.AdditionType;
import se.inera.intyg.clinicalprocess.healthcond.certificate.getcertificateadditions.v1.GetCertificateAdditionsResponseType;
import se.inera.intyg.clinicalprocess.healthcond.certificate.getcertificateadditions.v1.IntygAdditionsType;
import se.inera.intyg.clinicalprocess.healthcond.certificate.getcertificateadditions.v1.StatusType;
import se.inera.intyg.rehabstod.integration.wc.client.WcClientService;
import se.inera.intyg.rehabstod.integration.wc.exception.WcIntegrationException;
import se.inera.intyg.rehabstod.integration.wc.service.dto.UnansweredQAs;

/**
 * Created by marced on 2019-05-17.
 */
@RunWith(MockitoJUnitRunner.class)
public class WcIntegrationServiceImplTest {

    private static final String INTYGS_ID_1 = "intyg1";
    private static final String INTYGS_ID_2 = "intyg2";
    private static final String INTYGS_ID_3 = "intyg3";

    @Mock
    private WcClientService wcClientService;

    @InjectMocks
    private WcIntegrationServiceImpl testee;

    @Before
    public void setUp() throws Exception {
        ReflectionTestUtils.setField(testee, "maxDaysOld", 10);
    }

    @Test
    public void testGetCertificateAdditionsSuccessNoInput() {
        assertEquals(0, testee.getCertificateAdditionsForIntyg(null).size());
        assertEquals(0, testee.getCertificateAdditionsForIntyg(new ArrayList<>()).size());
    }

    @Test
    public void testGetCertificateAdditionsSuccess() {
        GetCertificateAdditionsResponseType result = new GetCertificateAdditionsResponseType();
        result.setResult(ResultCodeType.OK);

        // 2 that should match, and 1 besvarad and 1 obesvarad but to old
        result.getAdditions()
            .add(createIntygAdditionType(INTYGS_ID_1, Arrays.asList(
                createAdditionType(StatusType.OBESVARAD, LocalDateTime.now()),
                createAdditionType(StatusType.OBESVARAD, LocalDateTime.now().minusDays(2)),
                createAdditionType(StatusType.OBESVARAD, LocalDateTime.now().minusMonths(999)),
                createAdditionType(StatusType.BESVARAD, LocalDateTime.now()))));
        // obesvarad but to old
        result.getAdditions()
            .add(createIntygAdditionType(INTYGS_ID_2,
                Arrays.asList(createAdditionType(StatusType.OBESVARAD, LocalDateTime.now().minusMonths(999)))));
        // besvarad
        result.getAdditions()
            .add(createIntygAdditionType(INTYGS_ID_3,
                Arrays.asList(createAdditionType(StatusType.BESVARAD, LocalDateTime.now()))));

        when(wcClientService.getCertificateAdditions(anyList())).thenReturn(result);

        List<String> intygsIds = Arrays.asList(INTYGS_ID_1, INTYGS_ID_2, INTYGS_ID_3);
        final Map<String, UnansweredQAs> resultMap = testee
            .getCertificateAdditionsForIntyg(Arrays.asList(INTYGS_ID_1, INTYGS_ID_2, INTYGS_ID_3));

        verify(wcClientService).getCertificateAdditions(eq(intygsIds));

        assertEquals(2, resultMap.get(INTYGS_ID_1).getComplement());
        assertEquals(0, resultMap.get(INTYGS_ID_2).getComplement());
        assertEquals(0, resultMap.get(INTYGS_ID_3).getComplement());

    }

    @Test(expected = WcIntegrationException.class)
    public void shouldThrowWcIntegrationExceptionWhenErrorResult() {
        GetCertificateAdditionsResponseType result = new GetCertificateAdditionsResponseType();
        result.setResult(ResultCodeType.ERROR);
        when(wcClientService.getCertificateAdditions(anyList())).thenReturn(result);

        testee.getCertificateAdditionsForIntyg(Arrays.asList(INTYGS_ID_1));
    }

    @Test(expected = WcIntegrationException.class)
    public void shouldThrowWcIntegrationExceptionWhenAnyException() {

        when(wcClientService.getCertificateAdditions(anyList())).thenThrow(new RuntimeException("some error"));

        testee.getCertificateAdditionsForIntyg(Arrays.asList(INTYGS_ID_1));
    }

    private AdditionType createAdditionType(StatusType statusType, LocalDateTime skapad) {
        AdditionType addition = new AdditionType();
        addition.setStatus(statusType);
        addition.setSkapad(skapad);
        addition.getAny().add("KOMPL");
        return addition;
    }

    private IntygAdditionsType createIntygAdditionType(String intygsId, List<AdditionType> additions) {
        IntygAdditionsType intygAdditionsType = new IntygAdditionsType();
        IntygId id = new IntygId();
        id.setExtension(intygsId);
        intygAdditionsType.setIntygsId(id);
        intygAdditionsType.getAddition().addAll(additions);
        return intygAdditionsType;
    }
}
