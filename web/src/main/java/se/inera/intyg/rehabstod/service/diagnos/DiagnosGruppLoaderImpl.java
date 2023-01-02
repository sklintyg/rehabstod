/*
 * Copyright (C) 2023 Inera AB (http://www.inera.se)
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
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import se.inera.intyg.rehabstod.service.diagnos.dto.DiagnosGrupp;

/**
 * Created by marced on 14/03/16.
 */

@Component
public class DiagnosGruppLoaderImpl implements DiagnosGruppLoader {

    @Value("${rhs.diagnosgrupper.file}")
    private String diagnosGruppFile;

    @Autowired
    ResourceLoader resourceLoader;

    @Override
    public List<DiagnosGrupp> loadDiagnosGrupper() throws IOException {

        return getDiagnosGrupperInternal(diagnosGruppFile);
    }

    private List<DiagnosGrupp> getDiagnosGrupperInternal(String file) throws IOException {

        // FIXME: Legacy support, can be removed when local config has been substituted by refdata (INTYG-7701)
        String location = ResourceUtils.isUrl(file) ? file : "file://" + file;

        Resource resource = resourceLoader.getResource(location);

        List<DiagnosGrupp> list = new ArrayList<>();
        try (LineIterator it = IOUtils.lineIterator(resource.getInputStream(), "UTF-8")) {

            while (it.hasNext()) {
                final String line = it.nextLine();
                list.add(new DiagnosGrupp(line));
            }
        }
        return list;
    }
}
