/*
 * Copyright (C) 2023 Inera AB (http://www.inera.se)
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

import static org.mockito.ArgumentMatchers.any;
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
import org.mockito.junit.MockitoJUnitRunner;
import se.inera.intyg.rehabstod.integration.wc.service.WcIntegrationService;
import se.inera.intyg.rehabstod.integration.wc.service.dto.UnansweredQAs;
import se.inera.intyg.rehabstod.web.model.AGCertificate;
import se.inera.intyg.rehabstod.web.model.LUCertificate;
import se.inera.intyg.rehabstod.web.model.PatientData;
import se.inera.intyg.rehabstod.web.model.SjukfallEnhet;
import se.inera.intyg.rehabstod.web.model.SjukfallPatient;

@RunWith(MockitoJUnitRunner.class)
public class UnansweredQAsInfoDecoratorImplTest {

    @Mock
    private WcIntegrationService wcIntegrationService;

    @InjectMocks
    private UnansweredQAsInfoDecoratorImpl testee;

    @Test
    public void updateSjukfallPatientKompletteringar() {

        // Results returned from wc integration service..
        Map<String, UnansweredQAs> kompl = new HashMap<>();
        kompl.put("0", new UnansweredQAs(0, 0));
        kompl.put("1", new UnansweredQAs(1, 1));
        kompl.put("2", new UnansweredQAs(2, 2));
        kompl.put("3", new UnansweredQAs(3, 3));

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

        testee.updateSjukfallPatientWithQAs(sjukfall);

        verify(wcIntegrationService).getCertificateAdditionsForIntyg(integrationIdList.capture());
        assertEquals(Arrays.asList("0", "1", "2", "3", "n/a"), integrationIdList.getValue());

        assertEquals(0, sjukfall0.getIntyg().get(0).getObesvaradeKompl().intValue());
        assertEquals(1, sjukfall1.getIntyg().get(0).getObesvaradeKompl().intValue());
        assertEquals(2, sjukfall23.getIntyg().get(0).getObesvaradeKompl().intValue());
        assertEquals(3, sjukfall23.getIntyg().get(1).getObesvaradeKompl().intValue());
        assertEquals(0, sjukfall4.getIntyg().get(0).getObesvaradeKompl().intValue());
        assertEquals(0, sjukfall5.getIntyg().get(0).getObesvaradeKompl().intValue());
        assertEquals(0, sjukfall6.getIntyg().get(0).getObesvaradeKompl().intValue());
        assertEquals(0, sjukfallNotPresent.getIntyg().get(0).getObesvaradeKompl().intValue());
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

    @Test
    public void updateAGCertificatesWithKompletteringar() {
        Map<String, UnansweredQAs> qas = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            qas.put(Integer.toString(i), new UnansweredQAs(i, i));
        }

        when(wcIntegrationService.getCertificateAdditionsForIntyg(any(List.class))).thenReturn(qas);
        List<AGCertificate> agCertificateList = new ArrayList<>();

        final AGCertificate certificate = createAGCertificate("0");
        final AGCertificate certificate1 = createAGCertificate("1");
        final AGCertificate certificate2 = createAGCertificate("2");
        final AGCertificate certificate3 = createAGCertificate("3");
        final AGCertificate certificateNotPresent = createAGCertificate("Zero");
        agCertificateList.add(certificate);
        agCertificateList.add(certificate1);
        agCertificateList.add(certificate2);
        agCertificateList.add(certificate3);
        agCertificateList.add(certificateNotPresent);

        testee.updateAGCertificatesWithQAs(agCertificateList);

        assertEquals(0, certificate.getUnAnsweredComplement());
        assertEquals(1, certificate1.getUnAnsweredComplement());
        assertEquals(2, certificate2.getUnAnsweredComplement());
        assertEquals(3, certificate3.getUnAnsweredComplement());
        assertEquals(0, certificateNotPresent.getUnAnsweredComplement());
    }

    private LUCertificate createLUCertificate(String id) {
        return LUCertificate.builder().certificateId(id).build();
    }

    private AGCertificate createAGCertificate(String id) {
        return AGCertificate.builder().certificateId(id).build();
    }
}
