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
package se.inera.intyg.rehabstod.auth.pdl;

import se.inera.intyg.infra.logmessages.ActivityType;
import se.inera.intyg.rehabstod.web.model.InternalSjukfall;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A simple Map based store to cache combinations of vardenhet/patient/ActivityType
 * Created by marced on 22/02/16.
 */
public final class PDLActivityStore {

    private PDLActivityStore() {
    }

    /**
     * Should return list of sjukfall (internally identified by patient) not already present in store for this vardenhet
     * and activityType.
     *
     * @param enhetsId
     * @param sjukfall
     * @param activityType
     * @param storedActivities the map holding sjukfall
     *
     * @return a list of InternalSjukfall
     */    public static List<InternalSjukfall> getActivitiesNotInStore(String enhetsId, List<InternalSjukfall> sjukfall, ActivityType activityType, Map<String, List<PDLActivityEntry>> storedActivities) {
        if (sjukfall == null || sjukfall.isEmpty()) {
            return new ArrayList<>();
        }

        if (storedActivities == null || storedActivities.isEmpty()) {
            return sjukfall;
        }

        // We actually don't check tha vardenehet for each sjukfall, we trust that the given enhetsId is correct.
        List<PDLActivityEntry> vardenhetEvents = storedActivities.get(enhetsId);

        if (vardenhetEvents == null) {
            // Nothing logged for this vardenhet yet - so all are new activities
            return sjukfall;
        }

        // find all patientId's NOT having av event entry for the combination patientId + eventType
        return sjukfall.stream()
                .filter(sf -> vardenhetEvents.stream()
                        .noneMatch(storedEvent -> storedEvent.getPatientId().equals(sf.getSjukfall().getPatient().getId())
                                && storedEvent.getActivityType().equals(activityType)))
                .collect(Collectors.toList());

    }

    /**
     * Should store the specified sjukfall for the vardenhet and activityType.
     *
     * @param enhetsId
     * @param sjukfallToAdd
     * @param activityType
     * @param storedActivities the map where sjukfall will be stored
     */
    public static void addActivitiesToStore(String enhetsId, List<InternalSjukfall> sjukfallToAdd, ActivityType activityType, Map<String, List<PDLActivityEntry>> storedActivities) {
        if (sjukfallToAdd == null || sjukfallToAdd.isEmpty()) {
            return;
        }

        List<PDLActivityEntry> vardenhetEvents = storedActivities.get(enhetsId);

        final List<PDLActivityEntry> pdlActivityEntryList = sjukfallToAdd.stream()
                .map(sf -> new PDLActivityEntry(sf.getSjukfall().getPatient().getId(), activityType))
                .collect(Collectors.toList());

        if (vardenhetEvents == null) {
            storedActivities.put(enhetsId, pdlActivityEntryList);
        } else {
            vardenhetEvents.addAll(pdlActivityEntryList);
        }
    }

}
