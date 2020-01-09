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
package se.inera.intyg.rehabstod.service.sjukfall.komplettering;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.AssertJUnit.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import se.inera.intyg.rehabstod.integration.wc.service.WcIntegrationService;
import se.inera.intyg.rehabstod.web.model.PatientData;
import se.inera.intyg.rehabstod.web.model.SjukfallEnhet;
import se.inera.intyg.rehabstod.web.model.SjukfallPatient;

@RunWith(MockitoJUnitRunner.class)
public class KompletteringInfoDecoratorImplTest {

    @Mock
    private WcIntegrationService wcIntegrationService;

    @InjectMocks
    private KompletteringInfoDecoratorImpl testee;

    @Test
    public void updateSjukfallEnhetKompletteringar() {
        Map<String, Integer> kompl = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            kompl.put(Integer.toString(i), i);
        }

        when(wcIntegrationService.getCertificateAdditionsForIntyg(any(List.class))).thenReturn(kompl);
        List<SjukfallEnhet> sjukfall = new ArrayList<>();

        final SjukfallEnhet sjukfall0 = createSjukfall("0");
        final SjukfallEnhet sjukfall1 = createSjukfall("1");
        final SjukfallEnhet sjukfall23 = createSjukfall("2", "3");
        final SjukfallEnhet sjukfall456 = createSjukfall("4", "5", "6");
        final SjukfallEnhet sjukfallNotPresent = createSjukfall("Zero");
        sjukfall.add(sjukfall0);
        sjukfall.add(sjukfall1);
        sjukfall.add(sjukfall23);
        sjukfall.add(sjukfall456);
        sjukfall.add(sjukfallNotPresent);

        testee.updateSjukfallEnhetKompletteringar(sjukfall);

        assertEquals(0, sjukfall0.getObesvaradeKompl());
        assertEquals(1, sjukfall1.getObesvaradeKompl());
        assertEquals(5, sjukfall23.getObesvaradeKompl());
        assertEquals(15, sjukfall456.getObesvaradeKompl());
        assertEquals(0, sjukfallNotPresent.getObesvaradeKompl());
    }

    @Test
    public void updateSjukfallPatientKompletteringar() {

        // Results returned from wc integration service..
        Map<String, Integer> kompl = new HashMap<>();
        kompl.put("0", 0);
        kompl.put("1", 1);
        kompl.put("2", 2);
        kompl.put("3", 3);

        ArgumentCaptor<List> integrationIdList = ArgumentCaptor.forClass(List.class);

        when(wcIntegrationService.getCertificateAdditionsForIntyg(any(List.class))).thenReturn(kompl);

        List<SjukfallPatient> sjukfall = new ArrayList<>();
        final SjukfallPatient sjukfall0 = createSjukfallPatient(createPatientData("0", false, false));
        final SjukfallPatient sjukfall1 = createSjukfallPatient(createPatientData("1", false, false));
        final SjukfallPatient sjukfall23 = createSjukfallPatient(createPatientData("2", false, false),
            createPatientData("3", false, false));
        // Should be 0 because not queried for (because other vg and/or ve rule)
        final SjukfallPatient sjukfall4 = createSjukfallPatient(createPatientData("4", true, false));
        final SjukfallPatient sjukfall5 = createSjukfallPatient(createPatientData("5", false, true));
        final SjukfallPatient sjukfall6 = createSjukfallPatient(createPatientData("6", true, true));

        // should be 0 because no matching results
        final SjukfallPatient sjukfallNotPresent = createSjukfallPatient(createPatientData("n/a", false, false));

        sjukfall.add(sjukfall0);
        sjukfall.add(sjukfall1);
        sjukfall.add(sjukfall23);
        sjukfall.add(sjukfall4);
        sjukfall.add(sjukfall5);
        sjukfall.add(sjukfall6);
        sjukfall.add(sjukfallNotPresent);

        testee.updateSjukfallPatientKompletteringar(sjukfall);

        verify(wcIntegrationService).getCertificateAdditionsForIntyg(integrationIdList.capture());
        assertEquals(Arrays.asList("0", "1", "2", "3", "n/a"), integrationIdList.getValue());

        assertEquals(0, sjukfall0.getIntyg().get(0).getObesvaradeKompl().intValue());
        assertEquals(1, sjukfall1.getIntyg().get(0).getObesvaradeKompl().intValue());
        assertEquals(2, sjukfall23.getIntyg().get(0).getObesvaradeKompl().intValue());
        assertEquals(3, sjukfall23.getIntyg().get(1).getObesvaradeKompl().intValue());
        assertEquals(null, sjukfall4.getIntyg().get(0).getObesvaradeKompl());
        assertEquals(null, sjukfall5.getIntyg().get(0).getObesvaradeKompl());
        assertEquals(null, sjukfall6.getIntyg().get(0).getObesvaradeKompl());
        assertEquals(null, sjukfallNotPresent.getIntyg().get(0).getObesvaradeKompl());
    }

    private PatientData createPatientData(String intygId, boolean otherVardgivare, boolean otherVardenhet) {
        PatientData patientData = new PatientData();
        patientData.setIntygsId(intygId);
        patientData.setOtherVardgivare(otherVardgivare);
        patientData.setOtherVardenhet(otherVardenhet);
        return patientData;
    }

    private SjukfallPatient createSjukfallPatient(PatientData... patientData) {
        SjukfallPatient sfp = new SjukfallPatient();
        sfp.setIntyg(Arrays.asList(patientData));
        return sfp;
    }

    private SjukfallEnhet createSjukfall(String... intygIds) {
        SjukfallEnhet sfe = new SjukfallEnhet();
        sfe.setIntygLista(Arrays.asList(intygIds));
        return sfe;
    }
}
