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

import java.util.List;

import se.inera.intyg.common.logmessages.ActivityType;
import se.inera.intyg.rehabstod.web.model.InternalSjukfall;

/**
 * Created by marced on 23/02/16.
 */
public interface PDLActivityStore {

    /**
     * Should return list of sjukfall (internally identified by patient) not already present in store for this vardenhet
     * and activityType.
     *
     * @param enhetsId
     * @param sjukfall
     * @param activityType
     * @return
     */
    List<InternalSjukfall> getActivitiesNotInStore(String enhetsId, List<InternalSjukfall> sjukfall, ActivityType activityType);

    /**
     * Should store the specified sjukfall for the vardenhet and activityType.
     *
     * @param enhetsId
     * @param sjukfall
     * @param activityType
     */
    void addActivitiesToStore(String enhetsId, List<InternalSjukfall> sjukfall, ActivityType activityType);
}
