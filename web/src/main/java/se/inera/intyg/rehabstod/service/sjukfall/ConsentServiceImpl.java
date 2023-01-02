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
package se.inera.intyg.rehabstod.service.sjukfall;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.integration.hsatk.model.legacy.Mottagning;
import se.inera.intyg.infra.integration.hsatk.model.legacy.SelectableVardenhet;
import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.integration.samtyckestjanst.service.SamtyckestjanstIntegrationService;
import se.inera.intyg.schemas.contract.Personnummer;
import se.riv.informationsecurity.authorization.consent.v2.ActionType;
import se.riv.informationsecurity.authorization.consent.v2.ActorType;

/**
 * @author Magnus Ekstrand on 2018-10-25.
 */
@Service("consentService")
public class ConsentServiceImpl implements ConsentService {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());

    @Autowired
    private SamtyckestjanstIntegrationService samtyckestjanstIntegrationService;

    @Override
    public LocalDateTime giveConsent(Personnummer personnummer, boolean onlyCurrentUser, String representedBy,
        LocalDateTime consentFrom, LocalDateTime consentTo, RehabstodUser user) {

        LocalDateTime registrationDate = LocalDateTime.now();
        ActionType registrationAction = createActionType(user, registrationDate);

        String userHsaId = onlyCurrentUser ? user.getHsaId() : null;
        String vgHsaId = user.getValdVardgivare().getId();
        String veHsaId = getCareUnit(user);

        try {
            LOG.debug("Calling Samtyckestjänsten - registering consent.");
            samtyckestjanstIntegrationService.registerConsent(vgHsaId, veHsaId,
                personnummer, userHsaId, representedBy,
                consentFrom, consentTo, registrationAction);

        } catch (Exception e) {
            LOG.error("INTEGRATION_CONSENT_SERVICE: Fatal error - message is '{}'", e.getMessage());
            registrationDate = null;
        }

        return registrationDate;
    }

    private String getCareUnit(RehabstodUser user) {
        final SelectableVardenhet unit = user.getValdVardenhet();
        if (unit instanceof Mottagning) {
            return ((Mottagning) unit).getParentHsaId();
        }
        return unit.getId();
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
