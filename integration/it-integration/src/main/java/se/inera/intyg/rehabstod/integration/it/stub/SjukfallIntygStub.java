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
package se.inera.intyg.rehabstod.integration.it.stub;

// CHECKSTYLE:OFF LineLength

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.IntygsData;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

// CHECKSTYLE:ON LineLength

/**
 * Created by Magnus Ekstrand on 2018-10-26.
 */
@Service
@Profile({"rhs-it-stub"})
public class SjukfallIntygStub {

    private List<IntygsData> intygsData = new ArrayList<>();

    @Autowired
    private SjukfallIntygDataGenerator sjukfallIntygDataGenerator;

    @Value("${rhs.sjukfall.stub.numberOfPatients}")
    private Integer numberOfPatients;

    @Value("${rhs.sjukfall.stub.intygPerPatient}")
    private Integer intygPerPatient;

    @PostConstruct
    public void init() {
        intygsData = sjukfallIntygDataGenerator.generateIntygsData(numberOfPatients, intygPerPatient);
    }

    public List<IntygsData> getIntygsData() {
        return intygsData;
    }
}
