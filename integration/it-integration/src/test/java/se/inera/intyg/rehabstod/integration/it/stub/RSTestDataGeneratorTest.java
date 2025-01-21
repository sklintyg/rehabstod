/*
 * Copyright (C) 2025 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.integration.it.stub;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import se.inera.intyg.infra.integration.pu.stub.StubResidentStore;
import se.inera.intyg.rehabstod.integration.it.stub.RSTestDataGeneratorImpl.StubData;
import se.inera.intyg.rehabstod.integration.wc.stub.WcStubStore;

/**
 * Created by eriklupander on 2016-01-31.
 */
@RunWith(MockitoJUnitRunner.class)
public class RSTestDataGeneratorTest {

    @Mock
    private WcStubStore wcStubStore;

    @Mock
    private StubResidentStore residentStore;

    @Mock
    private PersonnummerLoader personnummerLoader;

    @InjectMocks
    private RSTestDataGeneratorImpl testee;

    @Before
    public void init() {
        testee.init();
    }

    @Test
    public void testGenerateIntygsData() throws Exception {
        when(personnummerLoader.readTestPersonnummer()).thenReturn(buildPersonnummerList());

        final int numberOfPatients = 10;
        final int intygPerPatient = 4;
        StubData stubData = testee.generateIntygsData(numberOfPatients, intygPerPatient);
        var intygsData = stubData.getIntygsData();
        var diagnosedCertificates = stubData.getDiagnosedCertificates();
        var sickLeaveCertificates = stubData.getSickLeaveCertificates();

        final int intygPerPatientOtherCareUnit = intygPerPatient * 2; // Vi skapar upp intyg på andra vårdgivare för sjf
        final int intygTolvan = intygPerPatient + intygPerPatientOtherCareUnit + 6; // Tolvan är gammal och ges då fyra extra intyg
        final int intygLillTolvan = intygPerPatient + 9; //sjf användare
        int expectedAntalIntyg = numberOfPatients * (intygPerPatient + intygPerPatientOtherCareUnit) + intygTolvan + intygLillTolvan;

        assertEquals(expectedAntalIntyg, intygsData.size());
        assertEquals("19791110-9291", intygsData.get(0).getPatient().getPersonId().getExtension());
        assertEquals("M16.0", intygsData.get(0).getDiagnoskod());
        assertNotNull(intygsData.get(0).getArbetsformaga().getFormaga().get(0).getStartdatum());
        assertNotNull(intygsData.get(0).getArbetsformaga().getFormaga().get(0).getSlutdatum());
        verify(residentStore, times(numberOfPatients + 2)).addResident(any());

        assertNotNull(diagnosedCertificates.get(0).getPatientFullName());
        assertNotNull(diagnosedCertificates.get(0).getDiagnoseCode());
        assertNotNull(sickLeaveCertificates.get(0).getWorkCapacityList().get(0));
        assertNotNull(sickLeaveCertificates.get(0).getOccupation());
    }

    private List<String> buildPersonnummerList() {
        return Arrays.asList(
            "19791110-9291",
            "19791123-9262",
            "19791212-9280",
            "19791230-9296",
            "19800113-9297",
            "19800124-9286",
            "19800207-9294",
            "19800228-9224",
            "19800311-9255",
            "19800321-9295");
    }
}
