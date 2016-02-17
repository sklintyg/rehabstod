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
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.IntygsData;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Magnus Ekstrand on 2016-02-15.
 */
public class SortableIntygsDataCreator {

    public SortableIntygsDataCreator() {
    }

    public Map<String, List<SortableIntygsData>> create(List<IntygsData> intygsData, LocalDate aktivtDatum) {
        Map<String, List<SortableIntygsData>> map = new HashMap();

        for (IntygsData i : intygsData) {
            String k = i.getPatient().getPersonId().getExtension();
            if (map.get(k) == null) {
                map.put(k, new ArrayList<SortableIntygsData>());
            }

            SortableIntygsData v = SortableIntygsData.createInstance(i, aktivtDatum);
            map.get(k).add(v);
        }

        return sortValues(map);
    }

    Map<String, List<SortableIntygsData>> createMap(List<IntygsData> intygsData, LocalDate aktivtDatum) {
        Map<String, List<SortableIntygsData>> map = new HashMap();

        for (IntygsData i : intygsData) {
            String k = i.getPatient().getPersonId().getExtension();
            if (map.get(k) == null) {
                map.put(k, new ArrayList<>());
            }

            map.get(k).add(SortableIntygsData.createInstance(i, aktivtDatum));
        }

        return map;
    }

    /**
     * Method returns a map with sorted values. The sorting is done on
     * IntygsData objects' slutDatum. Objects are arranged in ascending order,
     * i.e object with biggest slutDatum will be last.
     *
     * @param unsortedMap
     * @return
     */
    Map<String, List<SortableIntygsData>> sortValues(Map<String, List<SortableIntygsData>> unsortedMap) {
        // Lambda comparator
        Comparator<SortableIntygsData> endDateComparator = (o1, o2) -> o1.getSlutDatum().compareTo(o2.getSlutDatum());

        Map<String, List<SortableIntygsData>> sortedMap = unsortedMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        e -> e.getValue().stream()
                                .sorted(endDateComparator)
                                .collect(Collectors.toList())));

        return sortedMap;
    }


}
