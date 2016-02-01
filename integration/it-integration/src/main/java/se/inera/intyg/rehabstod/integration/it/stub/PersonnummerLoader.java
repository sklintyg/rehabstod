package se.inera.intyg.rehabstod.integration.it.stub;

import java.io.IOException;
import java.util.List;

/**
 * Created by eriklupander on 2016-01-31.
 */
public interface PersonnummerLoader {
    List<String> readTestPersonnummer() throws IOException;
}
