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

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import se.inera.intyg.clinicalprocess.healthcond.rehabilitation.listactivesickleavesforperson.v1.ListActiveSickLeavesForPersonResponderInterface;
import se.inera.intyg.clinicalprocess.healthcond.rehabilitation.listactivesickleavesforperson.v1.ListActiveSickLeavesForPersonResponseType;
import se.inera.intyg.clinicalprocess.healthcond.rehabilitation.listactivesickleavesforperson.v1.ListActiveSickLeavesForPersonType;
import se.inera.intyg.clinicalprocess.healthcond.rehabilitation.listactivesickleavesforperson.v1.ResultType;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.IntygsData;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.IntygsLista;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// CHECKSTYLE:ON LineLength

/**
 * Created by Magnus Ekstrand on 2018-10-26.
 */
@Service
@Profile({"rhs-it-stub"})
public class ListActiveSickLeavesForPersonStub implements ListActiveSickLeavesForPersonResponderInterface {

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
    public ListActiveSickLeavesForPersonResponseType listActiveSickLeavesForPerson(String s, ListActiveSickLeavesForPersonType parameters) {

        //Just interested in a specific patient?
        String personnummer = parameters.getPersonId() != null && parameters.getPersonId().getExtension() != null
                ? parameters.getPersonId().getExtension().trim()
                : null;

        Preconditions.checkArgument(!Strings.isNullOrEmpty(personnummer));

        IntygsLista intygsLista = new IntygsLista();
        intygsLista.getIntygsData().addAll(intygsData);

        intygsLista.getIntygsData().addAll(intygsData.stream()
                .filter(item -> personnummer.equals(item.getPatient().getPersonId().getExtension()))
                .collect(Collectors.toList()));

        ResultType resultType = new ResultType();
        resultType.setResultCode(
                se.inera.intyg.clinicalprocess.healthcond.rehabilitation.listactivesickleavesforperson.v1.ResultCodeEnum.OK);

        ListActiveSickLeavesForPersonResponseType resp = new ListActiveSickLeavesForPersonResponseType();
        resp.setResult(resultType);
        resp.setIntygsLista(intygsLista);

        return resp;
    }
}
