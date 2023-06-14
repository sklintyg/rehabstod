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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.rehabstod.integration.wc.service.WcRestIntegrationService;
import se.inera.intyg.rehabstod.integration.wc.service.dto.UnansweredCommunicationRequest;
import se.inera.intyg.rehabstod.integration.wc.service.dto.UnansweredCommunicationResponse;
import se.inera.intyg.rehabstod.integration.wc.service.dto.UnansweredQAs;
import se.inera.intyg.rehabstod.service.unansweredCommunication.UnansweredCommunicationDecoratorServiceImpl;
import se.inera.intyg.rehabstod.web.model.LUCertificate;
import se.inera.intyg.rehabstod.web.model.Patient;
import se.inera.intyg.rehabstod.web.model.SjukfallEnhet;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UnansweredCommunicationDecoratorServiceTest {

    @Mock
    WcRestIntegrationService wcRestIntegrationService;

    @InjectMocks
    UnansweredCommunicationDecoratorServiceImpl unansweredCommunicationDecoratorService;

    UnansweredCommunicationResponse response = new UnansweredCommunicationResponse();

    private static final String CERTIFICATE_ID_0 = "C0";
    private static final String CERTIFICATE_ID_1 = "C1";
    private static final String CERTIFICATE_ID_2 = "C2";
    private static final String CERTIFICATE_ID_3 = "C3";
    private static final String PATIENT_ID_0 = "191212121212";
    private static final String PATIENT_ID_1 = "201212121212";
    private static final String PATIENT_ID_2 = "181212121212";
    private static final SjukfallEnhet SICK_LEAVE_SEVERAL_CERTIFICATES = new SjukfallEnhet();
    private static final SjukfallEnhet SICK_LEAVE_ONE_CERTIFICATE = new SjukfallEnhet();
    private static final SjukfallEnhet SICK_LEAVE_NO_CERTIFICATE_WITH_COMMUNICATION = new SjukfallEnhet();

    private static final List<SjukfallEnhet> SICK_LEAVES = List.of(
            SICK_LEAVE_ONE_CERTIFICATE,
            SICK_LEAVE_NO_CERTIFICATE_WITH_COMMUNICATION,
            SICK_LEAVE_SEVERAL_CERTIFICATES
    );
    private static final UnansweredQAs QA_0 = new UnansweredQAs(0, 0);
    private static final UnansweredQAs QA_1 = new UnansweredQAs(5, 3);
    private static final UnansweredQAs QA_2 = new UnansweredQAs(2, 3);
    private static final UnansweredQAs QA_3 = new UnansweredQAs(10, 20);

    @Nested
    class TestSickLeaves {

        @BeforeEach
        void setup() {
            SICK_LEAVE_SEVERAL_CERTIFICATES.setIntygLista(Arrays.asList(CERTIFICATE_ID_0, CERTIFICATE_ID_1, CERTIFICATE_ID_2));
            SICK_LEAVE_SEVERAL_CERTIFICATES.setPatient(new Patient(PATIENT_ID_0, "Name"));
            SICK_LEAVE_ONE_CERTIFICATE.setIntygLista(List.of(CERTIFICATE_ID_3));
            SICK_LEAVE_ONE_CERTIFICATE.setPatient(new Patient(PATIENT_ID_1, "Name"));
            SICK_LEAVE_NO_CERTIFICATE_WITH_COMMUNICATION.setIntygLista(List.of("NO_COMMUNICATION"));
            SICK_LEAVE_NO_CERTIFICATE_WITH_COMMUNICATION.setPatient(new Patient(PATIENT_ID_2, "Name"));

            final var unansweredQAsMap = new HashMap<String, UnansweredQAs>();
            unansweredQAsMap.put(CERTIFICATE_ID_0, QA_0);
            unansweredQAsMap.put(CERTIFICATE_ID_1, QA_1);
            unansweredQAsMap.put(CERTIFICATE_ID_2, QA_2);
            unansweredQAsMap.put(CERTIFICATE_ID_3, QA_3);
            response.setUnansweredQAsMap(unansweredQAsMap);

            when(wcRestIntegrationService.getUnansweredCommunicationForPatients(any())).thenReturn(response);
        }

        @Nested
        class TestItRequest {

            @Test
            void shouldCallRestServiceWithPatientIds() {
                final var captor = ArgumentCaptor.forClass(UnansweredCommunicationRequest.class);

                unansweredCommunicationDecoratorService.decorateSickLeaves(SICK_LEAVES);

                verify(wcRestIntegrationService).getUnansweredCommunicationForPatients(captor.capture());
                assertEquals(SICK_LEAVES.size(), captor.getValue().getPatientIds().size());
                assertTrue(captor.getValue().getPatientIds().contains(PATIENT_ID_0));
                assertTrue(captor.getValue().getPatientIds().contains(PATIENT_ID_1));
                assertTrue(captor.getValue().getPatientIds().contains(PATIENT_ID_2));
            }

            @Test
            void shouldCallRestServiceWithMaxDaysOfCommunication() {
                final var captor = ArgumentCaptor.forClass(UnansweredCommunicationRequest.class);

                unansweredCommunicationDecoratorService.decorateSickLeaves(SICK_LEAVES);

                verify(wcRestIntegrationService).getUnansweredCommunicationForPatients(captor.capture());
                assertNotNull(captor.getValue().getMaxDaysOfUnansweredCommunication());
            }
        }

        @Nested
        class TestResponse {
            @Test
            void shouldSetErrorAsTrueIfResponseIsSuccess() {
                response.setUnansweredCommunicationError(true);

                final var hasDecorated = unansweredCommunicationDecoratorService.decorateSickLeaves(SICK_LEAVES);

                assertFalse(hasDecorated);
            }

            @Test
            void shouldSetErrorAsFalseIfResponseIsFail() {
                response.setUnansweredCommunicationError(false);

                final var hasDecorated = unansweredCommunicationDecoratorService.decorateSickLeaves(SICK_LEAVES);

                assertTrue(hasDecorated);
            }
        }

        @Nested
        class TestDecoration {

            @Nested
            class TestComplements {
                @Test
                void shouldNotDecorateCertificateWithNoCommunication() {
                    unansweredCommunicationDecoratorService.decorateSickLeaves(SICK_LEAVES);

                    assertEquals(0, SICK_LEAVE_NO_CERTIFICATE_WITH_COMMUNICATION.getObesvaradeKompl());
                }

                @Test
                void shouldSetValueOfSickLeaveWithOneCertificateWithCommunication() {
                    unansweredCommunicationDecoratorService.decorateSickLeaves(SICK_LEAVES);

                    assertEquals(10, SICK_LEAVE_ONE_CERTIFICATE.getObesvaradeKompl());
                }

                @Test
                void shouldAddValuesOfSickLeaveWithSeveralCertificatesWithCommunication() {
                    unansweredCommunicationDecoratorService.decorateSickLeaves(SICK_LEAVES);

                    assertEquals(7, SICK_LEAVE_SEVERAL_CERTIFICATES.getObesvaradeKompl());
                }
            }

            @Nested
            class TestOthers {
                @Test
                void shouldNotDecorateCertificateWithNoCommunication() {
                    unansweredCommunicationDecoratorService.decorateSickLeaves(SICK_LEAVES);

                    assertEquals(0, SICK_LEAVE_NO_CERTIFICATE_WITH_COMMUNICATION.getUnansweredOther());
                }

                @Test
                void shouldSetValueOfSickLeaveWithOneCertificateWithCommunication() {
                    unansweredCommunicationDecoratorService.decorateSickLeaves(SICK_LEAVES);

                    assertEquals(20, SICK_LEAVE_ONE_CERTIFICATE.getUnansweredOther());
                }

                @Test
                void shouldAddValuesOfSickLeaveWithSeveralCertificatesWithCommunication() {
                    unansweredCommunicationDecoratorService.decorateSickLeaves(SICK_LEAVES);

                    assertEquals(6, SICK_LEAVE_SEVERAL_CERTIFICATES.getUnansweredOther());
                }
            }
        }
    }

    @Nested
    class TestLuCertificate {

        final List<LUCertificate> certificates = Arrays.asList(
                LUCertificate
                        .builder()
                        .certificateId(CERTIFICATE_ID_0)
                        .patient(new Patient(PATIENT_ID_0, "Name"))
                        .build(),
                LUCertificate
                        .builder()
                        .certificateId(CERTIFICATE_ID_1)
                        .patient(new Patient(PATIENT_ID_1, "Name"))
                        .build()
        );

        @BeforeEach
        void setup() {

            final var unansweredQAsMap = new HashMap<String, UnansweredQAs>();
            unansweredQAsMap.put(CERTIFICATE_ID_0, QA_0);
            unansweredQAsMap.put(CERTIFICATE_ID_1, QA_1);
            response.setUnansweredQAsMap(unansweredQAsMap);

            when(wcRestIntegrationService.getUnansweredCommunicationForPatients(any())).thenReturn(response);
        }

        @Nested
        class TestItRequest {

            @Test
            void shouldCallRestServiceWithPatientIds() {
                final var captor = ArgumentCaptor.forClass(UnansweredCommunicationRequest.class);

                unansweredCommunicationDecoratorService.decorateLuCertificates(certificates);

                verify(wcRestIntegrationService).getUnansweredCommunicationForPatients(captor.capture());
                assertEquals(certificates.size(), captor.getValue().getPatientIds().size());
                assertTrue(captor.getValue().getPatientIds().contains(PATIENT_ID_0));
                assertTrue(captor.getValue().getPatientIds().contains(PATIENT_ID_1));
            }

            @Test
            void shouldCallRestServiceWithMaxDaysOfCommunication() {
                final var captor = ArgumentCaptor.forClass(UnansweredCommunicationRequest.class);

                unansweredCommunicationDecoratorService.decorateLuCertificates(certificates);

                verify(wcRestIntegrationService).getUnansweredCommunicationForPatients(captor.capture());
                assertNotNull(captor.getValue().getMaxDaysOfUnansweredCommunication());
            }
        }

        @Nested
        class TestResponse {
            @Test
            void shouldSetErrorAsTrueIfResponseIsSuccess() {
                response.setUnansweredCommunicationError(true);

                final var hasDecorated = unansweredCommunicationDecoratorService.decorateLuCertificates(certificates);

                assertFalse(hasDecorated);
            }

            @Test
            void shouldSetErrorAsFalseIfResponseIsFail() {
                response.setUnansweredCommunicationError(false);

                final var hasDecorated = unansweredCommunicationDecoratorService.decorateLuCertificates(certificates);

                assertTrue(hasDecorated);
            }
        }

        @Nested
        class TestDecoration {

            @Nested
            class TestComplements {
                @Test
                void shouldNotDecorateCertificateWithNoCommunication() {
                    unansweredCommunicationDecoratorService.decorateLuCertificates(certificates);

                    assertEquals(QA_0.getComplement(), certificates.get(0).getUnAnsweredComplement());
                }

                @Test
                void shouldSetValueOfCertificateWithCommunication() {
                    unansweredCommunicationDecoratorService.decorateLuCertificates(certificates);

                    assertEquals(QA_1.getComplement(), certificates.get(1).getUnAnsweredComplement());
                }
            }

            @Nested
            class TestOthers {
                @Test
                void shouldNotDecorateCertificateWithNoCommunication() {
                    unansweredCommunicationDecoratorService.decorateLuCertificates(certificates);

                    assertEquals(QA_0.getOthers(), certificates.get(0).getUnAnsweredOther());
                }

                @Test
                void shouldSetValueOfSickLeaveWithOneCertificateWithCommunication() {
                    unansweredCommunicationDecoratorService.decorateLuCertificates(certificates);

                    assertEquals(QA_1.getOthers(), certificates.get(1).getUnAnsweredOther());
                }
            }
        }
    }
}