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
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOG = LoggerFactory.getLogger(DiagnosKoderLoaderImpl.class);

    @Value("${rhs.diagnoskod.icd10se.file1}")
    private String diagnoseCodeIcd10SeFile1;

    @Value("${rhs.diagnoskod.icd10se.file2}")
    private String diagnoseCodeIcd10SeFile2;

    @Value("${rhs.diagnoskod.icd10se.file3}")
    private String diagnoseCodeIcd10SeFile3;

    @Value("${rhs.diagnoskod.ksh97p_kod.file}")
    private String diagnosKodKS97PKodFile;

    @Autowired
    ResourceLoader resourceLoader;

    @Override
    public Map<String, String> loadDiagnosKoder() throws IOException {
        Map<String, String> map = new HashMap<>();

        map.putAll(loadDiagnosFile(diagnoseCodeIcd10SeFile1, StandardCharsets.UTF_8));
        map.putAll(loadDiagnosFile(diagnoseCodeIcd10SeFile2, StandardCharsets.UTF_8));
        map.putAll(loadDiagnosFile(diagnoseCodeIcd10SeFile3, StandardCharsets.UTF_8));
        map.putAll(loadDiagnosFile(diagnosKodKS97PKodFile, StandardCharsets.ISO_8859_1));

        return map;
    }

    private Map<String, String> loadDiagnosFile(final String file, Charset fileEncoding) throws IOException {

        // FIXME: Legacy support, can be removed when local config has been substituted by refdata (INTYG-7701)
        String location = ResourceUtils.isUrl(file) ? file : "file://" + file;
        Resource resource = resourceLoader.getResource(location);

        Map<String, String> map = new HashMap<>();
        int count = 0;
        try (LineIterator it = IOUtils.lineIterator(resource.getInputStream(), fileEncoding)) {
            while (it.hasNext()) {
                final String line = it.nextLine();
                final DiagnosKod kod = new DiagnosKod(line, count == 0);

                if (kod.getCode() != null) {
                    map.put(kod.getCode(), kod.getName());
                    count++;
                }
            }
        }
        LOG.info("Loaded {} codes from file {}", count, file);

        return map;
    }

}
