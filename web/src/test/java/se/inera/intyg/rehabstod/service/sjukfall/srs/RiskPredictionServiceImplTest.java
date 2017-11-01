/*
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.service.sjukfall.srs;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import se.inera.intyg.rehabstod.integration.srs.model.RiskSignal;
import se.inera.intyg.rehabstod.integration.srs.service.SRSIntegrationService;
import se.inera.intyg.rehabstod.service.feature.RehabstodFeature;
import se.inera.intyg.rehabstod.service.feature.RehabstodFeatureServiceImpl;
import se.inera.intyg.rehabstod.web.model.PatientData;
import se.inera.intyg.rehabstod.web.model.SjukfallEnhet;
import se.inera.intyg.rehabstod.web.model.SjukfallPatient;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * Created by eriklupander on 2017-11-01.
 */
@RunWith(MockitoJUnitRunner.class)
public class RiskPredictionServiceImplTest {

    @Mock
    private RehabstodFeatureServiceImpl featureService;

    @Mock
    private SRSIntegrationService srsIntegrationService;

    @InjectMocks
    private RiskPredictionServiceImpl testee;

    @Test
    public void testNoInteractionWithSRSWhenFeatureNotActive() {
        when(featureService.isFeatureActive(RehabstodFeature.SRS)).thenReturn(false);
        testee.updateWithRiskPredictions(buildSjukfallEnhetList(UUID.randomUUID().toString()));
        verifyZeroInteractions(srsIntegrationService);
    }

    @Test
    public void testPatientDataNoInteractionWithSRSWhenFeatureNotActive() {
        when(featureService.isFeatureActive(RehabstodFeature.SRS)).thenReturn(false);
        testee.updateSjukfallPatientListWithRiskPredictions(buildSjukfallPatientList(UUID.randomUUID().toString()));
        verifyZeroInteractions(srsIntegrationService);
    }


    @Test
    public void testNoInteractionWithSRSWhenFeatureActiveButNoSjukfallSupplied() {
        when(featureService.isFeatureActive(RehabstodFeature.SRS)).thenReturn(true);
        testee.updateWithRiskPredictions(new ArrayList<>());
        verifyZeroInteractions(srsIntegrationService);
    }

    @Test
    public void testPatientDataNoInteractionWithSRSWhenFeatureActiveButNoSjukfallSupplied() {
        when(featureService.isFeatureActive(RehabstodFeature.SRS)).thenReturn(true);
        testee.updateSjukfallPatientListWithRiskPredictions(new ArrayList<>());
        verifyZeroInteractions(srsIntegrationService);
    }

    @Test
    public void testInteractionWithSRSWhenFeatureActive() {
        String intygsId = UUID.randomUUID().toString();

        when(featureService.isFeatureActive(RehabstodFeature.SRS)).thenReturn(true);
        when(srsIntegrationService.getRiskPreditionerForIntygsId(anyList())).thenReturn(buildRiskSignalList(intygsId));
        List<SjukfallEnhet> sjukfallEnhetList = buildSjukfallEnhetList(intygsId);
        testee.updateWithRiskPredictions(sjukfallEnhetList);

        assertEquals(1, sjukfallEnhetList.size());
        SjukfallEnhet sjukfallEnhet = sjukfallEnhetList.get(0);

        assertEquals(intygsId, sjukfallEnhet.getRiskSignal().getIntygsId());
        assertEquals(2, sjukfallEnhet.getRiskSignal().getRiskKategori());
        assertEquals("beskrivning", sjukfallEnhet.getRiskSignal().getRiskDescription());

        verify(srsIntegrationService, times(1)).getRiskPreditionerForIntygsId(anyList());
    }


    @Test
    public void testPatientDataInteractionWithSRSWhenFeatureActive() {
        String intygsId = UUID.randomUUID().toString();

        when(featureService.isFeatureActive(RehabstodFeature.SRS)).thenReturn(true);
        when(srsIntegrationService.getRiskPreditionerForIntygsId(anyList())).thenReturn(buildRiskSignalList(intygsId));
        List<SjukfallPatient> sjukfallPatientList = buildSjukfallPatientList(intygsId);
        testee.updateSjukfallPatientListWithRiskPredictions(sjukfallPatientList);

        assertEquals(1, sjukfallPatientList.size());
        PatientData patientData = sjukfallPatientList.get(0).getIntyg().get(0);

        assertEquals(intygsId, patientData.getRiskSignal().getIntygsId());
        assertEquals(2, patientData.getRiskSignal().getRiskKategori());
        assertEquals("beskrivning", patientData.getRiskSignal().getRiskDescription());

        verify(srsIntegrationService, times(1)).getRiskPreditionerForIntygsId(anyList());
    }

    private List<RiskSignal> buildRiskSignalList(String intygsId) {
        List<RiskSignal> list = new ArrayList<>();
        list.add(new RiskSignal(intygsId, 2, "beskrivning"));
        return list;
    }

    // sjukfall enhet builder.
    private List<SjukfallEnhet> buildSjukfallEnhetList(String intygsId) {
        List<SjukfallEnhet> list = new ArrayList<>();
        SjukfallEnhet se = new SjukfallEnhet();
        se.setAktivIntygsId(intygsId);
        list.add(se);
        return list;
    }

    // Patient data builders.
    private List<SjukfallPatient> buildSjukfallPatientList(String intygsId) {
        List<SjukfallPatient> list = new ArrayList<>();
        SjukfallPatient sjukfallPatient = new SjukfallPatient();
        sjukfallPatient.setIntyg(buildIntyg(intygsId));
        list.add(sjukfallPatient);
        return list;
    }

    private List<PatientData> buildIntyg(String intygsId) {
        List<PatientData> intyg = new ArrayList<>();
        intyg.add(buildPatientData(intygsId));
        return intyg;
    }

    private PatientData buildPatientData(String intygsId) {
        PatientData pd = new PatientData();
        pd.setIntygsId(intygsId);
        return pd;
    }

}
