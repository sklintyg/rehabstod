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

package se.inera.intyg.rehabstod.logging;

import java.util.concurrent.TimeUnit;

public class SickLeaveLogMessageFactory {

    public static final String GET_ACTIVE_SICK_LEAVES = "GET_ACTIVE_SICK_LEAVES";
    public static final String ADD_DOCTOR_NAMES = "ADD_DOCTOR_NAMES";
    public static final String ADD_SRS_RISK = "ADD_SRS_RISK";
    public static final String ADD_UNANSWERED_COMMUNICATION = "ADD_UNANSWERED_COMMUNICATION";

    private long startTimer;

    public SickLeaveLogMessageFactory(long startTime) {
        startTimer = startTime;
    }

    public String message(String constant, int amount) {
        return String.format("SICK LEAVE LOG - Duration for %s: %d seconds. Amount: %d.", constant, timeElapsed(startTimer), amount);
    }

    private long timeElapsed(long startTime) {
        return TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - startTime);
    }

    public void setStartTimer(long startTimer) {
        this.startTimer = startTimer;
    }
}
