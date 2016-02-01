package se.inera.intyg.rehabstod.integration.it.stub;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads skatteverkets testpersonnummer from .csv file.
 *
 * Created by eriklupander on 2016-01-31.
 */
@Component
@Profile({"dev", "rhs-hsa-stub"})
public class PersonnummerLoaderImpl implements PersonnummerLoader {

    public List<String> readTestPersonnummer() throws IOException {
        Resource resource = getResource("classpath:stubdata/testpersonnummer_skatteverket.csv");
        LineIterator it = FileUtils.lineIterator(resource.getFile(), "UTF-8");

        List<String> personnummerList = new ArrayList<>();
        try {
            while (it.hasNext()) {
                String line = it.nextLine();
                personnummerList.add(line);
            }
        } finally {
            LineIterator.closeQuietly(it);
        }
        return personnummerList;
    }

    private Resource getResource(String location) {
        PathMatchingResourcePatternResolver r = new PathMatchingResourcePatternResolver();
        return r.getResource(location);
    }
}
