/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import se.inera.intyg.infra.logmessages.ActivityType;
import se.inera.intyg.infra.logmessages.ResourceType;
import se.inera.intyg.rehabstod.web.model.SjukfallEnhet;
import se.inera.intyg.schemas.contract.Personnummer;

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

        // We actually don't check the vardenehet for each sjukfall, we trust that the given enhetsId is correct.
        List<PDLActivityEntry> vardenhetEvents = storedActivities.get(enhetsId);

        if (vardenhetEvents == null) {
            // Nothing logged for this vardenhet yet - so all are new activities
            return sjukfall;
        }

        // find all patientId's NOT having av event entry for the combination patientId + eventType
        return sjukfall.stream()
                .filter(sf -> vardenhetEvents.stream()
                        .noneMatch(storedEvent -> isStoredEvent(storedEvent,
                                sf.getPatient().getId(),
                                activityType,
                                resourceType)))
                .collect(Collectors.toList());

    }

    /**
     * Should return true or false if patient's sjukfall is in store or not.
     */
    public static boolean isActivityInStore(String enhetsId,
                                            String patientId,
                                            ActivityType activityType,
                                            ResourceType resourceType,
                                            Map<String, List<PDLActivityEntry>> storedActivities) {

        String errMsg = "Cannot make lookup in PDL activity store, %s was null or empty.";
        Preconditions.checkArgument(!Strings.isNullOrEmpty(enhetsId), String.format(errMsg, "enhetsId"));
        Preconditions.checkArgument(!Strings.isNullOrEmpty(patientId), String.format(errMsg, "patientId"));

        if (storedActivities == null || storedActivities.isEmpty()) {
            return false;
        }

        // We actually don't check tha vardenhet for each sjukfall, we trust that the given enhetsId is correct.
        List<PDLActivityEntry> vardenhetEvents = storedActivities.get(enhetsId);
        if (vardenhetEvents == null) {
            // Nothing logged for this vardenhet yet - so all are new activities
            return false;
        }

        return vardenhetEvents.stream()
                .anyMatch(storedEvent -> isStoredEvent(storedEvent, patientId, activityType, resourceType));
    }

    /**
     * Should store the specified patient for the vardenhet, activityType and resourceType.
     */
    public static void addActivityToStore(String enhetsId,
                                          String patientId,
                                          ActivityType activityType,
                                          ResourceType resourceType,
                                          Map<String, List<PDLActivityEntry>> storedActivities) {

        if (Strings.isNullOrEmpty(patientId)) {
            return;
        }

        PDLActivityEntry newEntry = new PDLActivityEntry(
                createPersonnummer(patientId).getPersonnummer(),
                activityType,
                resourceType);

        List<PDLActivityEntry> vardenhetEvents = storedActivities.get(enhetsId);
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

        final List<PDLActivityEntry> newEntryList = sjukfallToAdd.stream()
                .map(sf -> new PDLActivityEntry(
                        createPersonnummer(sf.getPatient().getId()).getPersonnummer(),
                        activityType,
                        resourceType))
                .collect(Collectors.toList());

        List<PDLActivityEntry> vardenhetEvents = storedActivities.get(enhetsId);
        if (vardenhetEvents == null) {
            storedActivities.put(enhetsId, newEntryList);
        } else {
            ArrayList<PDLActivityEntry> list = new ArrayList<>();
            list.addAll(vardenhetEvents);
            list.addAll(newEntryList);
            storedActivities.put(enhetsId, list);
        }
    }

    private static boolean isStoredEvent(PDLActivityEntry storedEvent, String patientId,
                                         ActivityType activityType, ResourceType resourceType) {

        Personnummer pnrPatinet = createPersonnummer(patientId);

        return storedEvent.getPatientId().equals(pnrPatinet.getPersonnummer())
            && storedEvent.getActivityType().equals(activityType)
            && storedEvent.getResourceType().equals(resourceType);
    }

    private static Personnummer createPersonnummer(String personId) {
        return Personnummer.createPersonnummer(personId)
                .orElseThrow(() -> new IllegalStateException("Could not parse passed personnummer: " + personId));
    }
}
