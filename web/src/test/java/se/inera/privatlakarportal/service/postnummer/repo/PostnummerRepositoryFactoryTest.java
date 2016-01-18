package se.inera.privatlakarportal.service.postnummer.repo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import se.inera.privatlakarportal.service.postnummer.model.Omrade;

/**
 * Created by pebe on 2015-08-12.
 */
@RunWith(MockitoJUnitRunner.class)
public class PostnummerRepositoryFactoryTest {

    private PostnummerRepositoryFactory factory = new PostnummerRepositoryFactory();

    private static final String LINE_1 = "13100;NACKA;01;STOCKHOLM;0182;NACKA;01";
    private static final String LINE_1_POSTNUMMER = "13100";
    private static final String LINE_1_POSTORT = "NACKA";
    private static final String LINE_1_LAN = "STOCKHOLM";
    private static final String LINE_1_KOMMUN = "NACKA";

    @Test
    public void testCreateOmradeFromString() {

        Omrade res = factory.createOmradeFromString(LINE_1);

        assertNotNull(res);
        assertEquals(LINE_1_POSTNUMMER, res.getPostnummer());
        assertEquals(LINE_1_POSTORT, res.getPostort());
        assertEquals(LINE_1_KOMMUN, res.getKommun());
        assertEquals(LINE_1_LAN, res.getLan());
    }

    @Test
    public void testCreateOmradeWithSetters() {
        Omrade control = factory.createOmradeFromString(LINE_1);
        Omrade test = new Omrade(null, null, null, null);
        test.setKommun(LINE_1_KOMMUN);
        test.setLan(LINE_1_LAN);
        test.setPostnummer(LINE_1_POSTNUMMER);
        test.setPostort(LINE_1_POSTORT);
        assertTrue(control.hashCode() == test.hashCode());
    }

}
