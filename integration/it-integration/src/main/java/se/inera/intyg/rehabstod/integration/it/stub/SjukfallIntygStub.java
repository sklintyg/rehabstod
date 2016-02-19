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
package se.inera.intyg.rehabstod.integration.it.stub;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import se.inera.intyg.clinicalprocess.healthcond.rehabilitation.listactivesickleavesforcareunit.v1.ListActiveSickLeavesForCareUnitResponderInterface;
import se.inera.intyg.clinicalprocess.healthcond.rehabilitation.listactivesickleavesforcareunit.v1.ListActiveSickLeavesForCareUnitResponseType;
import se.inera.intyg.clinicalprocess.healthcond.rehabilitation.listactivesickleavesforcareunit.v1.ListActiveSickLeavesForCareUnitType;
import se.inera.intyg.clinicalprocess.healthcond.rehabilitation.listactivesickleavesforcareunit.v1.ResultCodeEnum;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.IntygsData;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.IntygsLista;

/**
 * Created by eriklupander on 2016-01-29.
 */
@Service
@Profile({"rhs-it-stub"})
public class SjukfallIntygStub implements ListActiveSickLeavesForCareUnitResponderInterface {

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



    @Override
    public ListActiveSickLeavesForCareUnitResponseType listActiveSickLeavesForCareUnit(String logicalAddress, ListActiveSickLeavesForCareUnitType parameters) {
        ListActiveSickLeavesForCareUnitResponseType resp = new ListActiveSickLeavesForCareUnitResponseType();
        resp.setResultCode(ResultCodeEnum.OK);

        IntygsLista intygsLista = new IntygsLista();
        intygsLista.getIntygsData().addAll(intygsData);
        resp.setIntygsLista(intygsLista);
        return resp;
    }
}
