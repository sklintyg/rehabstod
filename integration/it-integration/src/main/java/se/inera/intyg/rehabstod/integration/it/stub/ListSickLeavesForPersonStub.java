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

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import se.inera.intyg.clinicalprocess.healthcond.rehabilitation.listsickleavesforperson.v1.ListSickLeavesForPersonResponderInterface;
import se.inera.intyg.clinicalprocess.healthcond.rehabilitation.listsickleavesforperson.v1.ListSickLeavesForPersonResponseType;
import se.inera.intyg.clinicalprocess.healthcond.rehabilitation.listsickleavesforperson.v1.ListSickLeavesForPersonType;
import se.inera.intyg.clinicalprocess.healthcond.rehabilitation.listsickleavesforperson.v1.ResultType;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.IntygsLista;

// CHECKSTYLE:ON LineLength

/**
 * Created by Magnus Ekstrand on 2018-10-26.
 */
@Service
@Profile({"rhs-it-stub"})
public class ListSickLeavesForPersonStub implements ListSickLeavesForPersonResponderInterface {

    @Autowired
    private  SjukfallIntygStub sjukfallIntygStub;

    @Override
    public ListSickLeavesForPersonResponseType listSickLeavesForPerson(String s, ListSickLeavesForPersonType parameters) {

        //Just interested in a specific patient?
        String personnummer = parameters.getPersonId() != null && parameters.getPersonId().getExtension() != null
                ? parameters.getPersonId().getExtension().trim()
                : null;

        Preconditions.checkArgument(!Strings.isNullOrEmpty(personnummer));

        IntygsLista intygsLista = new IntygsLista();
        intygsLista.getIntygsData().addAll(sjukfallIntygStub.getIntygsData().stream()
                .filter(item -> personnummer.equals(item.getPatient().getPersonId().getExtension()))
                .collect(Collectors.toList()));

        ResultType resultType = new ResultType();
        resultType.setResultCode(
                se.inera.intyg.clinicalprocess.healthcond.rehabilitation.listsickleavesforperson.v1.ResultCodeEnum.OK);

        ListSickLeavesForPersonResponseType resp = new ListSickLeavesForPersonResponseType();
        resp.setResult(resultType);
        resp.setIntygsLista(intygsLista);

        return resp;
    }
}
