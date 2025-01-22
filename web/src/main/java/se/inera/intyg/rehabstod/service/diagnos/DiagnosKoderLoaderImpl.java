/*
 * Copyright (C) 2025 Inera AB (http://www.inera.se)
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
import lombok.RequiredArgsConstructor;
import se.inera.intyg.rehabstod.service.diagnos.dto.DiagnosKod;

/**
 * Created by martin on 10/02/16.
 */
@Component
@RequiredArgsConstructor
public class DiagnosKoderLoaderImpl implements DiagnosKoderLoader {

    private static final Logger LOG = LoggerFactory.getLogger(DiagnosKoderLoaderImpl.class);
    private final IcdCodeConverter icdCodeConverter;
    @Value("${rhs.diagnoskod.ksh97p_kod.file}")
    private String diagnosKodKS97PKodFile;
    @Value("${rhs.diagnosisCode.icd10se.file}")
    private String diagnosisCodeIcd10SeFile;

    @Autowired
    ResourceLoader resourceLoader;

    @Override
    public Map<String, String> loadDiagnosKoder() throws IOException {
        Map<String, String> map = new HashMap<>();
        map.putAll(icdCodeConverter.convert(diagnosisCodeIcd10SeFile));
        map.putAll(loadDiagnosFile(diagnosKodKS97PKodFile));
        return map;
    }

    private Map<String, String> loadDiagnosFile(final String file) throws IOException {

        // FIXME: Legacy support, can be removed when local config has been substituted by refdata (INTYG-7701)
        String location = ResourceUtils.isUrl(file) ? file : "file://" + file;
        Resource resource = resourceLoader.getResource(location);

        Map<String, String> map = new HashMap<>();
        int count = 0;
        try (LineIterator it = IOUtils.lineIterator(resource.getInputStream(), StandardCharsets.ISO_8859_1)) {
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
