/*
 * Copyright (C) 2024 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.service.sjukfall.statistics;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.inera.intyg.rehabstod.service.diagnos.DiagnosGruppLoader;
import se.inera.intyg.rehabstod.service.diagnos.dto.DiagnosGrupp;
import se.inera.intyg.rehabstod.service.sjukfall.SjukfallServiceException;
import se.inera.intyg.rehabstod.service.sjukfall.dto.DiagnosGruppStat;
import se.inera.intyg.rehabstod.service.sjukfall.dto.GenderStat;
import se.inera.intyg.rehabstod.service.sjukfall.dto.SickLeaveDegreeStat;
import se.inera.intyg.rehabstod.service.sjukfall.dto.SickLeaveLengthStat;
import se.inera.intyg.rehabstod.service.sjukfall.dto.SickLeaveSummary;
import se.inera.intyg.rehabstod.web.model.Gender;
import se.inera.intyg.rehabstod.web.model.SjukfallEnhet;

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
    public SickLeaveSummary getSickLeaveSummary(List<SjukfallEnhet> sickLeave) {

        int total = sickLeave.size();

        final var genderStat = calculateGenderStat(sickLeave);

        final var diagnosisGroups = calculateGroupStatistics(sickLeave);
        final var maleDiagnosisGroups = calculateGroupStatistics(filterSickLeavesByGender(sickLeave, Gender.M));
        final var femaleDiagnosisGroups = calculateGroupStatistics(filterSickLeavesByGender(sickLeave, Gender.F));

        final var sickLeaveDegrees = filterEmptyPercentages(
            calculateSickLeaveDegrees(sickLeave)
        );
        final var maleSickLeaveDegrees = filterEmptyPercentages(
            calculateSickLeaveDegrees(filterSickLeavesByGender(sickLeave, Gender.M))
        );
        final var femaleSickLeaveDegrees = filterEmptyPercentages(
            calculateSickLeaveDegrees(filterSickLeavesByGender(sickLeave, Gender.F))
        );

        final var countSickLeaveDegrees = countSickLeaveDegrees(sickLeave);
        final var countMaleSickLeaveDegrees = countSickLeaveDegrees(filterSickLeavesByGender(sickLeave, Gender.M));
        final var countFemaleSickLeaveDegrees = countSickLeaveDegrees(filterSickLeavesByGender(sickLeave, Gender.F));

        final var sickLeaveLength = calculateSickLeaveLength(sickLeave);
        final var maleSickLeaveLength = calculateSickLeaveLength(filterSickLeavesByGender(sickLeave, Gender.M));
        final var femaleSickLeaveLength = calculateSickLeaveLength(filterSickLeavesByGender(sickLeave, Gender.F));

        return new SickLeaveSummary(
            total,
            genderStat,
            diagnosisGroups,
            maleDiagnosisGroups,
            femaleDiagnosisGroups,
            sickLeaveDegrees,
            maleSickLeaveDegrees,
            femaleSickLeaveDegrees,
            countSickLeaveDegrees,
            countMaleSickLeaveDegrees,
            countFemaleSickLeaveDegrees,
            sickLeaveLength,
            maleSickLeaveLength,
            femaleSickLeaveLength
        );

    }

    private List<SickLeaveDegreeStat> filterEmptyPercentages(List<SickLeaveDegreeStat> list) {
        return list
            .stream()
            .filter((item) -> item.getPercentage() > 0 && item.getCount() > 0)
            .collect(Collectors.toList());
    }

    private List<SickLeaveDegreeStat> countSickLeaveDegrees(List<SjukfallEnhet> sickLeaves) {
        final var bySickLeaveDegreeCount = sickLeaves
            .stream()
            .filter((sickLeave) -> sickLeave.getGrader() != null)
            .collect(Collectors.groupingBy((sickLeave) -> sickLeave.getGrader().size() == 1));

        return bySickLeaveDegreeCount
            .entrySet()
            .stream()
            .map((entry) -> new SickLeaveDegreeStat(
                entry.getKey() ? 1 : 2,
                entry.getKey() ? "En" : "Flera",
                entry.getValue().size(),
                calculatePercentage(entry.getValue().size(), sickLeaves.size())))
            .filter((item) -> item.getPercentage() > 0 && item.getCount() > 0)
            .sorted(Comparator.comparing(SickLeaveDegreeStat::getName))
            .collect(Collectors.toList());

    }

    private List<SjukfallEnhet> filterOnSickLeaveLength(List<SjukfallEnhet> sickLeaves, Predicate<SjukfallEnhet> filter) {
        return sickLeaves
            .stream()
            .filter(filter)
            .collect(Collectors.toList());
    }

    private List<SickLeaveLengthStat> calculateSickLeaveLength(List<SjukfallEnhet> sickLeaves) {
        final var firstLimit =
            filterOnSickLeaveLength(sickLeaves, (sickLeave) -> sickLeave.getDagar() < 90);
        final var secondLimit =
            filterOnSickLeaveLength(sickLeaves, (sickLeave) -> sickLeave.getDagar() >= 90 && sickLeave.getDagar() <= 180);
        final var thirdLimit =
            filterOnSickLeaveLength(sickLeaves, (sickLeave) -> sickLeave.getDagar() <= 365 && sickLeave.getDagar() > 180);
        final var fourthLimit =
            filterOnSickLeaveLength(sickLeaves, (sickLeave) -> sickLeave.getDagar() > 365);

        return Stream.of(
                getStatForSickLeaveLength(firstLimit, 1, sickLeaves.size()),
                getStatForSickLeaveLength(secondLimit, 2, sickLeaves.size()),
                getStatForSickLeaveLength(thirdLimit, 3, sickLeaves.size()),
                getStatForSickLeaveLength(fourthLimit, 4, sickLeaves.size())
            )
            .filter((item) -> item.getPercentage() > 0 && item.getCount() > 0)
            .collect(Collectors.toList());
    }

    private SickLeaveLengthStat getStatForSickLeaveLength(List<SjukfallEnhet> sickLeaves, int id, int totalCount) {
        return new SickLeaveLengthStat(
            id,
            getSickLeaveLengthName(id),
            sickLeaves.size(),
            calculatePercentage(sickLeaves.size(), totalCount)
        );
    }

    private String getSickLeaveLengthName(int id) {
        switch (id) {
            case 1:
                return "< 90";
            case 2:
                return "Dag 90-180";
            case 3:
                return "Dag 181-365";
            case 4:
            default:
                return "> 365";
        }
    }

    private List<SickLeaveDegreeStat> calculateSickLeaveDegrees(List<SjukfallEnhet> sjukfall) {
        Map<Integer, List<SjukfallEnhet>> byGrad = sjukfall.stream()
            .collect(Collectors.groupingBy(SjukfallEnhet::getAktivGrad));
        return byGrad.entrySet().stream()
            .map(entry -> new SickLeaveDegreeStat(entry.getKey(), "" + entry.getKey() + " %",
                entry.getValue().size(), calculatePercentage(entry.getValue().size(), sjukfall.size())))
            .sorted(Comparator.comparingInt(SickLeaveDegreeStat::getId))
            .collect(Collectors.toList());
    }

    private List<GenderStat> calculateGenderStat(List<SjukfallEnhet> sjukfall) {
        Map<Gender, List<SjukfallEnhet>> byGender = sjukfall.stream().collect(Collectors.groupingBy(s -> s.getPatient().getKon()));

        int menTotal = byGender.getOrDefault(Gender.M, Collections.emptyList()).size();
        int womenTotal = byGender.getOrDefault(Gender.F, Collections.emptyList()).size();

        return Arrays.asList(
            new GenderStat(Gender.F, womenTotal, calculatePercentage(womenTotal, (womenTotal + menTotal))),
            new GenderStat(Gender.M, menTotal, calculatePercentage(menTotal, (womenTotal + menTotal))));
    }

    private List<SjukfallEnhet> filterSickLeavesByGender(List<SjukfallEnhet> sickLeaves, Gender gender) {
        return sickLeaves
            .stream()
            .filter((sickLeave) -> sickLeave.getPatient().getKon() == gender)
            .collect(Collectors.toList());
    }

    private List<DiagnosGruppStat> calculateGroupStatistics(List<SjukfallEnhet> sjukfall) {
        List<DiagnosGruppStat> stats = new ArrayList<>();

        long assignedToExistingGroupCount = 0;

        for (DiagnosGrupp grupp : diagnosGrupper) {
            long count = getAntalSjukfallPerDiagnosGrupp(grupp, sjukfall);
            if (count > 0) {
                stats.add(new DiagnosGruppStat(grupp, count, calculatePercentage(count, sjukfall.size())));
            }
            assignedToExistingGroupCount += count;
        }

        long sjukFallWithUnassignedCodeCount = sjukfall.size() - assignedToExistingGroupCount;
        if (sjukFallWithUnassignedCodeCount > 0) {
            stats.add(new DiagnosGruppStat(NON_MATCHING_GROUP, sjukFallWithUnassignedCodeCount,
                calculatePercentage(sjukFallWithUnassignedCodeCount, sjukfall.size())));
        }

        // Finally sort the stats in falling order
        return stats.stream().sorted((s1, s2) -> s2.getCount().compareTo(s1.getCount())).collect(Collectors.toList());

    }

    private long getAntalSjukfallPerDiagnosGrupp(DiagnosGrupp grupp, List<SjukfallEnhet> sjukfall) {
        return sjukfall.stream().filter(s -> grupp.includes(s.getDiagnos().getKod())).count();
    }

    private float calculatePercentage(long fraction, long total) {
        return total == 0 ? 0.0f : ((float) fraction / total) * 100;
    }

}