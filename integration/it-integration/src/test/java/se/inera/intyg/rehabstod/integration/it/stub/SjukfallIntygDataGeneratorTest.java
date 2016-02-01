package se.inera.intyg.rehabstod.integration.it.stub;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.IntygsData;

import java.util.Arrays;
import java.util.List;

/**
 * Created by eriklupander on 2016-01-31.
 */
@RunWith(MockitoJUnitRunner.class)
public class SjukfallIntygDataGeneratorTest {

    @Mock
    private PersonnummerLoader personnummerLoader;

    @InjectMocks
    private SjukfallIntygDataGeneratorImpl testee;

    @Before
    public void init() {
        testee.init();
    }

    @Test
    public void testGenerateIntygsData() throws Exception {
        when(personnummerLoader.readTestPersonnummer()).thenReturn(buildPersonnummerList());
        List<IntygsData> intygsData = testee.generateIntygsData(10, 4);
        assertEquals(40, intygsData.size());
        assertEquals("19791110-9291",intygsData.get(0).getPatient().getPersonId().getExtension());
        assertEquals("M16",intygsData.get(0).getDiagnos().getKod());
        assertNotNull("M16",intygsData.get(0).getArbetsformaga().getFormaga().get(0).getStartdatum());
        assertNotNull("M16",intygsData.get(0).getArbetsformaga().getFormaga().get(0).getSlutdatum());
    }

    private List<String> buildPersonnummerList() {
        return Arrays.asList("19791110-9291",
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