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
package se.inera.intyg.rehabstod.integration.samtyckestjanst.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import se.inera.intyg.rehabstod.integration.samtyckestjanst.util.SamtyckestjanstUtil;
import se.inera.intyg.schemas.contract.Personnummer;
import se.riv.informationsecurity.authorization.consent.CheckConsent.v2.rivtabp21.CheckConsentResponderInterface;
import se.riv.informationsecurity.authorization.consent.CheckConsentResponder.v2.CheckConsentResponseType;
import se.riv.informationsecurity.authorization.consent.CheckConsentResponder.v2.CheckConsentType;

/**
 * Created by Magnus Ekstrand 2018-10-10.
 */
@Service
public class SamtyckestjanstClientServiceImpl implements SamtyckestjanstClientService {

    @Autowired
    private CheckConsentResponderInterface service;

    @Value("${samtyckestjanst.service.logicalAddress}")
    private String logicalAddress;

    /**
     * @see SamtyckestjanstClientService#checkConsent(String, String, String, String) checkConsent
     */
    @Override
    public CheckConsentResponseType checkConsent(String vgHsaId, String veHsaId, String userHsaId, String patientId) {

        final Personnummer personnummer = Personnummer.createPersonnummer(patientId)
                .orElseThrow(() -> new IllegalArgumentException("PatientId must be a valid personnummer or samordningsnummer"));

        CheckConsentType checkConsentType = new CheckConsentType();
        checkConsentType.setPatientId(SamtyckestjanstUtil.buildIITypeForPersonOrSamordningsnummer(personnummer));
        checkConsentType.setAccessingActor(SamtyckestjanstUtil.buildAccessingActorType(vgHsaId, veHsaId, userHsaId));

        return service.checkConsent(logicalAddress, checkConsentType);
    }

}
