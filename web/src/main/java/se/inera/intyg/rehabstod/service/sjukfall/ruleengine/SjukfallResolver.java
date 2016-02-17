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
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.inera.intyg.rehabstod.web.model.Sjukfall;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.IntygsData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Magnus Ekstrand on 10/02/16.
 */
public class SjukfallResolver {

    private static final Logger LOG = LoggerFactory.getLogger(SjukfallResolver.class);

    private SjukfallMapper mapper;


    public SjukfallResolver(SjukfallMapper mapper) {
        this.mapper = mapper;
    }

    // API

    public List<Sjukfall> resolve(List<IntygsData> intygsData, int maxIntygsGlapp, LocalDate aktivtDatum) {

        if (intygsData == null || intygsData.size() == 0) {
            LOG.debug("There was no in-data! Returning empty list");
            return new ArrayList<Sjukfall>();
        }

        if (maxIntygsGlapp < 0) {
            LOG.debug("Maximal days between certificates was {}. Value must be equal or greater than zero", maxIntygsGlapp);
            return new ArrayList<Sjukfall>();
        }

        // Create an map with personnummer as key holding each person's intygsdata
        Map<String, List<SortableIntygsData>> map = toMap(intygsData, aktivtDatum);

        // Create Sjukfall objects
        List<Sjukfall> sjukfallList = assemble(map, maxIntygsGlapp, aktivtDatum);

        return sjukfallList;
    }


    // Package scope

    List<Sjukfall> assemble(Map<String, List<SortableIntygsData>> map, int maxIntygsGlapp, LocalDate aktivtDatum) {

        List<Sjukfall> list = new ArrayList<>();

        // Sort map values by slutDatum. Biggest first
        Map<String, List<SortableIntygsData>> sortedMap = toSortedMap(map);

        // Loop through each patient's entry and decide which intyg to choose

        // 1. Which intyg is active
        sortedMap.entrySet().stream().forEach(e -> e.setValue(setActive(e.getValue())));

        // 2. Remove intyg that is not within 'maxIntygsGlapp'
        sortedMap.entrySet().stream().forEach(e -> e.setValue(reduce(e.getValue(), maxIntygsGlapp)));

        // 3.
        //mapper.map()
        return list;
    }

    List<SortableIntygsData> setActive(final List<SortableIntygsData> sortedList) {
        boolean found = false;
        SortableIntygsData[] arr = sortedList.toArray(new SortableIntygsData[sortedList.size()]);

        for (int i = 0; i < arr.length; i++) {
            // If the active certificate is already found, set the rest to false
            if (found) {
                arr[i].setAktivtIntyg(false);
                continue;
            }

            if (arr[i].isAktivtIntyg()) {
                // lookahead to decide if current object shall be the active one
                if (i < arr.length - 1 && arr[i + 1].isAktivtIntyg()) {
                    // Next object is also set to be active, use
                    // signeringsTidpunkt to decide which object that shall be active
                    LocalDateTime d1 = arr[i].getSigneringsTidpunkt();
                    LocalDateTime d2 = arr[i + 1].getSigneringsTidpunkt();
                    if (d1.isAfter(d2)) {
                        arr[i + 1].setAktivtIntyg(false);
                    } else {
                        arr[i].setAktivtIntyg(false);
                    }
                }

                found = true;
            }
        }

        return Arrays.asList(arr);
    }

    List<SortableIntygsData> reduce(final List<SortableIntygsData> sortedList, int maxIntygsGlapp) {

        return sortedList;

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

    /**
     * Method returns a map with sorted intermediate IntygsData objects.
     * Sorting is done on IntygsData objects' slutDatum. Objects are arranged
     * in descending order, i.e object with biggest slutDatum first.
     *
     * Patient's personal-id is used as key.
     *
     * @param unsortedMap
     * @return
     */
    Map<String, List<SortableIntygsData>> toSortedMap(Map<String, List<SortableIntygsData>> unsortedMap) {
        // Lambda comparator
        Comparator<SortableIntygsData> endDateComparator = (o1, o2) -> o1.getSlutDatum().compareTo(o2.getSlutDatum());

        Map<String, List<SortableIntygsData>> sortedMap = unsortedMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        e -> e.getValue().stream()
                                .sorted(endDateComparator.reversed())
                                .collect(Collectors.toList())));

        return sortedMap;
    }

}
