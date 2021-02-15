/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import se.inera.intyg.infra.integration.hsatk.model.legacy.Mottagning;
import se.inera.intyg.infra.integration.hsatk.model.legacy.Vardenhet;
import se.inera.intyg.infra.integration.hsatk.model.legacy.Vardgivare;
import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.integration.samtyckestjanst.service.SamtyckestjanstIntegrationService;
import se.inera.intyg.schemas.contract.Personnummer;

@RunWith(MockitoJUnitRunner.class)
public class ConsentServiceTest {

    private static final String PERSON_ID = "19121212-1212";
    private static final String VARDGIVARE_ID = "VG123";
    private static final String VARDENHETS_ID = "VG123-VEA";
    private static final String MOTTAGNINGS_ID = "VG123-VEA-E1";
    private static final String USER_HSA_ID = "USERHSA";

    @Mock
    private RehabstodUser rehabstodUserMock;

    @Mock
    private SamtyckestjanstIntegrationService samtyckestjanstIntegrationService;

    @InjectMocks
    private ConsentServiceImpl testee = new ConsentServiceImpl();


    @Test
    public void testGiveConsentLoggedInOnCareUnit() {
        LocalDateTime consentFrom = LocalDateTime.now();
        LocalDateTime consentTo = consentFrom.plusDays(10);

        Optional<Personnummer> personnummer = Personnummer.createPersonnummer(PERSON_ID);

        when(rehabstodUserMock.getValdVardgivare()).thenReturn(new Vardgivare(VARDGIVARE_ID, "vårdgivare"));
        when(rehabstodUserMock.getValdVardenhet()).thenReturn(new Vardenhet(VARDENHETS_ID, "enhet"));
        when(rehabstodUserMock.getHsaId()).thenReturn(USER_HSA_ID);

        testee.giveConsent(personnummer.get(), true, null, consentFrom, consentTo, rehabstodUserMock);

        verify(samtyckestjanstIntegrationService)
            .registerConsent(eq(VARDGIVARE_ID), eq(VARDENHETS_ID), eq(personnummer.get()), eq(USER_HSA_ID), eq(null), eq(consentFrom),
                eq(consentTo), any());
    }

    @Test
    public void testGiveConsentLoggedInOnSubUnit() {
        LocalDateTime consentFrom = LocalDateTime.now();
        LocalDateTime consentTo = consentFrom.plusDays(10);

        Optional<Personnummer> personnummer = Personnummer.createPersonnummer(PERSON_ID);
        Mottagning mottagning = new Mottagning(MOTTAGNINGS_ID, "enhet");
        mottagning.setParentHsaId(VARDENHETS_ID);

        when(rehabstodUserMock.getValdVardgivare()).thenReturn(new Vardgivare(VARDGIVARE_ID, "vårdgivare"));
        when(rehabstodUserMock.getValdVardenhet()).thenReturn(mottagning);
        when(rehabstodUserMock.getHsaId()).thenReturn(USER_HSA_ID);

        testee.giveConsent(personnummer.get(), true, null, consentFrom, consentTo, rehabstodUserMock);

        verify(samtyckestjanstIntegrationService)
            .registerConsent(eq(VARDGIVARE_ID), eq(VARDENHETS_ID), eq(personnummer.get()), eq(USER_HSA_ID), eq(null), eq(consentFrom),
                eq(consentTo), any());
    }
}
