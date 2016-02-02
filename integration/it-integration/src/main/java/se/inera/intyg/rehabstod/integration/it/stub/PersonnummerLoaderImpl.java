/**
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
 *
 * This file is part of rehabstod (https://github.com/sklintyg/rehabstod).
 *
 * rehabstod is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * rehabstod is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.intyg.rehabstod.integration.it.stub;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.springframework.beans.factory.annotation.Value;
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
@Profile({"dev", "rhs-it-stub"})
public class PersonnummerLoaderImpl implements PersonnummerLoader {

    @Value("${rhs.stub.personnummer.file}")
    private String testPersonnummerFile;

    public List<String> readTestPersonnummer() throws IOException {
        Resource resource = getResource(testPersonnummerFile);
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
