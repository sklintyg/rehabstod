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
package se.inera.intyg.rehabstod.integration.srs.service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import se.inera.intyg.clinicalprocess.healthcond.srs.getriskpredictionforcertificate.v1.RiskPrediktion;
import se.inera.intyg.clinicalprocess.healthcond.srs.getriskpredictionforcertificate.v1.Risksignal;
import se.inera.intyg.rehabstod.integration.srs.client.SRSClientService;
import se.inera.intyg.rehabstod.integration.srs.model.RiskSignal;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * Created by eriklupander on 2017-11-01.
 */
@RunWith(MockitoJUnitRunner.class)
public class SRSIntegrationServiceImplTest {

    @Mock
    private SRSClientService srsClientService;

    @InjectMocks
    private SRSIntegrationServiceImpl testee;

    @Test
    public void testHappyPath() {
        String intygsId = UUID.randomUUID().toString();

        when(srsClientService.getRiskPrediktionForCertificate(anyListOf(String.class))).thenReturn(buildResult(intygsId));
        List<RiskSignal> result = testee.getRiskPreditionerForIntygsId(Arrays.asList(intygsId));
        assertEquals(1, result.size());
        assertEquals(intygsId, result.get(0).getIntygsId());
        assertEquals(1, result.get(0).getRiskKategori());
        assertEquals("Beskrivning", result.get(0).getRiskDescription());
    }

    @Test
    public void testEmptyListReturnedWhenNullIdsSupplied() {
        List<RiskSignal> result = testee.getRiskPreditionerForIntygsId(null);
        assertEquals(0, result.size());
        verifyZeroInteractions(srsClientService);
    }

    private List<RiskPrediktion> buildResult(String intygsId) {
        List<RiskPrediktion> result = new ArrayList<>();
        RiskPrediktion rp = new RiskPrediktion();
        rp.setIntygsId(intygsId);
        Risksignal signal = new Risksignal();
        signal.setRiskkategori(1);
        signal.setBeskrivning("Beskrivning");
        rp.setRisksignal(signal);
        result.add(rp);
        return result;
    }
}
