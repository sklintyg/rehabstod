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

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.IntygsData;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Magnus Ekstrand on 2016-02-15.
 */
public class InternalIntygsDataCreator {

    private static final Logger LOG = LoggerFactory.getLogger(InternalIntygsDataCreator.class);


    // - - - API - - -

    public Map<String, List<InternalIntygsData>> create(List<IntygsData> intygsData, LocalDate aktivtDatum) {
        LOG.debug("Start creating a map with storing application specific certificate information...");

        Map<String, List<InternalIntygsData>> map;

        map = createMap(intygsData, aktivtDatum);
        map = reduceMap(map);
        map = sortValues(map);
        map = setActive(map);

        LOG.debug("...stop creating a map with storing application specific certificate information.");
        return map;
    }


    // - - - Package scope - - -

    Map<String, List<InternalIntygsData>> createMap(List<IntygsData> intygsData, LocalDate aktivtDatum) {
        LOG.debug("  1. Create the map");

        Map<String, List<InternalIntygsData>> map = new HashMap<>();

        for (IntygsData i : intygsData) {
            String k = i.getPatient().getPersonId().getExtension().trim();

            if (map.get(k) == null) {
                map.put(k, new ArrayList<>());
            }

            InternalIntygsData v = new InternalIntygsData.InternalIntygsDataBuilder(i, aktivtDatum).build();
            map.get(k).add(v);
        }

        return map;
    }

    Map<String, List<InternalIntygsData>> reduceMap(Map<String, List<InternalIntygsData>> map) {
        LOG.debug("  2. Reduce map - filter out each entry where there is no active certificate.");

        return map.entrySet().stream()
                .filter(e -> e.getValue().stream()
                        .filter(o -> o.isAktivtIntyg()).count() > 0)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Method returns a map with sorted values. The sorting is done on
     * IntygsData objects' slutDatum. Objects are arranged in ascending order,
     * i.e object with biggest slutDatum will be last.
     *
     * @param unsortedMap
     * @return
     */
    Map<String, List<InternalIntygsData>> sortValues(Map<String, List<InternalIntygsData>> unsortedMap) {
        LOG.debug("  3. Sort map - sort each entry by its end date using ascending order.");

        // Lambda comparator
        Comparator<InternalIntygsData> endDateComparator = (o1, o2) -> o1.getSlutDatum().compareTo(o2.getSlutDatum());

        return unsortedMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        e -> e.getValue().stream()
                                .sorted(endDateComparator)
                                .collect(Collectors.toList())));
    }

    Map<String, List<InternalIntygsData>> setActive(Map<String, List<InternalIntygsData>> map) {
        LOG.debug("  4. Set the active certificate - there can be only one active certificate, find it and make it active.");

        return map.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.setValue(setActive(e.getValue())).stream()
                                .collect(Collectors.toList())));
    }

    List<InternalIntygsData> setActive(List<InternalIntygsData> intygsDataList) {
        List<InternalIntygsData> values = new ArrayList<>();

        int aktivtIntygIndex = 0;
        InternalIntygsData aktivtIntyg = null;

        ListIterator<InternalIntygsData> iterator = intygsDataList.listIterator();
        while (iterator.hasNext()) {
            int currentIndex = iterator.nextIndex();
            InternalIntygsData current = iterator.next();

            // Add current object to new list
            values.add(current);

            if (current.isAktivtIntyg()) {

                if (aktivtIntyg == null) {
                    aktivtIntyg = current;
                    aktivtIntygIndex = currentIndex;
                    continue;
                }

                LocalDateTime dtAktivt = aktivtIntyg.getSigneringsTidpunkt();
                LocalDateTime dtCurrent = current.getSigneringsTidpunkt();

                if (dtAktivt.isBefore(dtCurrent)) {
                    // Change active status
                    aktivtIntyg.setAktivtIntyg(false);
                    // Update new list
                    values.set(aktivtIntygIndex, aktivtIntyg);
                    // Swap
                    aktivtIntyg = current;
                    aktivtIntygIndex = currentIndex;
                } else {
                    // Change active status
                    current.setAktivtIntyg(false);
                    // Update new list
                    values.set(currentIndex, current);
                }
            }
        }

        return values;
    }

}
