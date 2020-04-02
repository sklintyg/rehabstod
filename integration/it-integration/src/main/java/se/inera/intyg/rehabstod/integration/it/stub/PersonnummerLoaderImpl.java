/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

/**
 * Loads skatteverkets testpersonnummer from .csv file.
 *
 * Created by eriklupander on 2016-01-31.
 */
@Component
@Profile({"dev", "rhs-it-stub"})
public class PersonnummerLoaderImpl implements PersonnummerLoader {

    @Value("${rhs.stub.personnummer.file}")
    private String location;

    @PostConstruct
    void initialize() {
        // FIXME: Legacy support, can be removed when local config has been substituted by refdata (INTYG-7701)
        if (!ResourceUtils.isUrl(location)) {
            location = "file:" + location;
        }
    }

    @Override
    public List<String> readTestPersonnummer() throws IOException {
        Resource resource = getResource(location);

        List<String> personnummerList = new ArrayList<>();
        try (LineIterator it = IOUtils.lineIterator(resource.getInputStream(), "UTF-8")) {
            // Skip CSV column name
            if (it.hasNext()) {
                it.nextLine();
            }

            while (it.hasNext()) {
                String line = it.nextLine();
                personnummerList.add(line);
            }
        }
        return personnummerList;
    }

    private Resource getResource(String location) {
        PathMatchingResourcePatternResolver r = new PathMatchingResourcePatternResolver();
        return r.getResource(location);
    }
}
