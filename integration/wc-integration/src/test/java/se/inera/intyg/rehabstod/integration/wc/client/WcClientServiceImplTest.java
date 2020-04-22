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
package se.inera.intyg.rehabstod.integration.wc.client;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import se.inera.intyg.clinicalprocess.healthcond.certificate.getcertificateadditions.v1.GetCertificateAdditionsResponderInterface;
import se.inera.intyg.clinicalprocess.healthcond.certificate.getcertificateadditions.v1.GetCertificateAdditionsType;

/**
 * Created by marced on 2019-05-17.
 */
@RunWith(MockitoJUnitRunner.class)
public class WcClientServiceImplTest {

    private static final String INTYG_1 = "intyg-1";
    private static final String INTYG_2 = "intyg-2";
    private static final String LOGICAL_ADDRESS = "123";

    @Mock
    private GetCertificateAdditionsResponderInterface service;

    @InjectMocks
    private WcClientServiceImpl testee;

    @Test
    public void getCertificateAdditions() {
        ReflectionTestUtils.setField(testee, "logicalAddress", LOGICAL_ADDRESS);
        ArgumentCaptor<GetCertificateAdditionsType> requestCapture = ArgumentCaptor.forClass(GetCertificateAdditionsType.class);

        testee.getCertificateAdditions(Arrays.asList(INTYG_1, INTYG_2));

        verify(service).getCertificateAdditions(eq(LOGICAL_ADDRESS), requestCapture.capture());

        final GetCertificateAdditionsType args = requestCapture.getValue();
        assertEquals(2, args.getIntygsId().size());
        assertEquals(INTYG_1, args.getIntygsId().get(0).getExtension());
        assertEquals(INTYG_2, args.getIntygsId().get(1).getExtension());
    }

}
