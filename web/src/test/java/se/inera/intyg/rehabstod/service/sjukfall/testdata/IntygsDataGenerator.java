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
package se.inera.intyg.rehabstod.service.sjukfall.testdata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.IntygsData;

/**
 * Created by Magnus Ekstrand on 2016-02-10.
 */
public class IntygsDataGenerator {

    private final int linesToSkip = 1;

    private IntygsDataReader reader;
    private List<IntygsData> intygsData;

    public IntygsDataGenerator(String location) {
        this.reader = new IntygsDataReader(location, linesToSkip);
        this.intygsData = new ArrayList<>();
    }

    public IntygsDataGenerator generate() throws IOException {
        List<String> csvlines = reader.read();
        intygsData = IntygsDataLineMapper.map(csvlines);
        return this;
    }

    public List<IntygsData> get() {
        return this.intygsData;
    }

}
