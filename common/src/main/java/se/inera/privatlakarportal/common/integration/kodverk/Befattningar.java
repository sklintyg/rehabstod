package se.inera.privatlakarportal.common.integration.kodverk;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pebe on 2015-08-19.
 */
public class Befattningar {

    public static final String BEFATTNING_NAME = "Befattning HSA";
    public static final String BEFATTNING_OID = "1.2.752.129.2.2.1.4";
    public static final String BEFATTNING_VERSION = "3.1";

    private static final Map<String, String> BEFATTNINGS_MAP;

    private Befattningar() {
    }

    static {
        Map<String, String> map = new HashMap<>();
        map.put("201011", "Distriktsläkare/Specialist allmänmedicin");
        map.put("201012", "Skolläkare");
        map.put("201013", "Företagsläkare");
        map.put("202010", "Specialistläkare");
        map.put("203010", "Läkare legitimerad, specialiseringstjänstgöring");
        map.put("203090", "Läkare legitimerad, annan");
        BEFATTNINGS_MAP = Collections.unmodifiableMap(map);
    }

    public static String getDisplayName(String code) {
        return BEFATTNINGS_MAP.get(code);
    }

    public static Map<String, String> getBefattningar() {
        return BEFATTNINGS_MAP;
    }
}
