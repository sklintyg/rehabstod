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
import java.util.Map;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by martin on 10/02/16.
 */
@Component
public class DiagnosBeskrivningServiceImpl implements DiagnosBeskrivningService {

    private static final Logger LOG = LoggerFactory.getLogger(DiagnosBeskrivningServiceImpl.class);

    private Map<String, String> diagnosBeskrivningMap;

    @Autowired
    private DiagnosKoderLoader diagnosKoderLoader;

    @PostConstruct
    public void init() {
        try {
            diagnosBeskrivningMap = diagnosKoderLoader.loadDiagnosKoder();
        } catch (IOException e) {
            throw new DiagnosServiceException("Failed to load diagnosBeskrivning!", e);
        }
        LOG.info("Loaded " + diagnosBeskrivningMap.size() + " diagnosBeskrivning definitions");
    }

    @Override
    public String getDiagnosBeskrivning(String diagnosKod) {
        return diagnosBeskrivningMap.get(diagnosKod);
    }
}
