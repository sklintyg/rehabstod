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
package se.inera.intyg.rehabstod.service.diagnos;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import se.inera.intyg.rehabstod.service.diagnos.dto.DiagnosKod;

/**
 * Created by martin on 10/02/16.
 */
@Component
public class DiagnosKoderLoaderImpl implements DiagnosKoderLoader {

    @Value("${rhs.diagnoskod.ksh97_kat.file}")
    private String diagnosKodKS97KatFile;

    @Value("${rhs.diagnoskod.ksh97_kod.file}")
    private String diagnosKodKS97KodFile;

    @Value("${rhs.diagnoskod.ksh97_kxx.file}")
    private String diagnosKodKS97KxxFile;

    @Value("${rhs.diagnoskod.ksh97p_kod.file}")
    private String diagnosKodKS97PKodFile;

    @Autowired
    ResourceLoader resourceLoader;

    @Override
    public Map<String, String> loadDiagnosKoder() throws IOException {
        Map<String, String> map = new HashMap<>();

        map.putAll(loadDiagnosFile(diagnosKodKS97KatFile));
        map.putAll(loadDiagnosFile(diagnosKodKS97KodFile));
        map.putAll(loadDiagnosFile(diagnosKodKS97KxxFile));
        map.putAll(loadDiagnosFile(diagnosKodKS97PKodFile));

        return map;
    }

    private Map<String, String> loadDiagnosFile(final String file) throws IOException {

        // FIXME: Legacy support, can be removed when local config has been substituted by refdata (INTYG-7701)
        String location = ResourceUtils.isUrl(file) ? file : "file://" + file;
        Resource resource = resourceLoader.getResource(location);

        Map<String, String> map = new HashMap<>();
        try (LineIterator it = IOUtils.lineIterator(resource.getInputStream(), "ISO-8859-1")) {
            while (it.hasNext()) {
                final String line = it.nextLine();

                final DiagnosKod kod = new DiagnosKod(line);

                if (kod.getCleanedCode() != null) {
                    map.put(kod.getCleanedCode(), kod.getName());
                }
            }
        }
        return map;
    }
}
