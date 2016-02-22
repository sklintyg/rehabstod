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
package se.inera.intyg.rehabstod.service.sjukfall.ruleengine;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.IntygsData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Magnus Ekstrand on 10/02/16.
 */
@Component
public class SjukfallResolverImpl implements SjukfallResolver {

    private static final Logger LOG = LoggerFactory.getLogger(SjukfallResolverImpl.class);

    public SjukfallResolverImpl() {
    }


    // - - -  API  - - -

    @Override
    public Map<String, List<SortableIntygsData>> resolve(List<IntygsData> intygsData, int maxIntygsGlapp, LocalDate aktivtDatum) {

        if (intygsData == null || intygsData.size() == 0) {
            LOG.debug("There was no in-data! Returning empty list");
            return new HashMap<>();
        }

        if (maxIntygsGlapp < 0) {
            LOG.debug("Maximal days between certificates was {}. Value must be equal or greater than zero", maxIntygsGlapp);
            return new HashMap<>();
        }

        // Create an map with personnummer as key holding each person's intygsdata
        // The map's values are sorted by slutDatum with ascending order.
        Map<String, List<SortableIntygsData>> intygsDataMap = toMap(intygsData, aktivtDatum);

        // Reduce list of SortableIntygsData
        return reduceMap(intygsDataMap, maxIntygsGlapp);
    }


    // - - -  Package scope  - - -

    Map<String, List<SortableIntygsData>> reduceMap(Map<String, List<SortableIntygsData>> intygsDataMap, int maxIntygsGlapp) {

        Map<String, List<SortableIntygsData>> resultMap = new HashMap<>();

        // For each entry in map, lookup "aktivtIntyg" within the list of SortableIntygsData
        for (Map.Entry<String, List<SortableIntygsData>> entry : intygsDataMap.entrySet()) {

            List<SortableIntygsData> reducedList = reduceList(entry.getValue(), maxIntygsGlapp);
            if (reducedList.size() > 0) {
                resultMap.put(entry.getKey(), reducedList);
            }
        }

        return resultMap;
    }

    List<SortableIntygsData> reduceList(List<SortableIntygsData> values, int maxIntygsGlapp)  {

        // filter out "aktivtIntyg"
        SortableIntygsData aktivtIntyg = values.stream().filter(e -> e.isAktivtIntyg()).findFirst().get();

        // get position of the "aktivtIntyg"
        int aktivtIndex = values.indexOf(aktivtIntyg);

        // Slice "list of SortableIntygsData" into two lists, use "aktivtIntyg" as divider
        List<SortableIntygsData> left = new ArrayList<>();
        if (aktivtIndex > 0) {
            left = values.subList(0, aktivtIndex);
        }

        List<SortableIntygsData> right = new ArrayList<>();
        if (aktivtIndex < values.size() - 1) {
            right = values.subList(aktivtIndex + 1, values.size());
        }

        // traverse (1) right and (2) left sub lists and add intyg fulfilling "maxIntygsGlapp"
        right = reduceRight(right, maxIntygsGlapp, aktivtIntyg.getSlutDatum());

        // assure we have the smallest date as initial compare date when we call reduceLeft method
        left = reduceLeft(left, maxIntygsGlapp, getCompareDate(right, aktivtIntyg));

        // concatenate the reduced list
        List<SortableIntygsData> reducedList = new ArrayList<>();
        reducedList.addAll(left);
        reducedList.add(aktivtIntyg);
        reducedList.addAll(right);

        return reducedList;
    }

    List<SortableIntygsData> reduceRight(List<SortableIntygsData> right, int maxIntygsGlapp, LocalDate initialCompareDate) {
        // ensure right list is sorted by startDatum ascending order
        right.sort((o1, o2) -> o1.getStartDatum().compareTo(o2.getStartDatum()));

        List<SortableIntygsData> list = new ArrayList<>();
        LocalDate compareDate = initialCompareDate;

        for (SortableIntygsData nextRight : right) {
            LocalDate start = nextRight.getStartDatum();
            LocalDate lastValidStartDate = compareDate.plusDays(maxIntygsGlapp);

            if (lastValidStartDate.compareTo(start) > -1) {
                list.add(nextRight);
            } else {
                break;
            }

            compareDate = nextRight.getSlutDatum();
        }

        return list;
    }

    List<SortableIntygsData> reduceLeft(List<SortableIntygsData> left, int maxIntygsGlapp, LocalDate initialCompareDate) {
        List<SortableIntygsData> list = new ArrayList<>();
        LocalDate compareDate = initialCompareDate;

        for (int i = left.size() - 1; i >= 0; i--) {
            SortableIntygsData nextLeft = left.get(i);

            LocalDate end = nextLeft.getSlutDatum();
            LocalDate lastValidEndDate = compareDate.minusDays(maxIntygsGlapp);

            if (lastValidEndDate.compareTo(end) < 1) {
                list.add(0, nextLeft);
            } else {
                break;
            }

            compareDate = nextLeft.getStartDatum();
        }

        return list;
    }

    /**
     * Method returns a map with intermediate IntygsData objects.
     * Patient's personal-id is used as key.
     *
     * @param intygsData a list with basic IntygsData
     * @param aktivtDatum a date used for decision if a certificate is active or not
     * @return
     */
    Map<String, List<SortableIntygsData>> toMap(List<IntygsData> intygsData, LocalDate aktivtDatum) {
        Map<String, List<SortableIntygsData>> map = new HashMap();

        if (intygsData == null || intygsData.size() == 0) {
            return map;
        }

        SortableIntygsDataCreator creator = new SortableIntygsDataCreator();
        map = creator.create(intygsData, aktivtDatum);

        return map;
    }


    // - - -  Private scope  - - -

    private LocalDate getCompareDate(List<SortableIntygsData> right, SortableIntygsData aktivtIntyg) {
        LocalDate smallest = null;

        if (right == null || right.size() == 0) {
            return  aktivtIntyg.getStartDatum();
        } else if (right.size() == 1) {
            smallest = right.stream().findFirst().get().getStartDatum();
        } else {
            smallest = right.stream()
                    .min((o1, o2) -> o1.getStartDatum().compareTo(o2.getStartDatum()))
                    .get()
                    .getStartDatum();
        }

        if (smallest.isBefore(aktivtIntyg.getStartDatum())) {
            return smallest;
        }

        return aktivtIntyg.getStartDatum();
    }

}
