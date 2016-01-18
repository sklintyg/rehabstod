package se.inera.privatlakarportal.common.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import se.inera.privatlakarportal.persistence.model.LegitimeradYrkesgrupp;
import se.inera.privatlakarportal.persistence.model.Privatlakare;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by pebe on 2015-09-07.
 */
@RunWith(MockitoJUnitRunner.class)
public class PrivatlakareUtilsTest {

    @Test
    public void testLakare() {
        Privatlakare privatlakare = new Privatlakare();
        Set<LegitimeradYrkesgrupp> legitimeradYrkesgrupper = new HashSet<LegitimeradYrkesgrupp>();
        legitimeradYrkesgrupper.add(new LegitimeradYrkesgrupp(privatlakare, "Extra", "E"));
        legitimeradYrkesgrupper.add(new LegitimeradYrkesgrupp(privatlakare, "Läkare", "LK"));
        legitimeradYrkesgrupper.add(new LegitimeradYrkesgrupp(privatlakare, "Mer", "M"));
        privatlakare.setLegitimeradeYrkesgrupper(legitimeradYrkesgrupper);

        assertTrue(PrivatlakareUtils.hasLakareLegitimation(privatlakare));
    }

    @Test
    public void testEjLakare() {
        Privatlakare privatlakare = new Privatlakare();
        Set<LegitimeradYrkesgrupp> legitimeradYrkesgrupper = new HashSet<LegitimeradYrkesgrupp>();
        legitimeradYrkesgrupper.add(new LegitimeradYrkesgrupp(privatlakare, "Extra", "E"));
        legitimeradYrkesgrupper.add(new LegitimeradYrkesgrupp(privatlakare, "Mer", "M"));
        privatlakare.setLegitimeradeYrkesgrupper(legitimeradYrkesgrupper);

        assertFalse(PrivatlakareUtils.hasLakareLegitimation(privatlakare));
    }

}
