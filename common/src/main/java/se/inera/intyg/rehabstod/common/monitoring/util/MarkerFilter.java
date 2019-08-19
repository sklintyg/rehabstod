/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.common.monitoring.util;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.AbstractMatcherFilter;
import ch.qos.logback.core.spi.FilterReply;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class MarkerFilter extends AbstractMatcherFilter<ILoggingEvent> {

    private List<Marker> markersToMatch = new ArrayList<>();

    @Override
    public void start() {
        if (!markersToMatch.isEmpty()) {
            super.start();
        } else {
            addError("!!! no marker yet !!!");
        }
    }

    @Override
    public FilterReply decide(ILoggingEvent event) {
        Marker marker = event.getMarker();
        if (!isStarted()) {
            return FilterReply.NEUTRAL;
        }
        if (null == marker) {
            return onMismatch;
        }
        for (Marker markerToMatch : markersToMatch) {
            if (markerToMatch.contains(marker)) {
                return onMatch;
            }
        }
        return onMismatch;
    }

    public void setMarker(String markerStr) {
        if (null != markerStr && !markerStr.isEmpty()) {
            markersToMatch.clear();
            markersToMatch.add(MarkerFactory.getMarker(markerStr));
        }
    }

    public void setMarkers(String markersStr) {
        if (null != markersStr && !markersStr.isEmpty()) {
            markersToMatch.clear();
            StringTokenizer tokenizer = new StringTokenizer(markersStr, ",");
            while (tokenizer.hasMoreElements()) {
                String markerStr = tokenizer.nextToken().trim();
                markersToMatch.add(MarkerFactory.getMarker(markerStr));
            }
        }
    }
}
