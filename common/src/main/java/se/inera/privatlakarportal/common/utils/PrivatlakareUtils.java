package se.inera.privatlakarportal.common.utils;

import se.inera.privatlakarportal.persistence.model.LegitimeradYrkesgrupp;
import se.inera.privatlakarportal.persistence.model.Privatlakare;

/**
 * Created by pebe on 2015-09-07.
 */
public class PrivatlakareUtils {

    private static final String LAKARE = "Läkare";

    private PrivatlakareUtils() {
    }

    public static boolean hasLakareLegitimation(Privatlakare privatlakare) {
        for (LegitimeradYrkesgrupp legitimeradYrkesgrupp : privatlakare.getLegitimeradeYrkesgrupper()) {
            if (legitimeradYrkesgrupp.getNamn().equals(LAKARE)) {
                return true;
            }
        }
        return false;
    }
}
