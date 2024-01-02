/*
 * Copyright (C) 2024 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.service.diagnos;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import se.inera.intyg.rehabstod.service.diagnos.dto.DiagnosKapitel;

/**
 * Created by marced on 08/02/16.
 */
@Component
public class DiagnosKapitelLoaderImpl implements DiagnosKapitelLoader {

    @Value("${rhs.diagnoskapitel.file}")
    private String diagnosKapitelFile;

    @Autowired
    ResourceLoader resourceLoader;

    @PostConstruct
    void initialize() {
        // FIXME: Legacy support, can be removed when local config has been substituted by refdata (INTYG-7701)
        if (!ResourceUtils.isUrl(diagnosKapitelFile)) {
            diagnosKapitelFile = "file://" + diagnosKapitelFile;
        }
    }

    @Override
    public List<DiagnosKapitel> loadDiagnosKapitel() throws IOException {

        Resource resource = resourceLoader.getResource(diagnosKapitelFile);

        List<DiagnosKapitel> list = new ArrayList<>();
        try (LineIterator it = IOUtils.lineIterator(resource.getInputStream(), "UTF-8")) {

            while (it.hasNext()) {
                final String line = it.nextLine();
                list.add(new DiagnosKapitel(line));
            }
        }
        return list;
    }
}
