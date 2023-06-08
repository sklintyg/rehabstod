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

import org.springframework.stereotype.Service;
import se.inera.intyg.rehabstod.service.sjukfall.dto.UnansweredCommunicationFilterType;
import se.inera.intyg.rehabstod.web.model.SjukfallEnhet;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UnansweredCommunicationFilterServiceImpl implements UnansweredCommunicationFilterService {
    @Override
    public List<SjukfallEnhet> filter(List<SjukfallEnhet> sickLeaves, String filterTypeId) {
        return sickLeaves
                .stream()
                .filter((sickLeave) -> filterSickLeave(sickLeave, filterTypeId))
                .collect(Collectors.toList());
    }

    private boolean filterSickLeave(SjukfallEnhet sickLeave, String filterTypeId) {
        if (filterTypeId == null || filterTypeId.isBlank()) {
            return true;
        }

        final var convertedFilterTypeId = UnansweredCommunicationFilterType.fromId(filterTypeId);

        switch (convertedFilterTypeId) {
            case UNANSWERED_COMMUNICATION_FILTER_TYPE_1:
                return sickLeave.getUnansweredOther() + sickLeave.getObesvaradeKompl() == 0;
            case UNANSWERED_COMMUNICATION_FILTER_TYPE_2:
                return sickLeave.getUnansweredOther() + sickLeave.getObesvaradeKompl() > 0;
            case UNANSWERED_COMMUNICATION_FILTER_TYPE_3:
                return sickLeave.getObesvaradeKompl() > 0;
            case UNANSWERED_COMMUNICATION_FILTER_TYPE_4:
                return sickLeave.getUnansweredOther() > 0;
            default:
                return true;
        }
    }
}
