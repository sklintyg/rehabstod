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
package se.inera.intyg.rehabstod.service.pdl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.support.destination.DestinationResolutionException;
import se.inera.intyg.infra.logmessages.ActivityType;
import se.inera.intyg.infra.logmessages.ResourceType;
import se.inera.intyg.rehabstod.service.user.UserService;
import se.inera.intyg.rehabstod.testutil.TestDataGen;

/**
 * Created by eriklupander on 2016-03-03.
 */
@RunWith(MockitoJUnitRunner.class)
public class LogServiceImplTest {

    @Mock
    private JmsTemplate template = mock(JmsTemplate.class);

    @Mock
    private UserService userService;

    @Spy
    private PdlLogMessageFactoryImpl pdlLogMessageFactory;

    @InjectMocks
    private LogServiceImpl testee;

    @Test
    public void testSendPdlReadMessage() {
        when(userService.getUser()).thenReturn(TestDataGen.buildRehabStodUser(true));
        testee.logSjukfallData(TestDataGen.buildSjukfallList(5),
            ActivityType.READ, ResourceType.RESOURCE_TYPE_SJUKFALL);
        verify(template, times(1)).send(any());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSendPdlUnknownMessage() {
        when(userService.getUser()).thenReturn(TestDataGen.buildRehabStodUser(true));
        try {
            testee.logSjukfallData(TestDataGen.buildSjukfallList(5),
                ActivityType.EMERGENCY_ACCESS, ResourceType.RESOURCE_TYPE_SJUKFALL);
        } finally {
            verify(template, times(0)).send(any());
        }
    }

    @Test
    public void testNoLogMessageSentWhenSjukfallListIsEmpty() {
        testee.logSjukfallData(new ArrayList<>(),
            ActivityType.READ, ResourceType.RESOURCE_TYPE_SJUKFALL);
        verify(template, times(0)).send(any());
    }

    @Test(expected = JmsException.class)
    public void testSendPdlJmsException() {
        when(userService.getUser()).thenReturn(TestDataGen.buildRehabStodUser(true));
        doThrow(new DestinationResolutionException("")).when(template).send(any(MessageCreator.class));
        try {
            testee.logSjukfallData(TestDataGen.buildSjukfallList(5),
                ActivityType.READ, ResourceType.RESOURCE_TYPE_SJUKFALL);
        } finally {
            verify(template, times(1)).send(any());
        }
    }
}
