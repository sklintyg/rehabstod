/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.spi.FilterReply;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;

/**
 * Created by marced on 14/04/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class MarkerFilterTest {

    private static final String MARKER_NAME = "TEST_MARKER";

    MarkerFilter markerFilter;

    @Before
    public void before() {
        markerFilter = new MarkerFilter();
        markerFilter.setName("testfilter");

    }

    @Test
    public void testStartWithoutMarker() {
        markerFilter.start();
        assertFalse(markerFilter.isStarted());

    }

    @Test
    public void testStartWithMarker() {
        markerFilter.setMarker(MARKER_NAME);
        markerFilter.start();
        assertTrue(markerFilter.isStarted());

    }

    @Test
    public void testDecide() {
        markerFilter.setMarker(MARKER_NAME);
        markerFilter.start();
        ch.qos.logback.classic.Logger logbackLogger = (ch.qos.logback.classic.Logger) LoggerFactory
            .getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);

        LoggingEvent event = new LoggingEvent("fqcn", logbackLogger, Level.DEBUG, "a message", null, null);
        final FilterReply reply = markerFilter.decide(event);
        assertEquals(FilterReply.NEUTRAL, reply);

    }

    @Test
    public void testDecideWithoutStarted() {
        markerFilter.setMarkers("olle,pelle");
        ch.qos.logback.classic.Logger logbackLogger = (ch.qos.logback.classic.Logger) LoggerFactory
            .getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);

        LoggingEvent event = new LoggingEvent("fqcn", logbackLogger, Level.DEBUG, "a message", null, null);
        final FilterReply reply = markerFilter.decide(event);
        assertEquals(FilterReply.NEUTRAL, reply);

    }

    @Test
    public void testDecideMismatchWithNonMatchingMarker() {
        markerFilter.setOnMismatch(FilterReply.DENY);
        markerFilter.setMarker(MARKER_NAME);
        markerFilter.start();
        ch.qos.logback.classic.Logger logbackLogger = (ch.qos.logback.classic.Logger) LoggerFactory
            .getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);

        LoggingEvent event = new LoggingEvent("fqcn", logbackLogger, Level.DEBUG, "a message", null, null);
        event.setMarker(MarkerFactory.getMarker("unknownmarker"));
        final FilterReply reply = markerFilter.decide(event);
        assertEquals(FilterReply.DENY, reply);

    }

    @Test
    public void testDecideWithMatchingMarker() {
        markerFilter.setOnMatch(FilterReply.ACCEPT);
        markerFilter.setMarker(MARKER_NAME);
        markerFilter.start();
        ch.qos.logback.classic.Logger logbackLogger = (ch.qos.logback.classic.Logger) LoggerFactory
            .getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);

        LoggingEvent event = new LoggingEvent("fqcn", logbackLogger, Level.DEBUG, "a message", null, null);
        event.setMarker(MarkerFactory.getMarker(MARKER_NAME));
        final FilterReply reply = markerFilter.decide(event);
        assertEquals(FilterReply.ACCEPT, reply);

    }

}
