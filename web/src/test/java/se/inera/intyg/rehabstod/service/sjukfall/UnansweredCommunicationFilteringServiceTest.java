/*
 * Copyright (C) 2023 Inera AB (http://www.inera.se)
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

package se.inera.intyg.rehabstod.service.sjukfall;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.rehabstod.service.sjukfall.dto.UnansweredCommunicationFilterType;
import se.inera.intyg.rehabstod.web.model.SjukfallEnhet;

@ExtendWith(MockitoExtension.class)
public class UnansweredCommunicationFilteringServiceTest {

    SjukfallEnhet sickLeave;

    @InjectMocks
    UnansweredCommunicationFilterServiceImpl unansweredCommunicationFilterService;

    @Nested
    class TestUnansweredCommunicationFiltering {
        @BeforeEach
        void setup() {
            sickLeave = new SjukfallEnhet();
        }

        @Nested
        class TestBlankFilterTypeId {

            @Test
            void shouldNotFilterSickLeaveWithQuestion() {
                sickLeave.setUnansweredOther(10);
                sickLeave.setObesvaradeKompl(0);

                final var response = unansweredCommunicationFilterService.filter(sickLeave, "");

                Assertions.assertTrue(response);
            }

            @Test
            void shouldNotFilterSickLeaveWithComplement() {
                sickLeave.setUnansweredOther(0);
                sickLeave.setObesvaradeKompl(10);

                final var response = unansweredCommunicationFilterService.filter(sickLeave, "");

                Assertions.assertTrue(response);
            }

            @Test
            void shouldNotFilterSickLeaveWithBothCommunicationTypes() {
                sickLeave.setUnansweredOther(10);
                sickLeave.setObesvaradeKompl(10);

                final var response = unansweredCommunicationFilterService.filter(sickLeave, "");

                Assertions.assertTrue(response);
            }
        }

        @Nested
        class TestNullFilterTypeId {

            @Test
            void shouldNotFilterSickLeaveWithQuestion() {
                sickLeave.setUnansweredOther(10);
                sickLeave.setObesvaradeKompl(0);

                final var response = unansweredCommunicationFilterService.filter(sickLeave, null);

                Assertions.assertTrue(response);
            }

            @Test
            void shouldNotFilterSickLeaveWithComplement() {
                sickLeave.setUnansweredOther(0);
                sickLeave.setObesvaradeKompl(10);

                final var response = unansweredCommunicationFilterService.filter(sickLeave, null);

                Assertions.assertTrue(response);
            }

            @Test
            void shouldNotFilterSickLeaveWithBothCommunicationTypes() {
                sickLeave.setUnansweredOther(10);
                sickLeave.setObesvaradeKompl(10);

                final var response = unansweredCommunicationFilterService.filter(sickLeave, null);

                Assertions.assertTrue(response);
            }
        }

        @Nested
        class TestNonMatchingFilterTypeId {

            @Test
            void shouldNotFilterSickLeaveWithQuestion() {
                sickLeave.setUnansweredOther(10);
                sickLeave.setObesvaradeKompl(0);

                final var response = unansweredCommunicationFilterService.filter(sickLeave, "random");

                Assertions.assertTrue(response);
            }

            @Test
            void shouldNotFilterSickLeaveWithComplement() {
                sickLeave.setUnansweredOther(0);
                sickLeave.setObesvaradeKompl(10);

                final var response = unansweredCommunicationFilterService.filter(sickLeave, "random");

                Assertions.assertTrue(response);
            }

            @Test
            void shouldNotFilterSickLeaveWithBothCommunicationTypes() {
                sickLeave.setUnansweredOther(10);
                sickLeave.setObesvaradeKompl(10);

                final var response = unansweredCommunicationFilterService.filter(sickLeave, "random");

                Assertions.assertTrue(response);
            }
        }

        @Nested
        class TestFilterTypeId1 {

            String request = UnansweredCommunicationFilterType.UNANSWERED_COMMUNICATION_FILTER_TYPE_1.toString();


            @Test
            void shouldFilterSickLeaveWithQuestion() {
                sickLeave.setUnansweredOther(10);
                sickLeave.setObesvaradeKompl(0);

                final var response = unansweredCommunicationFilterService.filter(sickLeave, request);

                Assertions.assertFalse(response);
            }

            @Test
            void shouldFilterSickLeaveWithComplement() {
                sickLeave.setUnansweredOther(0);
                sickLeave.setObesvaradeKompl(10);

                final var response = unansweredCommunicationFilterService.filter(sickLeave, request);

                Assertions.assertFalse(response);
            }

            @Test
            void shouldFilterSickLeaveWithBothCommunicationTypes() {
                sickLeave.setUnansweredOther(10);
                sickLeave.setObesvaradeKompl(10);

                final var response = unansweredCommunicationFilterService.filter(sickLeave, request);

                Assertions.assertFalse(response);
            }

            @Test
            void shouldNotFilterSickLeaveWithNoCommunicationTypes() {
                sickLeave.setUnansweredOther(0);
                sickLeave.setObesvaradeKompl(0);

                final var response = unansweredCommunicationFilterService.filter(sickLeave, request);

                Assertions.assertTrue(response);
            }
        }

        @Nested
        class TestFilterTypeId2 {
            String request = UnansweredCommunicationFilterType.UNANSWERED_COMMUNICATION_FILTER_TYPE_2.toString();

            @Test
            void shouldNotFilterSickLeaveWithQuestion() {
                sickLeave.setUnansweredOther(10);
                sickLeave.setObesvaradeKompl(0);

                final var response = unansweredCommunicationFilterService.filter(sickLeave, request);

                Assertions.assertTrue(response);
            }

            @Test
            void shouldNotFilterSickLeaveWithComplement() {
                sickLeave.setUnansweredOther(0);
                sickLeave.setObesvaradeKompl(10);

                final var response = unansweredCommunicationFilterService.filter(sickLeave, request);

                Assertions.assertTrue(response);
            }

            @Test
            void shouldNotFilterSickLeaveWithBothCommunicationTypes() {
                sickLeave.setUnansweredOther(10);
                sickLeave.setObesvaradeKompl(10);

                final var response = unansweredCommunicationFilterService.filter(sickLeave, request);

                Assertions.assertTrue(response);
            }

            @Test
            void shouldFilterSickLeaveWithNoCommunicationTypes() {
                sickLeave.setUnansweredOther(0);
                sickLeave.setObesvaradeKompl(0);

                final var response = unansweredCommunicationFilterService.filter(sickLeave, request);

                Assertions.assertFalse(response);
            }
        }

        @Nested
        class TestFilterTypeId3 {
            String request = UnansweredCommunicationFilterType.UNANSWERED_COMMUNICATION_FILTER_TYPE_3.toString();

            @Test
            void shouldFilterSickLeaveWithOnlyQuestion() {
                sickLeave.setUnansweredOther(10);
                sickLeave.setObesvaradeKompl(0);

                final var response = unansweredCommunicationFilterService.filter(sickLeave, request);

                Assertions.assertFalse(response);
            }

            @Test
            void shouldNotFilterSickLeaveWithOnlyComplement() {
                sickLeave.setUnansweredOther(0);
                sickLeave.setObesvaradeKompl(10);

                final var response = unansweredCommunicationFilterService.filter(sickLeave, request);

                Assertions.assertTrue(response);
            }

            @Test
            void shouldNotFilterSickLeaveWithBothCommunicationTypes() {
                sickLeave.setUnansweredOther(10);
                sickLeave.setObesvaradeKompl(10);

                final var response = unansweredCommunicationFilterService.filter(sickLeave, request);

                Assertions.assertTrue(response);
            }

            @Test
            void shouldFilterSickLeaveWithNoCommunicationTypes() {
                sickLeave.setUnansweredOther(0);
                sickLeave.setObesvaradeKompl(0);

                final var response = unansweredCommunicationFilterService.filter(sickLeave, request);

                Assertions.assertFalse(response);
            }
        }

        @Nested
        class TestFilterTypeId4 {
            String request = UnansweredCommunicationFilterType.UNANSWERED_COMMUNICATION_FILTER_TYPE_4.toString();

            @Test
            void shouldNotFilterSickLeaveWithOnlyQuestion() {
                sickLeave.setUnansweredOther(10);
                sickLeave.setObesvaradeKompl(0);

                final var response = unansweredCommunicationFilterService.filter(sickLeave, request);

                Assertions.assertTrue(response);
            }

            @Test
            void shouldFilterSickLeaveWithOnlyComplement() {
                sickLeave.setUnansweredOther(0);
                sickLeave.setObesvaradeKompl(10);

                final var response = unansweredCommunicationFilterService.filter(sickLeave, request);

                Assertions.assertFalse(response);
            }

            @Test
            void shouldNotFilterSickLeaveWithBothCommunicationTypes() {
                sickLeave.setUnansweredOther(10);
                sickLeave.setObesvaradeKompl(10);

                final var response = unansweredCommunicationFilterService.filter(sickLeave, request);

                Assertions.assertTrue(response);
            }

            @Test
            void shouldFilterSickLeaveWithNoCommunicationTypes() {
                sickLeave.setUnansweredOther(0);
                sickLeave.setObesvaradeKompl(0);

                final var response = unansweredCommunicationFilterService.filter(sickLeave, request);

                Assertions.assertFalse(response);
            }
        }
    }
}