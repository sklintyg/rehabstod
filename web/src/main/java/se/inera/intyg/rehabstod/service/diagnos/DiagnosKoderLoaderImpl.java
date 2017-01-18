/**
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.service.diagnos;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import se.inera.intyg.rehabstod.service.diagnos.dto.DiagnosKod;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by martin on 10/02/16.
 */
@Component
public class DiagnosKoderLoaderImpl implements DiagnosKoderLoader {

    @Value("${rhs.diagnoskod.ksh97_kat.file}")
    private Resource diagnosKodKS97KatFile;

    @Value("${rhs.diagnoskod.ksh97_kod.file}")
    private Resource diagnosKodKS97KodFile;

    @Value("${rhs.diagnoskod.ksh97_kxx.file}")
    private Resource diagnosKodKS97KxxFile;

    @Value("${rhs.diagnoskod.ksh97p_kod.file}")
    private Resource diagnosKodKS97PKodFile;

    @Override
    public Map<String, String> loadDiagnosKoder() throws IOException {
        Map<String, String> map = new HashMap<>();

        map.putAll(loadDiagnosFile(diagnosKodKS97KatFile));
        map.putAll(loadDiagnosFile(diagnosKodKS97KodFile));
        map.putAll(loadDiagnosFile(diagnosKodKS97KxxFile));
        map.putAll(loadDiagnosFile(diagnosKodKS97PKodFile));

        return map;
    }

    private Map<String, String> loadDiagnosFile(Resource resource) throws IOException {
        LineIterator it = FileUtils.lineIterator(resource.getFile(), "ISO-8859-1");

        Map<String, String> map = new HashMap<>();
        try {

            while (it.hasNext()) {
                String line = it.nextLine();

                DiagnosKod kod = new DiagnosKod(line);

                if (kod.getId() != null) {
                    map.put(kod.getId(), kod.getName());
                }
            }
        } finally {
            LineIterator.closeQuietly(it);
        }
        return map;
    }
}
