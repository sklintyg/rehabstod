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
package se.inera.intyg.rehabstod.web.controller.api;

import static org.mockito.Mockito.verify;

import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.rehabstod.service.sjukfall.GetActiveSickLeavesService;
import se.inera.intyg.rehabstod.service.sjukfall.GetSickLeaveSummaryService;
import se.inera.intyg.rehabstod.service.sjukfall.PopulateFiltersService;
import se.inera.intyg.rehabstod.web.controller.api.dto.SickLeavesFilterRequestDTO;

@ExtendWith(MockitoExtension.class)
public class SickLeaveControllerTest {

    @Mock
    private GetActiveSickLeavesService getActiveSickLeavesService;
    @Mock
    private PopulateFiltersService populateFiltersService;
    @Mock
    private GetSickLeaveSummaryService getSickLeaveSummaryService;

    @InjectMocks
    private SickLeaveController sickLeaveController = new SickLeaveController();

    @Test
    void shouldCallGetActiveSickLeavesService() {
        final var expectedRequest =
            new SickLeavesFilterRequestDTO(Collections.singletonList("doctorId"), 1, 365, Collections.emptyList(), 1, 150);
        sickLeaveController.getSickLeavesForUnit(expectedRequest);
        verify(getActiveSickLeavesService).get(expectedRequest, true);
    }

    @Test
    void shouldCallPopulateFiltersService() {
        sickLeaveController.populateFilters();
        verify(populateFiltersService).get();
    }

    @Test
    void shouldCallGetSickLeaveSummaryService() {
        sickLeaveController.getSummary();
        verify(getSickLeaveSummaryService).get();
    }
}
