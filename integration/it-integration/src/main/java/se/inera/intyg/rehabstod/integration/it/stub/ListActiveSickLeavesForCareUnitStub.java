/*
 * Copyright (C) 2022 Inera AB (http://www.inera.se)
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

import com.google.common.base.Strings;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import se.inera.intyg.clinicalprocess.healthcond.rehabilitation.listactivesickleavesforcareunit.v1.ListActiveSickLeavesForCareUnitResponderInterface;
import se.inera.intyg.clinicalprocess.healthcond.rehabilitation.listactivesickleavesforcareunit.v1.ListActiveSickLeavesForCareUnitResponseType;
import se.inera.intyg.clinicalprocess.healthcond.rehabilitation.listactivesickleavesforcareunit.v1.ListActiveSickLeavesForCareUnitType;
import se.inera.intyg.clinicalprocess.healthcond.rehabilitation.listactivesickleavesforcareunit.v1.ResultCodeEnum;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.IntygsLista;

// CHECKSTYLE:ON LineLength

/**
 * Created by eriklupander on 2016-01-29.
 */
@Service
@Profile({"rhs-it-stub"})
public class ListActiveSickLeavesForCareUnitStub implements ListActiveSickLeavesForCareUnitResponderInterface {

    @Autowired
    private RSTestIntygStub rsTestIntygStub;

    @Override
    public ListActiveSickLeavesForCareUnitResponseType listActiveSickLeavesForCareUnit(String logicalAddress,
        ListActiveSickLeavesForCareUnitType parameters) {
        ListActiveSickLeavesForCareUnitResponseType resp = new ListActiveSickLeavesForCareUnitResponseType();
        resp.setResultCode(ResultCodeEnum.OK);

        List<String> enhetIds = RSTestDataGeneratorHelper.getUnderenheterHsaIds(parameters.getEnhetsId().getExtension());
        enhetIds.add(parameters.getEnhetsId().getExtension());

        IntygsLista intygsLista = new IntygsLista();

        //Just interested in a specific patient?
        String personnummer = parameters.getPersonId() != null && parameters.getPersonId().getExtension() != null
            ? parameters.getPersonId().getExtension().trim()
            : null;

        if (!Strings.isNullOrEmpty(personnummer)) {
            intygsLista.getIntygsData().addAll(rsTestIntygStub.getIntygsData().stream()
                .filter(id -> enhetIds.contains(id.getSkapadAv().getEnhet().getEnhetsId().getExtension())
                    && personnummer.equals(id.getPatient().getPersonId().getExtension()))
                .collect(Collectors.toList()));
        } else {
            intygsLista.getIntygsData().addAll(rsTestIntygStub.getIntygsData().stream()
                .filter(id -> enhetIds.contains(id.getSkapadAv().getEnhet().getEnhetsId().getExtension()))
                .collect(Collectors.toList()));
        }

        resp.setIntygsLista(intygsLista);
        return resp;
    }

}
