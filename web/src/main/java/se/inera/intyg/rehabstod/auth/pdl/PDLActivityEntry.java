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
package se.inera.intyg.rehabstod.auth.pdl;

import java.io.Serializable;
import java.util.Objects;
import se.inera.intyg.infra.logmessages.ActivityType;
import se.inera.intyg.infra.logmessages.ResourceType;

/**
 * @author marced on 22/02/16.
 */
public class PDLActivityEntry implements Serializable {

    private static final long serialVersionUID = -3747905461916881904L;

    private String patientId;
    private ActivityType activityType;
    private ResourceType resourceType;

    public PDLActivityEntry(String patientId, ActivityType activityType, ResourceType resourceType) {
        this.patientId = patientId;
        this.activityType = activityType;
        this.resourceType = resourceType;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public ActivityType getActivityType() {
        return activityType;
    }

    public void setActivityType(ActivityType activityType) {
        this.activityType = activityType;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PDLActivityEntry)) {
            return false;
        }
        PDLActivityEntry pdlActivityEntry = (PDLActivityEntry) o;
        return Objects.equals(patientId, pdlActivityEntry.patientId)
            && activityType == pdlActivityEntry.activityType
            && resourceType == pdlActivityEntry.resourceType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(patientId, activityType, resourceType);
    }

}
