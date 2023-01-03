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
package se.inera.intyg.rehabstod.integration.sparrtjanst.stub;

// CHECKSTYLE:OFF LineLength

import com.google.common.base.Joiner;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import se.riv.informationsecurity.authorization.blocking.CheckBlocks.v4.rivtabp21.CheckBlocksResponderInterface;
import se.riv.informationsecurity.authorization.blocking.CheckBlocksResponder.v4.CheckBlocksResponseType;
import se.riv.informationsecurity.authorization.blocking.CheckBlocksResponder.v4.CheckBlocksType;
import se.riv.informationsecurity.authorization.blocking.v4.CheckBlocksResultType;
import se.riv.informationsecurity.authorization.blocking.v4.CheckResultType;
import se.riv.informationsecurity.authorization.blocking.v4.CheckStatusType;
import se.riv.informationsecurity.authorization.blocking.v4.ResultCodeType;
import se.riv.informationsecurity.authorization.blocking.v4.ResultType;

// CHECKSTYLE:ON LineLength

/**
 * Created by eriklupander on 2016-01-29.
 */
public class SparrtjanstIntegrationStub implements CheckBlocksResponderInterface {

    @Autowired
    private SparrtjanstStubStore store;

    @Override
    public CheckBlocksResponseType checkBlocks(String logicalAddress, CheckBlocksType parameters) {
        validate(logicalAddress, parameters);

        CheckBlocksResponseType response = new CheckBlocksResponseType();
        CheckBlocksResultType result = new CheckBlocksResultType();
        List<CheckResultType> resultList = new ArrayList<>();

        for (int i = 0; i < parameters.getInformationEntities().size(); i++) {
            CheckResultType singleResult = new CheckResultType();
            singleResult.setRowNumber(i);
            LocalDate queryDateFrom = parameters.getInformationEntities().get(i).getInformationStartDate().toLocalDate();
            LocalDate queryDateTo = parameters.getInformationEntities().get(i).getInformationEndDate().toLocalDate();
            String vardGivareId = parameters.getInformationEntities().get(i).getInformationCareProviderId();
            String vardEnhetId = parameters.getInformationEntities().get(i).getInformationCareUnitId();
            singleResult
                .setStatus(store.isBlockedAtDate(parameters.getPatientId().getExtension(), queryDateFrom, queryDateTo,
                    vardGivareId, vardEnhetId)
                    ? CheckStatusType.BLOCKED
                    : CheckStatusType.OK);

            resultList.add(singleResult);
        }
        result.getCheckResults().addAll(resultList);

        ResultType resultType = new ResultType();
        resultType.setResultCode(ResultCodeType.OK);

        result.setResult(resultType);
        response.setCheckBlocksResult(result);
        return response;
    }

    private void validate(String logicalAddress, CheckBlocksType parameters) {
        List<String> messages = new ArrayList<>();
        if (logicalAddress == null || logicalAddress.length() == 0) {
            messages.add("logicalAddress can not be null or empty");
        }
        if (parameters == null) {
            messages.add("CheckBlocksType can not be null");
        } else {
            if (parameters.getAccessingActor().getCareProviderId().isEmpty()) {
                messages.add("Missing AccessingActor.CareProviderId");
            }
            if (parameters.getAccessingActor().getCareUnitId().isEmpty()) {
                messages.add("Missing AccessingActor.CareUnitId");
            }
            if (parameters.getAccessingActor().getEmployeeId().isEmpty()) {
                messages.add("Missing AccessingActor.EmployeeId");
            }
            if (parameters.getPatientId().getExtension().isEmpty()) {
                messages.add("Missing PatientId.Extension");
            }
            if (parameters.getInformationEntities().size() < 1) {
                messages.add("Missing InformationEntities");
            }
        }

        if (messages.size() > 0) {
            throw new IllegalArgumentException(Joiner.on(",").join(messages));
        }
    }
}
