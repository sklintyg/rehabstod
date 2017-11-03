/*
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.auth.pdl;

import se.inera.intyg.infra.logmessages.ActivityType;
import se.inera.intyg.infra.logmessages.ResourceType;
import se.inera.intyg.rehabstod.web.model.SjukfallEnhet;
import se.inera.intyg.rehabstod.web.model.SjukfallPatient;

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
     * Should return list of sjukfall (internally identified by patient) not already present in store
     * for this vardenhet, activityType and resourceType.
     *
     * @param enhetsId
     * @param sjukfall
     * @param activityType
     * @param storedActivities
     *
     * @return a list of SjukfallEnhet
     */
    public static List<SjukfallEnhet> getActivitiesNotInStore(String enhetsId,
                                                              List<SjukfallEnhet> sjukfall,
                                                              ActivityType activityType,
                                                              ResourceType resourceType,
                                                              Map<String, List<PDLActivityEntry>> storedActivities) {

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
                        .noneMatch(storedEvent -> isStoredEvent(storedEvent, sf.getPatient().getId(), activityType, resourceType)))
                .collect(Collectors.toList());

    }

    /**
     * Should return true or false if patient's sjukfall is in store or not.
     */
    public static boolean isActivityInStore(String enhetsId,
                                            SjukfallPatient sjukfall,
                                            ActivityType activityType,
                                            ResourceType resourceType,
                                            Map<String, List<PDLActivityEntry>> storedActivities) {

        if (sjukfall == null) {
            throw new IllegalArgumentException("Cannot make lookup in PDL activity store, sjukfall was null.");
        }

        if (storedActivities == null || storedActivities.isEmpty()) {
            return false;
        }

        // We actually don't check tha vardenehet for each sjukfall, we trust that the given enhetsId is correct.
        List<PDLActivityEntry> vardenhetEvents = storedActivities.get(enhetsId);
        if (vardenhetEvents == null) {
            // Nothing logged for this vardenhet yet - so all are new activities
            return false;
        }

        // find all patientId's NOT having av event entry for the combination patientId + activityType + resourceType
        String patientId = sjukfall.getIntyg().get(0).getPatient().getId();
        return vardenhetEvents.stream()
            .anyMatch(storedEvent -> isStoredEvent(storedEvent, patientId, activityType, resourceType));
    }

    /**
     * Should store the specified sjukfall for the vardenhet and activityType.
     *
     * @param enhetsId
     * @param sjukfallToAdd
     * @param activityType
     * @param storedActivities
     */
    public static void addActivitiesToStore(String enhetsId,
                                            List<SjukfallEnhet> sjukfallToAdd,
                                            ActivityType activityType,
                                            ResourceType resourceType,
                                            Map<String, List<PDLActivityEntry>> storedActivities) {
        if (sjukfallToAdd == null || sjukfallToAdd.isEmpty()) {
            return;
        }

        List<PDLActivityEntry> vardenhetEvents = storedActivities.get(enhetsId);

        final List<PDLActivityEntry> pdlActivityEntryList = sjukfallToAdd.stream()
                .map(sf -> new PDLActivityEntry(sf.getPatient().getId(), activityType, resourceType))
                .collect(Collectors.toList());

        if (vardenhetEvents == null) {
            storedActivities.put(enhetsId, pdlActivityEntryList);
        } else {
            vardenhetEvents.addAll(pdlActivityEntryList);
        }
    }

    /**
     * Should store the specified sjukfall for the vardenhet and activityType.
     *
     * @param enhetsId
     * @param sjukfallToAdd
     * @param activityType
     * @param storedActivities
     */
    public static void addActivityToStore(String enhetsId,
                                          SjukfallPatient sjukfallToAdd,
                                          ActivityType activityType,
                                          ResourceType resourceType,
                                          Map<String, List<PDLActivityEntry>> storedActivities) {
        if (sjukfallToAdd == null) {
            return;
        }

        List<PDLActivityEntry> vardenhetEvents = storedActivities.get(enhetsId);

        String patientId = sjukfallToAdd.getIntyg().get(0).getPatient().getId();
        PDLActivityEntry newEntry = new PDLActivityEntry(patientId, activityType, resourceType);

        if (vardenhetEvents == null) {
            ArrayList<PDLActivityEntry> list = new ArrayList<>();
            list.add(newEntry);
            storedActivities.put(enhetsId, list);
        } else {
            ArrayList<PDLActivityEntry> list = new ArrayList<>();
            list.addAll(vardenhetEvents);
            list.add(newEntry);
            storedActivities.put(enhetsId, list);
        }
    }

    private static boolean isStoredEvent(PDLActivityEntry storedEvent, String patientId,
                                         ActivityType activityType, ResourceType resourceType) {

        return storedEvent.getPatientId().equals(patientId)
            && storedEvent.getActivityType().equals(activityType)
            && storedEvent.getResourceType().equals(resourceType);
    }

}
