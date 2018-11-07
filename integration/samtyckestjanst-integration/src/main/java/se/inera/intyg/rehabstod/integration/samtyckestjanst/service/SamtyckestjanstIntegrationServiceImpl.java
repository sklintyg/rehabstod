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
package se.inera.intyg.rehabstod.integration.samtyckestjanst.service;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.inera.intyg.rehabstod.integration.samtyckestjanst.client.SamtyckestjanstClientService;
import se.inera.intyg.rehabstod.integration.samtyckestjanst.exception.SamtyckestjanstIntegrationException;
import se.inera.intyg.schemas.contract.Personnummer;
import se.riv.informationsecurity.authorization.consent.CheckConsentResponder.v2.CheckConsentResponseType;
import se.riv.informationsecurity.authorization.consent.RegisterExtendedConsentResponder.v2.RegisterExtendedConsentResponseType;
import se.riv.informationsecurity.authorization.consent.v2.ActionType;
import se.riv.informationsecurity.authorization.consent.v2.CheckResultType;
import se.riv.informationsecurity.authorization.consent.v2.ResultCodeType;
import se.riv.informationsecurity.authorization.consent.v2.ResultType;

import java.time.LocalDateTime;

/**
 * Created by Magnus Ekstrand on 2018-10-10.
 */
@Service("samtyckestjanstIntegrationService")
public class SamtyckestjanstIntegrationServiceImpl implements SamtyckestjanstIntegrationService {

    private static final Logger LOG = LoggerFactory.getLogger(SamtyckestjanstIntegrationServiceImpl.class);

    @Autowired
    private SamtyckestjanstClientService samtyckestjanstClientService;

    @Override
    public boolean checkForConsent(String patientId, String userHsaId, String currentVardgivarHsaId, String currentEnhetsId) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(patientId), "patientId may not be null or empty");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(userHsaId), "employeeId may not be null or empty");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(currentVardgivarHsaId), "currentVardgivarHsaId may not be null or empty");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(currentEnhetsId), "currentEnhetsId may not be null or empty");

        // Make call to the CheckConsent service and handle response
        final CheckConsentResponseType response =
                samtyckestjanstClientService.checkConsent(currentVardgivarHsaId, currentEnhetsId, userHsaId, patientId);

        if (response == null || response.getCheckResult() == null) {
            throw new SamtyckestjanstIntegrationException("Failed to get response from CheckConsent service");
        }

        // OK = OK
        // INFO = Some of the request information resouces ha an validation error, but a response should still have been
        // provided
        CheckResultType checkResultType = response.getCheckResult();
        if (checkResultType.getResult().getResultCode() != ResultCodeType.OK) {
            if (checkResultType.getResult().getResultCode() == ResultCodeType.INFO) {
                LOG.warn(String.format(
                        "checkForConsent received INFO result with resultText '%s' - a partially valid response is still expected.",
                        checkResultType.getResult().getResultText()));
            } else {
                throw new SamtyckestjanstIntegrationException(
                        String.format("checkForConsent failed with resultCode %s and resultText '%s'",
                                checkResultType.getResult().getResultCode(),
                                checkResultType.getResult().getResultText()));
            }
        }

        return checkResultType.isHasConsent();
    }

    @Override
    // CHECKSTYLE:OFF ParameterNumber
    public void registerConsent(String vgHsaId,
                                String veHsaId,
                                Personnummer patientId,
                                String userHsaId,
                                String representedBy,
                                LocalDateTime consentFrom,
                                LocalDateTime consentTo,
                                ActionType registrationAction) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(vgHsaId), "vgHsaId may not be null or empty");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(veHsaId), "veHsaId may not be null or empty");
        Preconditions.checkNotNull(patientId, "patientId may not be null");
        Preconditions.checkNotNull(registrationAction, "registrationAction may not be null");

        // Make call to the CheckConsent service and handle response
        final RegisterExtendedConsentResponseType response =
                samtyckestjanstClientService.registerExtendedConsent(vgHsaId, veHsaId, userHsaId, patientId,
                        representedBy, consentFrom, consentTo, registrationAction);

        if (response == null || response.getResult() == null) {
            throw new SamtyckestjanstIntegrationException("Failed to get response from RegisterExtendedConsent service");
        }

        // OK = OK
        // INFO = Some of the request information resouces ha an validation error, but a response should still have been
        // provided
        ResultType resultType = response.getResult();
        if (resultType.getResultCode() != ResultCodeType.OK) {
            if (resultType.getResultCode() == ResultCodeType.INFO) {
                LOG.warn(String.format(
                        "registerConsent received INFO result with resultText '%s' - a partially valid response is still expected.",
                        resultType.getResultText()));
            } else {
                throw new SamtyckestjanstIntegrationException(
                        String.format("registerConsent failed with resultCode %s and resultText '%s'",
                                resultType.getResultCode(),
                                resultType.getResultText()));
            }
        }
    }
    // CHECKSTYLE:ON ParameterNumber

}
