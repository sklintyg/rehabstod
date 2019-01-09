/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.integration.sparrtjanst.client;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import se.inera.intyg.infra.sjukfall.dto.IntygData;
import se.inera.intyg.rehabstod.integration.sparrtjanst.util.SparrtjanstUtil;
import se.riv.informationsecurity.authorization.blocking.CheckBlocks.v4.rivtabp21.CheckBlocksResponderInterface;
import se.riv.informationsecurity.authorization.blocking.CheckBlocksResponder.v4.CheckBlocksType;

/**
 * Created by marced on 2018-10-16.
 */
@RunWith(MockitoJUnitRunner.class)
public class SparrtjanstClientServiceImplTest {

    private static final String VG_HSA_ID = "vg1";
    private static final String VE_HSA_ID = "ve1.1";
    private static final String USER_HSA_ID = "hsa123";
    private static final String PATIENT_ID = "191212121212";
    private static final String LOGICAL_ADDRESS = "123";
    private LocalDateTime date1 = LocalDateTime.now().minusMonths(2);
    private LocalDateTime date2 = LocalDateTime.now().minusDays(1);

    @Mock
    private CheckBlocksResponderInterface service;

    @InjectMocks
    private SparrtjanstClientServiceImpl testee;

    @Test
    public void getCheckBlocks() throws Exception {
        ReflectionTestUtils.setField(testee, "logicalAddress", LOGICAL_ADDRESS);
        ArgumentCaptor<CheckBlocksType> requestCapture = ArgumentCaptor.forClass(CheckBlocksType.class);

        List<IntygData> intygLista = new ArrayList<>();
        intygLista.add(createIntygData("vg1", "ve1", date1));
        intygLista.add(createIntygData("vg2", "ve2", date2));
        testee.getCheckBlocks(VG_HSA_ID, VE_HSA_ID, USER_HSA_ID, PATIENT_ID,
                intygLista);
        verify(service).checkBlocks(eq(LOGICAL_ADDRESS), requestCapture.capture());

        final CheckBlocksType args = requestCapture.getValue();
        assertEquals(SparrtjanstUtil.KODVERK_PERSONNUMMER, args.getPatientId().getRoot());
        assertEquals(PATIENT_ID, args.getPatientId().getExtension());
        assertEquals(VG_HSA_ID, args.getAccessingActor().getCareProviderId());
        assertEquals(VE_HSA_ID, args.getAccessingActor().getCareUnitId());
        assertEquals(USER_HSA_ID, args.getAccessingActor().getEmployeeId());
        assertEquals(2, args.getInformationEntities().size());

        assertEquals(0, args.getInformationEntities().get(0).getRowNumber());
        assertEquals("vg1", args.getInformationEntities().get(0).getInformationCareProviderId());
        assertEquals("ve1", args.getInformationEntities().get(0).getInformationCareUnitId());
        assertEquals(date1, args.getInformationEntities().get(0).getInformationStartDate());
        assertEquals(date1, args.getInformationEntities().get(0).getInformationEndDate());

        assertEquals(1, args.getInformationEntities().get(1).getRowNumber());
        assertEquals("vg2", args.getInformationEntities().get(1).getInformationCareProviderId());
        assertEquals("ve2", args.getInformationEntities().get(1).getInformationCareUnitId());
        assertEquals(date2, args.getInformationEntities().get(1).getInformationStartDate());
        assertEquals(date2, args.getInformationEntities().get(1).getInformationEndDate());
    }

    @Test(expected = IllegalArgumentException.class)
    public void getCheckBlocksThrowsExceptionForInvalidPatientId() throws Exception {
        ReflectionTestUtils.setField(testee, "logicalAddress", LOGICAL_ADDRESS);

        List<IntygData> intygLista = new ArrayList<>();
        testee.getCheckBlocks(VG_HSA_ID, VE_HSA_ID, USER_HSA_ID, "123211122",
                intygLista);

    }

    private IntygData createIntygData(String vg, String ve, LocalDateTime signDate) {
        IntygData intyg = new IntygData();
        intyg.setVardgivareId(vg);
        intyg.setVardenhetId(ve);
        intyg.setSigneringsTidpunkt(signDate);
        return intyg;
    }

}
