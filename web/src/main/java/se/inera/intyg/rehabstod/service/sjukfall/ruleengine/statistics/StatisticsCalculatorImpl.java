/**
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.inera.intyg.rehabstod.service.diagnos.DiagnosGruppLoader;
import se.inera.intyg.rehabstod.service.diagnos.dto.DiagnosGrupp;
import se.inera.intyg.rehabstod.service.sjukfall.SjukfallServiceException;
import se.inera.intyg.rehabstod.service.sjukfall.dto.DiagnosGruppStat;
import se.inera.intyg.rehabstod.service.sjukfall.dto.GenderStat;
import se.inera.intyg.rehabstod.service.sjukfall.dto.SjukfallSummary;
import se.inera.intyg.rehabstod.web.model.Gender;
import se.inera.intyg.rehabstod.web.model.InternalSjukfall;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by marced on 04/03/16.
 */
@Component
public class StatisticsCalculatorImpl implements StatisticsCalculator {

    private static final Logger LOG = LoggerFactory.getLogger(StatisticsCalculatorImpl.class);

    public static final DiagnosGrupp NON_MATCHING_GROUP = new DiagnosGrupp("", new ArrayList<>(), "Utan giltig diagnoskod");

    private List<DiagnosGrupp> diagnosGrupper;

    @Autowired
    DiagnosGruppLoader diagnosGruppLoader;

    @PostConstruct
    public void init() {
        try {
            diagnosGrupper = diagnosGruppLoader.loadDiagnosGrupper();
        } catch (IOException e) {
            throw new SjukfallServiceException("Failed to load diagnosGruppList!", e);
        }
        LOG.info("Loaded " + diagnosGrupper.size() + " diagnosGrupper definitions");
    }

    @Override
    public SjukfallSummary getSjukfallSummary(List<InternalSjukfall> sjukfall) {

        int total = sjukfall.size();

        // Note: Some of the items may have Gender.UNKNOWN, but the men/women stats are correct anyway
        List<GenderStat> genderStat = calculateGenderStat(sjukfall);

        List<DiagnosGruppStat> grupper = calculateGroupStatistics(sjukfall);
        return new SjukfallSummary(total, genderStat, grupper);

    }

    private List<GenderStat> calculateGenderStat(List<InternalSjukfall> sjukfall) {
        Map<Gender, List<InternalSjukfall>> byGender = sjukfall.stream().collect(Collectors.groupingBy(s -> s.getSjukfall().getPatient().getKon()));

        int menTotal = byGender.getOrDefault(Gender.M, Collections.emptyList()).size();
        int womenTotal = byGender.getOrDefault(Gender.F, Collections.emptyList()).size();

        return Arrays.asList(new GenderStat(Gender.F, womenTotal), new GenderStat(Gender.M, menTotal));
    }

    private List<DiagnosGruppStat> calculateGroupStatistics(List<InternalSjukfall> sjukfall) {
        List<DiagnosGruppStat> stats = new ArrayList<>();

        long assignedToExistingGroupCount = 0;

        for (DiagnosGrupp grupp : diagnosGrupper) {
            long count = getAntalSjukfallPerDiagnosGrupp(grupp, sjukfall);
            if (count > 0) {
                stats.add(new DiagnosGruppStat(grupp, count));
            }
            assignedToExistingGroupCount += count;
        }

        long sjukFallWithUnassignedCodeCount = sjukfall.size() - assignedToExistingGroupCount;
        if (sjukFallWithUnassignedCodeCount > 0) {
            stats.add(new DiagnosGruppStat(NON_MATCHING_GROUP, sjukFallWithUnassignedCodeCount));
        }

        // Finally sort the stats in falling order
        return stats.stream().sorted((s1, s2) -> s2.getCount().compareTo(s1.getCount())).collect(Collectors.toList());

    }

    private long getAntalSjukfallPerDiagnosGrupp(DiagnosGrupp grupp, List<InternalSjukfall> sjukfall) {
        return sjukfall.stream().filter(s -> grupp.includes(s.getSjukfall().getDiagnos().getKod())).count();
    }
}
