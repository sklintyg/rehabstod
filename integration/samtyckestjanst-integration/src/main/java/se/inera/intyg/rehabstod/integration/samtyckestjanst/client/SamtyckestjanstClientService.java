/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
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

import java.time.LocalDateTime;
import se.inera.intyg.schemas.contract.Personnummer;
import se.riv.informationsecurity.authorization.consent.CheckConsentResponder.v2.CheckConsentResponseType;
import se.riv.informationsecurity.authorization.consent.RegisterExtendedConsentResponder.v2.RegisterExtendedConsentResponseType;
import se.riv.informationsecurity.authorization.consent.v2.ActionType;

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
    CheckConsentResponseType checkConsent(String vgHsaId,
        String veHsaId,
        String userHsaId,
        String patientId);

    /**
     * Tjänst som registrerar ett intyg gällande viss patient som ger direktåtkomst till patientens information
     * från andra vårdgivare enligt PDL. Intyget avser patientens aktiva medgivande (samtycke), alternativt
     * nödsituation då HoS personal bedömer att behov av uppgifterna finns för nödvändig vård av patient som
     * inte kan ge aktivt medgivande.
     *
     * @param vgHsaId Id på medarbetarens vårdgivare enligt aktuellt medarbetaruppdrag.
     * @param veHsaId Id på medarbetarens vårdenhet enligt aktuellt medarbetaruppdrag.
     * @param userHsaId Användarens Hsa-id. Anges om användaren har uppgett att samtycket
     * endast ska gälla för denne och inte alla behöriga användare på vårdenheten.
     * @param patientId Personidentitet på patienten vars samtycke skall kontrolleras.
     * @param representedBy Personidentitet på företrädare/vårdnadshavare som företräder patienten
     * @param consentFrom Samtycket gäller fr.o.m. denna tidpunkt.
     * @param consentTo Samtycket gäller t.o.m. denna tidpunkt.
     * @param registrationAction Identifierar den användare som angivit samtycket.
     */
    // CHECKSTYLE:OFF ParameterNumber
    RegisterExtendedConsentResponseType registerExtendedConsent(String vgHsaId,
        String veHsaId,
        String userHsaId,
        Personnummer patientId,
        String representedBy,
        LocalDateTime consentFrom,
        LocalDateTime consentTo,
        ActionType registrationAction);
    // CHECKSTYLE:ON ParameterNumber
}
