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
package se.inera.intyg.rehabstod.service.sjukfall;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.integration.samtyckestjanst.service.SamtyckestjanstIntegrationService;
import se.riv.informationsecurity.authorization.consent.v2.ActionType;
import se.riv.informationsecurity.authorization.consent.v2.ActorType;

/**
 * @author Magnus Ekstrand on 2018-10-25.
 */
@Service("consentService")
public class ConsentServiceImpl implements ConsentService {

    @Autowired
    private SamtyckestjanstIntegrationService samtyckestjanstIntegrationService;

    @Override
    public LocalDateTime giveConsent(String patientId, boolean onlyCurrentUser, String representedBy,
                                     LocalDateTime consentFrom, LocalDateTime consentTo, RehabstodUser user) {

        LocalDateTime registrationDate = LocalDateTime.now();
        ActionType registrationAction = createActionType(user, registrationDate);

        String userHsaId = onlyCurrentUser ? user.getHsaId() : null;
        String vgHsaId = user.getValdVardgivare().getId();
        String veHsaId = user.getValdVardenhet().getId();

        samtyckestjanstIntegrationService.registerConsent(vgHsaId, veHsaId,
                patientId, userHsaId, representedBy,
                consentFrom, consentTo, registrationAction);

        return registrationDate;
    }


    private ActionType createActionType(RehabstodUser user, LocalDateTime registrationDate) {
        ActionType registrationAction = new ActionType();
        registrationAction.setRegisteredBy(createActorType(user));
        registrationAction.setRegistrationDate(registrationDate);
        registrationAction.setRequestedBy(createActorType(user));
        registrationAction.setRequestDate(registrationDate);
        return registrationAction;
    }

    private ActorType createActorType(RehabstodUser user) {
        ActorType actorType = new ActorType();
        actorType.setEmployeeId(user.getHsaId());
        actorType.setAssignmentName(user.getSelectedMedarbetarUppdragNamn());
        return actorType;
    }
}
