package se.inera.intyg.rehabstod.common.integration.kodverk;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pebe on 2015-08-19.
 */
public class Verksamhetstyper {

    public static final String VERKSAMHETSTYP_NAME = "Verksamhetskod";
    public static final String VERKSAMHETSTYP_OID = "1.2.752.129.2.2.1.3";
    public static final String VERKSAMHETSTYP_VERSION = "4.1";

    private static final Map<String, String> VERKSAMHETSTYP_MAP;

    private Verksamhetstyper() {
    }

    static {
        Map<String, String> map = new HashMap();
        map.put("10", "Barn- och ungdomsverksamhet");
        map.put("11", "Medicinsk verksamhet");
        map.put("12", "Laboratorieverksamhet");
        map.put("13", "Opererande verksamhet");
        map.put("14", "Övrig medicinsk verksamhet");
        map.put("15", "Primärvårdsverksamhet");
        map.put("16", "Psykiatrisk verksamhet");
        map.put("17", "Radiologisk verksamhet");
        map.put("18", "Tandvårdsverksamhet");
        map.put("20", "Övrig medicinsk serviceverksamhet");
        VERKSAMHETSTYP_MAP = Collections.unmodifiableMap(map);
    }

    public static String getDisplayName(String code) {
        return VERKSAMHETSTYP_MAP.get(code);
    }

    public static Map<String, String> getVerksamhetstyper() {
        return VERKSAMHETSTYP_MAP;
    }
}
