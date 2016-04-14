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

import se.inera.intyg.common.logmessages.ActivityType;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author marced on 22/02/16.
 */
public class PDLActivityEntry implements Serializable {

    private static final long serialVersionUID = -3747905461916881904L;

    private String patientId;
    private ActivityType activityType;

    public PDLActivityEntry(String patientId, ActivityType activityType) {
        this.patientId = patientId;
        this.activityType = activityType;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PDLActivityEntry pdlActivityEntry = (PDLActivityEntry) o;
        return Objects.equals(patientId, pdlActivityEntry.patientId)
                && activityType == pdlActivityEntry.activityType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(patientId, activityType);
    }
}
