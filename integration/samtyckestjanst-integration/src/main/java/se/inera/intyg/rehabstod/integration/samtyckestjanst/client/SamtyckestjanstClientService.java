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

import se.riv.informationsecurity.authorization.consent.CheckConsentResponder.v2.CheckConsentResponseType;

/**
 * Created by Magnus Ekstrand 2018-10-10.
 */
public interface SamtyckestjanstClientService {

    /**
     * Tjänst som kontrollerar om det finns ett giltigt samtycke, alternativt intyg om nödsituation,
     * gällande åtkomst för viss aktör (vårdenhet eller medarbetare). Med giltigt samtycke avses ett
     * samtycke som fortfarande är giltigt (giltigt t o m har ej passerats), ej makulerat eller återkallat.
     *
     * Om ett giltigt intyg gällande åtkomst för angiven aktör hittas, kommer tjänsten att svara OK.
     *
     * @param vgHsaId Id på medarbetarens vårdgivare enligt aktuellt medarbetaruppdrag.
     * @param veHsaId Id på medarbetarens vårdenhet enligt aktuellt medarbetaruppdrag.
     * @param userHsaId Id för medarbetaren/personen.
     * @param patientId Personidentitet på patienten vars samtycke skall kontrolleras.
     * @return Status för om ett giltigt intyg gällande åtkomst för angiven aktör hittades.
     */
    CheckConsentResponseType checkConsent(String vgHsaId, String veHsaId, String userHsaId, String patientId);

}
