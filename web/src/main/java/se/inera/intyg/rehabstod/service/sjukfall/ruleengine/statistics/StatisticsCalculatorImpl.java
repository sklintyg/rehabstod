/**
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
 *
 * This file is part of rehabstod (https://github.com/sklintyg/rehabstod).
 *
 * rehabstod is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * rehabstod is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.intyg.rehabstod.service.sjukfall.ruleengine.statistics;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import se.inera.intyg.rehabstod.service.sjukfall.dto.SjukfallSummary;
import se.inera.intyg.rehabstod.web.model.Gender;
import se.inera.intyg.rehabstod.web.model.InternalSjukfall;

/**
 * Created by marced on 04/03/16.
 */
@Component
public class StatisticsCalculatorImpl implements StatisticsCalculator {

    private static final int PERCENTAGE_FACTOR = 100;

    @Override
    public SjukfallSummary getSjukfallSummary(List<InternalSjukfall> sjukfall) {

        int total = sjukfall.size();

        Map<Gender, List<InternalSjukfall>> byGender = sjukfall.stream().collect(Collectors.groupingBy(s -> s.getSjukfall().getPatient().getKon()));

        int menTotal = byGender.getOrDefault(Gender.M, Arrays.asList()).size();
        int womenTotal = byGender.getOrDefault(Gender.F, Arrays.asList()).size();

        // Note: Some of the items may have Geder.UNKNOWN, but the men/women stats are correct anyway
        double menPercentage = 0;
        double womenPercentage = 0;

        // calculate percentages
        if (total > 0) {
            menPercentage = (menTotal * 1.0 / total) * PERCENTAGE_FACTOR;
            womenPercentage = (womenTotal * 1.0 / total) * PERCENTAGE_FACTOR;
        }
        return new SjukfallSummary(total, menPercentage, womenPercentage);

    }
}
