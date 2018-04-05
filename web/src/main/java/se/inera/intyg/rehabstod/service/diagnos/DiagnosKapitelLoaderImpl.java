/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import se.inera.intyg.rehabstod.service.diagnos.dto.DiagnosKapitel;

/**
 * Created by marced on 08/02/16.
 */
@Component
public class DiagnosKapitelLoaderImpl implements DiagnosKapitelLoader {

    @Value("${rhs.diagnoskapitel.file}")
    private Resource diagnosKapitelFile;

    @Override
    public List<DiagnosKapitel> loadDiagnosKapitel() throws IOException {

        LineIterator it = FileUtils.lineIterator(diagnosKapitelFile.getFile(), "UTF-8");

        List<DiagnosKapitel> list = new ArrayList<>();
        try {

            while (it.hasNext()) {
                String line = it.nextLine();
                list.add(new DiagnosKapitel(line));
            }
        } finally {
            LineIterator.closeQuietly(it);
        }
        return list;
    }
}
