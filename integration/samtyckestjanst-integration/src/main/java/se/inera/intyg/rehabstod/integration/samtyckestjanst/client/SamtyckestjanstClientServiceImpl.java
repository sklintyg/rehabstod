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
package se.inera.intyg.rehabstod.integration.samtyckestjanst.client;

import com.google.common.base.Strings;
import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import se.inera.intyg.rehabstod.integration.samtyckestjanst.exception.SamtyckestjanstIntegrationException;
import se.inera.intyg.rehabstod.integration.samtyckestjanst.util.SamtyckestjanstUtil;
import se.inera.intyg.schemas.contract.Personnummer;
import se.riv.informationsecurity.authorization.consent.CheckConsent.v2.rivtabp21.CheckConsentResponderInterface;
import se.riv.informationsecurity.authorization.consent.CheckConsentResponder.v2.CheckConsentResponseType;
import se.riv.informationsecurity.authorization.consent.CheckConsentResponder.v2.CheckConsentType;
import se.riv.informationsecurity.authorization.consent.RegisterExtendedConsent.v2.rivtabp21.RegisterExtendedConsentResponderInterface;
import se.riv.informationsecurity.authorization.consent.RegisterExtendedConsentResponder.v2.RegisterExtendedConsentResponseType;
import se.riv.informationsecurity.authorization.consent.RegisterExtendedConsentResponder.v2.RegisterExtendedConsentType;
import se.riv.informationsecurity.authorization.consent.v2.ActionType;
import se.riv.informationsecurity.authorization.consent.v2.AssertionTypeType;
import se.riv.informationsecurity.authorization.consent.v2.ScopeType;

/**
 * Created by Magnus Ekstrand 2018-10-10.
 */
@Service
public class SamtyckestjanstClientServiceImpl implements SamtyckestjanstClientService {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());

    private static final ScopeType SCOPE_TYPE = ScopeType.NATIONAL_LEVEL;
    private static final AssertionTypeType ASSERTION_TYPE = AssertionTypeType.CONSENT;

    @Autowired
    private CheckConsentResponderInterface checkConsentservice;

    @Autowired
    private RegisterExtendedConsentResponderInterface registerExtendedConsentService;

    @Value("${samtyckestjanst.service.logicalAddress}")
    private String logicalAddress;

    /**
     * @see SamtyckestjanstClientService#checkConsent(String, String, String, String) checkConsent
     */
    @Override
    public CheckConsentResponseType checkConsent(String vgHsaId, String veHsaId, String userHsaId, String patientId) {

        final Personnummer personnummer = createPersonnummer(patientId, "PatientId");

        CheckConsentType checkConsentType = new CheckConsentType();
        checkConsentType.setPatientId(SamtyckestjanstUtil.buildIITypeForPersonOrSamordningsnummer(personnummer));
        checkConsentType.setAccessingActor(SamtyckestjanstUtil.buildAccessingActorType(vgHsaId, veHsaId, userHsaId));

        try {
            return checkConsentservice.checkConsent(logicalAddress, checkConsentType);
        } catch (Exception e) {
            LOG.error("CONSENT_SERVICE: Failure when calling Samtyckestjänsten and the checkConsent service.");
            throw new SamtyckestjanstIntegrationException(e.getMessage());
        }
    }

    /**
     * @see SamtyckestjanstClientService#registerExtendedConsent(String, String, String, Personnummer, String,
     * LocalDateTime, LocalDateTime, ActionType) registerConsent
     */
    @Override
    // CHECKSTYLE:OFF ParameterNumber
    public RegisterExtendedConsentResponseType registerExtendedConsent(String vgHsaId,
        String veHsaId,
        String userHsaId,
        Personnummer patientId,
        String representedBy,
        LocalDateTime consentFrom,
        LocalDateTime consentTo,
        ActionType registrationAction) {

        LocalDateTime startDate = consentFrom;
        LocalDateTime endDate = consentTo;

        RegisterExtendedConsentType registerExtendedConsentType = new RegisterExtendedConsentType();

        //Unik, global identifierare för intyget. Genereras vid skapande av samtycke.
        registerExtendedConsentType.setAssertionId(UUID.randomUUID().toString());

        //Omfånget/tillämpningsområde på intyget - fast värde.
        registerExtendedConsentType.setScope(SCOPE_TYPE);

        //Typ av intyg som ger direktåtkomst till information från andra vårdgivare enligt PDL - fast värde.
        registerExtendedConsentType.setAssertionType(ASSERTION_TYPE);

        registerExtendedConsentType.setCareProviderId(vgHsaId);
        registerExtendedConsentType.setCareUnitId(veHsaId);
        registerExtendedConsentType.setPatientId(SamtyckestjanstUtil.buildIITypeForPersonOrSamordningsnummer(patientId));

        if (!Strings.isNullOrEmpty(userHsaId)) {
            registerExtendedConsentType.setEmployeeId(userHsaId);
        }

        if (!Strings.isNullOrEmpty(representedBy)) {
            registerExtendedConsentType.setRepresentedBy(
                SamtyckestjanstUtil.buildIITypeForPersonOrSamordningsnummer(
                    createPersonnummer(representedBy, "representedBy")));
        }

        if (startDate == null) {
            startDate = LocalDateTime.now();
        }
        registerExtendedConsentType.setStartDate(startDate);

        if (endDate != null) {
            if (endDate.isBefore(startDate)) {
                throw new IllegalArgumentException(String.format(
                    "a consent's start date %tF must be before its end date %tF", startDate, endDate));
            }
            registerExtendedConsentType.setEndDate(endDate);
        }

        registerExtendedConsentType.setRegistrationAction(registrationAction);

        try {
            return registerExtendedConsentService.registerExtendedConsent(logicalAddress, registerExtendedConsentType);
        } catch (Exception e) {
            LOG.error("SAMTYCKESTJÄNSTEN: Failure when calling Samtyckestjänsten and the registerExtendedConsent service.");
            throw new SamtyckestjanstIntegrationException(e.getMessage());
        }
    }
    // CHECKSTYLE:ON ParameterNumber

    private Personnummer createPersonnummer(String str, String fieldName) {
        return Personnummer
            .createPersonnummer(str)
            .orElseThrow(() -> new IllegalArgumentException(fieldName + " must be a valid personnummer or samordningsnummer"));
    }

}
