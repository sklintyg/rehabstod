/*
 * Copyright (C) 2026 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.integration.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import se.inera.intyg.rehabstod.infrastructure.config.properties.AppProperties;
import se.inera.intyg.rehabstod.infrastructure.integration.wc.service.WcRestIntegrationServiceImpl;
import se.inera.intyg.rehabstod.infrastructure.integration.wc.service.dto.UnansweredCommunicationRequest;
import se.inera.intyg.rehabstod.infrastructure.integration.wc.service.dto.UnansweredCommunicationResponse;
import se.inera.intyg.rehabstod.infrastructure.integration.wc.service.dto.UnansweredQAs;
import se.inera.intyg.rehabstod.logging.MdcLogConstants;

@ExtendWith(MockitoExtension.class)
class WcRestIntegrationServiceImplTest {

  private static final String SCHEME = "http";
  private static final String BASE_URL = "localhost";
  private static final int PORT = 8080;
  private static final String TRACE_ID = "test-trace-id";
  private static final String SESSION_ID = "test-session-id";
  public static final int MAX_DAYS_OF_UNANSWERED_COMMUNICATION = 30;

  @Mock private RestClient wcRestClient;

  @Mock private RestClient.RequestBodyUriSpec requestBodyUriSpec;

  @Mock private RestClient.RequestBodySpec requestBodySpec;

  @Mock private RestClient.ResponseSpec responseSpec;

  private WcRestIntegrationServiceImpl service;

  @BeforeEach
  void setUp() {
    service =
        new WcRestIntegrationServiceImpl(
            wcRestClient,
            new AppProperties(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                new AppProperties.Integration(
                    null,
                    new AppProperties.Integration.WebcertIntegration(
                        SCHEME, BASE_URL, PORT, null, null, 0),
                    null,
                    null,
                    null,
                    null,
                    null)));

    MDC.put(MdcLogConstants.TRACE_ID_KEY, TRACE_ID);
    MDC.put(MdcLogConstants.SESSION_ID_KEY, SESSION_ID);
  }

  @AfterEach
  void tearDown() {
    MDC.clear();
  }

  @Test
  void shouldReturnUnansweredCommunicationSuccessfully() {
    List<String> patientIds = Arrays.asList("191212121212", "191212121213");
    UnansweredCommunicationRequest request =
        UnansweredCommunicationRequest.builder()
            .maxDaysOfUnansweredCommunication(MAX_DAYS_OF_UNANSWERED_COMMUNICATION)
            .patientIds(patientIds)
            .build();

    Map<String, UnansweredQAs> expectedMap = new HashMap<>();
    expectedMap.put("191212121212", new UnansweredQAs(2, 1));
    expectedMap.put("191212121213", new UnansweredQAs(0, 3));

    UnansweredCommunicationResponse expectedResponse =
        UnansweredCommunicationResponse.builder()
            .unansweredQAsMap(expectedMap)
            .unansweredCommunicationError(false)
            .build();

    when(wcRestClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.body(eq(request))).thenReturn(requestBodySpec);
    when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(any(MediaType.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.body(UnansweredCommunicationResponse.class)).thenReturn(expectedResponse);

    UnansweredCommunicationResponse result = service.getUnansweredCommunicationForPatients(request);

    assertNotNull(result);
    assertFalse(result.isUnansweredCommunicationError());
    assertNotNull(result.getUnansweredQAsMap());
    assertEquals(2, result.getUnansweredQAsMap().size());
    assertEquals(2, result.getUnansweredQAsMap().get("191212121212").getComplement());
    assertEquals(1, result.getUnansweredQAsMap().get("191212121212").getOthers());
    assertEquals(0, result.getUnansweredQAsMap().get("191212121213").getComplement());
    assertEquals(3, result.getUnansweredQAsMap().get("191212121213").getOthers());

    verify(wcRestClient).post();
    verify(requestBodySpec).body(request);
    verify(requestBodySpec).contentType(MediaType.APPLICATION_JSON);
  }

  @Test
  void shouldReturnErrorResponseWhenRestClientThrowsException() {
    UnansweredCommunicationRequest request =
        UnansweredCommunicationRequest.builder()
            .maxDaysOfUnansweredCommunication(MAX_DAYS_OF_UNANSWERED_COMMUNICATION)
            .patientIds(Arrays.asList("191212121212"))
            .build();

    when(wcRestClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.body(eq(request))).thenReturn(requestBodySpec);
    when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(any(MediaType.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.retrieve()).thenThrow(new RestClientException("Connection failed"));

    UnansweredCommunicationResponse result = service.getUnansweredCommunicationForPatients(request);

    assertNotNull(result);
    assertTrue(result.isUnansweredCommunicationError());
    assertNull(result.getUnansweredQAsMap());
  }

  @Test
  void shouldReturnErrorResponseWhenRetrieveThrowsRuntimeException() {
    UnansweredCommunicationRequest request =
        UnansweredCommunicationRequest.builder()
            .maxDaysOfUnansweredCommunication(MAX_DAYS_OF_UNANSWERED_COMMUNICATION)
            .patientIds(Arrays.asList("191212121212"))
            .build();

    when(wcRestClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.body(eq(request))).thenReturn(requestBodySpec);
    when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(any(MediaType.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.body(UnansweredCommunicationResponse.class))
        .thenThrow(new RuntimeException("Unexpected error"));

    UnansweredCommunicationResponse result = service.getUnansweredCommunicationForPatients(request);

    assertNotNull(result);
    assertTrue(result.isUnansweredCommunicationError());
    assertNull(result.getUnansweredQAsMap());
  }

  @Test
  void shouldHandleEmptyUnansweredQAsMap() {
    UnansweredCommunicationRequest request =
        UnansweredCommunicationRequest.builder()
            .maxDaysOfUnansweredCommunication(MAX_DAYS_OF_UNANSWERED_COMMUNICATION)
            .patientIds(Arrays.asList("191212121212"))
            .build();

    UnansweredCommunicationResponse expectedResponse =
        UnansweredCommunicationResponse.builder()
            .unansweredQAsMap(new HashMap<>())
            .unansweredCommunicationError(false)
            .build();

    when(wcRestClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.body(eq(request))).thenReturn(requestBodySpec);
    when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(any(MediaType.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.body(UnansweredCommunicationResponse.class)).thenReturn(expectedResponse);

    UnansweredCommunicationResponse result = service.getUnansweredCommunicationForPatients(request);

    assertNotNull(result);
    assertFalse(result.isUnansweredCommunicationError());
    assertNotNull(result.getUnansweredQAsMap());
    assertTrue(result.getUnansweredQAsMap().isEmpty());
  }

  @Test
  void shouldReturnNullWhenResponseIsNull() {
    UnansweredCommunicationRequest request =
        UnansweredCommunicationRequest.builder()
            .maxDaysOfUnansweredCommunication(30)
            .patientIds(Arrays.asList("191212121212"))
            .build();

    when(wcRestClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.body(eq(request))).thenReturn(requestBodySpec);
    when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(any(MediaType.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.body(UnansweredCommunicationResponse.class)).thenReturn(null);

    UnansweredCommunicationResponse result = service.getUnansweredCommunicationForPatients(request);

    assertNull(result);
  }

  @Test
  void shouldHandleMultiplePatientIds() {
    // Given
    List<String> patientIds =
        Arrays.asList("191212121212", "191212121213", "191212121214", "191212121215");

    UnansweredCommunicationRequest request =
        UnansweredCommunicationRequest.builder()
            .maxDaysOfUnansweredCommunication(MAX_DAYS_OF_UNANSWERED_COMMUNICATION)
            .patientIds(patientIds)
            .build();

    Map<String, UnansweredQAs> expectedMap = new HashMap<>();
    patientIds.forEach(id -> expectedMap.put(id, new UnansweredQAs(1, 2)));

    UnansweredCommunicationResponse expectedResponse =
        UnansweredCommunicationResponse.builder()
            .unansweredQAsMap(expectedMap)
            .unansweredCommunicationError(false)
            .build();

    when(wcRestClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.body(eq(request))).thenReturn(requestBodySpec);
    when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(any(MediaType.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.body(UnansweredCommunicationResponse.class)).thenReturn(expectedResponse);

    UnansweredCommunicationResponse result = service.getUnansweredCommunicationForPatients(request);

    assertNotNull(result);
    assertFalse(result.isUnansweredCommunicationError());
    assertEquals(4, result.getUnansweredQAsMap().size());
  }

  @Test
  void shouldHandleMissingMdcContext() {
    MDC.clear(); // Clear MDC context

    UnansweredCommunicationRequest request =
        UnansweredCommunicationRequest.builder()
            .maxDaysOfUnansweredCommunication(MAX_DAYS_OF_UNANSWERED_COMMUNICATION)
            .patientIds(Arrays.asList("191212121212"))
            .build();

    UnansweredCommunicationResponse expectedResponse =
        UnansweredCommunicationResponse.builder()
            .unansweredQAsMap(new HashMap<>())
            .unansweredCommunicationError(false)
            .build();

    when(wcRestClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.body(eq(request))).thenReturn(requestBodySpec);
    when(requestBodySpec.header(anyString(), nullable(String.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(any(MediaType.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.body(UnansweredCommunicationResponse.class)).thenReturn(expectedResponse);

    UnansweredCommunicationResponse result = service.getUnansweredCommunicationForPatients(request);

    assertNotNull(result);
    assertFalse(result.isUnansweredCommunicationError());
    verify(requestBodySpec).header("x-trace-id", null);
    verify(requestBodySpec).header("x-session-id", null);

    MDC.put(MdcLogConstants.TRACE_ID_KEY, TRACE_ID);
    MDC.put(MdcLogConstants.SESSION_ID_KEY, SESSION_ID);
  }

  @Test
  void shouldIncludeMdcHeadersInRequest() {
    UnansweredCommunicationRequest request =
        UnansweredCommunicationRequest.builder()
            .maxDaysOfUnansweredCommunication(MAX_DAYS_OF_UNANSWERED_COMMUNICATION)
            .patientIds(Arrays.asList("191212121212"))
            .build();

    UnansweredCommunicationResponse expectedResponse =
        UnansweredCommunicationResponse.builder()
            .unansweredQAsMap(new HashMap<>())
            .unansweredCommunicationError(false)
            .build();

    when(wcRestClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.body(eq(request))).thenReturn(requestBodySpec);
    when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(any(MediaType.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.body(UnansweredCommunicationResponse.class)).thenReturn(expectedResponse);

    service.getUnansweredCommunicationForPatients(request);

    verify(requestBodySpec).header("x-trace-id", TRACE_ID);
    verify(requestBodySpec).header("x-session-id", SESSION_ID);
  }

  @Test
  void shouldSetCorrectContentType() {
    UnansweredCommunicationRequest request =
        UnansweredCommunicationRequest.builder()
            .maxDaysOfUnansweredCommunication(MAX_DAYS_OF_UNANSWERED_COMMUNICATION)
            .patientIds(Arrays.asList("191212121212"))
            .build();

    UnansweredCommunicationResponse expectedResponse =
        UnansweredCommunicationResponse.builder()
            .unansweredQAsMap(new HashMap<>())
            .unansweredCommunicationError(false)
            .build();

    when(wcRestClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.body(eq(request))).thenReturn(requestBodySpec);
    when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(any(MediaType.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.body(UnansweredCommunicationResponse.class)).thenReturn(expectedResponse);

    service.getUnansweredCommunicationForPatients(request);

    verify(requestBodySpec).contentType(MediaType.APPLICATION_JSON);
  }
}
