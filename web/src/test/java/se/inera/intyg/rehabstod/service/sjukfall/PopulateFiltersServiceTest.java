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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.infra.integration.hsatk.model.legacy.Mottagning;
import se.inera.intyg.infra.integration.hsatk.model.legacy.SelectableVardenhet;
import se.inera.intyg.infra.integration.hsatk.model.legacy.Vardenhet;
import se.inera.intyg.infra.integration.hsatk.model.legacy.Vardgivare;
import se.inera.intyg.infra.sjukfall.dto.Lakare;
import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.auth.RehabstodUserPreferences;
import se.inera.intyg.rehabstod.auth.RehabstodUserPreferences.Preference;
import se.inera.intyg.rehabstod.integration.it.dto.PopulateFiltersRequestDTO;
import se.inera.intyg.rehabstod.integration.it.dto.PopulateFiltersResponseDTO;
import se.inera.intyg.rehabstod.integration.it.service.IntygstjanstRestIntegrationService;
import se.inera.intyg.rehabstod.service.user.UserService;

@ExtendWith(MockitoExtension.class)
public class PopulateFiltersServiceTest {

    @Mock
    UserService userService;

    @Mock
    IntygstjanstRestIntegrationService intygstjanstRestIntegrationService;

    @InjectMocks
    PopulateFiltersServiceImpl populateActiveFilters;

    static final RehabstodUser user = mock(RehabstodUser.class);
    static final String HSA_ID = "HSA_ID";
    static final String SUB_UNIT_ID = "SUB_UNIT_ID";
    static final String UNIT_ID = "UNIT_ID";
    static final String DOCTOR_NAME = "DOCTOR_NAME";
    static final String gap = "5";
    static final String days = "10";
    static final PopulateFiltersRequestDTO expectedRequest = new PopulateFiltersRequestDTO();

    Vardenhet unit;
    SelectableVardenhet careGiverUnit;
    Vardgivare careGiver;

    @BeforeEach
    void setup() {
        careGiverUnit = mock(SelectableVardenhet.class);
        careGiver = mock(Vardgivare.class);
        unit = mock(Vardenhet.class);

        when(userService.getUser()).thenReturn(user);
        when(user.getHsaId()).thenReturn(HSA_ID);
        when(user.getValdVardgivare()).thenReturn(careGiverUnit);
        when(user.getVardgivare()).thenReturn(Collections.singletonList(careGiver));

        final var preferences = new HashMap<String, String>();
        preferences.put(Preference.MAX_ANTAL_DAGAR_MELLAN_INTYG.getBackendKeyName(), gap);
        preferences.put(Preference.MAX_ANTAL_DAGAR_SEDAN_SJUKFALL_AVSLUT.getBackendKeyName(), days);
        final var userPreferences = RehabstodUserPreferences.fromBackend(preferences);
        when(user.getPreferences()).thenReturn(userPreferences);

        final var response = new PopulateFiltersResponseDTO(Collections.singletonList(Lakare.create(HSA_ID, DOCTOR_NAME)));
        when(intygstjanstRestIntegrationService.getPopulatedFiltersForActiveSickLeaves(any())).thenReturn(response);

        expectedRequest.setMaxCertificateGap(Integer.parseInt(days));
        expectedRequest.setMaxDaysSinceSickLeaveCompleted(Integer.parseInt(days));
        expectedRequest.setUnitId(UNIT_ID);
    }

    @Nested
    class TestITRequest {
        @Test
        void shouldCreateRequestWithCorrectValuesWhenChosenUnit() {
            when(user.getValdVardenhet()).thenReturn(unit);
            when(unit.getId()).thenReturn(UNIT_ID);

            final var captor = ArgumentCaptor.forClass(PopulateFiltersRequestDTO.class);
            populateActiveFilters.get();

            verify(intygstjanstRestIntegrationService).getPopulatedFiltersForActiveSickLeaves(captor.capture());
            Assertions.assertNull(captor.getValue().getCareUnitId());
            assertEquals(UNIT_ID, captor.getValue().getUnitId());
            assertEquals(Integer.parseInt(gap), captor.getValue().getMaxCertificateGap());
            assertEquals(Integer.parseInt(days), captor.getValue().getMaxDaysSinceSickLeaveCompleted());
        }

        @Test
        void shouldCreateRequestWithCorrectValuesWhenChosenSubUnit() {
            setupSubUnit();

            final var captor = ArgumentCaptor.forClass(PopulateFiltersRequestDTO.class);
            populateActiveFilters.get();

            verify(intygstjanstRestIntegrationService).getPopulatedFiltersForActiveSickLeaves(captor.capture());
            assertEquals(UNIT_ID, captor.getValue().getUnitId());
            assertEquals(SUB_UNIT_ID, captor.getValue().getCareUnitId());
            assertEquals(Integer.parseInt(gap), captor.getValue().getMaxCertificateGap());
            assertEquals(Integer.parseInt(days), captor.getValue().getMaxDaysSinceSickLeaveCompleted());
        }
    }

    @Nested
    class TestITResponse {

        @Test
        void shouldConvertResponseToCorrectFormat() {
            when(user.getValdVardenhet()).thenReturn(unit);
            when(unit.getId()).thenReturn(UNIT_ID);

            final var response = populateActiveFilters.get();
            assertEquals(1, response.getActiveDoctors().size());
            assertEquals(HSA_ID, response.getActiveDoctors().get(0).getHsaId());
            assertEquals(DOCTOR_NAME, response.getActiveDoctors().get(0).getNamn());
        }
    }

    private void setupSubUnit() {
        when(user.isValdVardenhetMottagning()).thenReturn(true);

        final var subUnit = mock(SelectableVardenhet.class);
        final var mottagning = mock(Mottagning.class);
        when(mottagning.getId()).thenReturn(SUB_UNIT_ID);
        when(subUnit.getId()).thenReturn(SUB_UNIT_ID);
        when(user.getValdVardenhet()).thenReturn(subUnit);

        when(careGiver.getVardenheter()).thenReturn(Collections.singletonList(unit));
        when(unit.getMottagningar()).thenReturn(Collections.singletonList(mottagning));
        when(unit.getId()).thenReturn(UNIT_ID);
    }
}
