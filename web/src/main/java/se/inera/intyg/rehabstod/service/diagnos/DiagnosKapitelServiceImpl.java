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
package se.inera.intyg.rehabstod.service.diagnos;

/**
 * Created by marced on 08/02/16.
 */

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import se.inera.intyg.rehabstod.service.diagnos.dto.DiagnosKapitel;
import se.inera.intyg.rehabstod.service.diagnos.dto.DiagnosKategori;

@Component
public class DiagnosKapitelServiceImpl implements DiagnosKapitelService {
    private static final Logger LOG = LoggerFactory.getLogger(DiagnosKapitelServiceImpl.class);


    private List<DiagnosKapitel> diagnosKapitelList;

    @Autowired
    private DiagnosKapitelLoader diagnosKapitelLoader;

    @PostConstruct
    public void init() {
        try {
            diagnosKapitelList = diagnosKapitelLoader.loadDiagnosKapitel();
            diagnosKapitelList.add(OGILTIGA_DIAGNOSKODER_KAPITEL);
        } catch (IOException e) {
            LOG.error("Failed to load diagnosKapitelList!");
            throw new RuntimeException(e);
        }
        LOG.info("Loaded " + diagnosKapitelList.size() + " diagnosKapitelList definitions");
    }

    @Override
    public List<DiagnosKapitel> getDiagnosKapitelList() {
        return diagnosKapitelList;
    }

    @Override
    public DiagnosKapitel getDiagnosKapitel(String diagnosKod) {
        return getDiagnosKapitelForDiagnosKategori(DiagnosKategori.extractFromString(diagnosKod));
    }

    private DiagnosKapitel getDiagnosKapitelForDiagnosKategori(Optional<DiagnosKategori> diagnosKategori) {
        return this.diagnosKapitelList.stream().filter(dk -> dk.includes(diagnosKategori)).findFirst().orElse(OGILTIGA_DIAGNOSKODER_KAPITEL);
    }

}
