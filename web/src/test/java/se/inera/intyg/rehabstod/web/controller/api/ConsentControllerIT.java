/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.web.controller.api;


import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static se.inera.intyg.rehabstod.web.controller.api.ConsentController.MAX_DAYS_FOR_CONSENT;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.util.Map;
import org.junit.After;
import org.junit.Test;
import se.inera.intyg.rehabstod.web.BaseRestIntegrationTest;
import se.inera.intyg.rehabstod.web.controller.api.dto.RegisterExtendedConsentRequest;
import se.inera.intyg.rehabstod.web.controller.api.dto.RegisterExtendedConsentResponse;
import se.inera.intyg.schemas.contract.Personnummer;

/**
 * Created by Magnus Ekstrand on 2018-10-28.
 */
public class ConsentControllerIT extends BaseRestIntegrationTest {

    private static final String API_ENDPOINT = "api/consent";
    private static final String API_ENDPOINT_STUB = "services/api/stub/samtyckestjanst-api/consent";

    // Use Tolvan Tolvansson since he exist in the stubbed data.
    private static final String PNR_TOLVAN_TOLVANSSON = "19121212-1212";

    @After
    public void cleanup() {
        sleep(200);
    }

    @Test
    public void testRegisterConsent() {
        SessionData sd = getAuthSession(DEFAULT_LAKARE);

        sd.begin().contentType(ContentType.JSON).expect().statusCode(OK).when().delete(API_ENDPOINT_STUB);

        // 1. Register a consent
        RegisterExtendedConsentRequest request = new RegisterExtendedConsentRequest();
        request.setPatientId(PNR_TOLVAN_TOLVANSSON);
        request.setDays(7);

        Response response = null;

        response = sd.begin().contentType(ContentType.JSON).and().body(request)
            .expect().statusCode(OK)
            .when().post(API_ENDPOINT)
            .then().body(matchesJsonSchemaInClasspath("jsonschema/rhs-registerextendedconsent-response-schema.json"))
            .extract().response();

        assertNotNull(response);

        RegisterExtendedConsentResponse result = response.body().as(RegisterExtendedConsentResponse.class);
        assertEquals(RegisterExtendedConsentResponse.ResponseCode.OK, result.getResponseCode());
        assertFalse(Strings.isNullOrEmpty(result.getResponseMessage()));
        assertEquals(DEFAULT_LAKARE.getHsaId(), result.getRegisteredBy());

        // 2. Do check that consent is in the store
        Map<String, String> parameters = ImmutableMap.of(
            "vardgivareId", DEFAULT_VG_HSAID,
            "vardenhetId", DEFAULT_VE_HSAID);

        response = sd.begin()
            .queryParams(parameters)
            .body(request)
            .expect().statusCode(OK)
            .when().get(API_ENDPOINT_STUB + "/" + PNR_TOLVAN_TOLVANSSON)
            .then().extract().response();

        String str = response.body().as(String.class);
        assertTrue(Boolean.parseBoolean(str));
    }

    @Test
    public void testRegisterConsentForCurrentUserOnly() throws Exception {
        SessionData sd = getAuthSession(DEFAULT_LAKARE);

        sd.begin().expect().statusCode(OK).when().delete(API_ENDPOINT_STUB);

        // 1. Register a consent
        RegisterExtendedConsentRequest request = new RegisterExtendedConsentRequest();
        request.setOnlyCurrentUser(true);
        request.setPatientId(PNR_TOLVAN_TOLVANSSON);
        request.setDays(7);

        Response response = sd.begin().contentType(ContentType.JSON).and().body(request).expect().statusCode(OK)
            .when().post(API_ENDPOINT)
            .then().body(matchesJsonSchemaInClasspath("jsonschema/rhs-registerextendedconsent-response-schema.json"))
            .extract().response();

        assertNotNull(response);

        RegisterExtendedConsentResponse result = response.body().as(RegisterExtendedConsentResponse.class);
        assertEquals(RegisterExtendedConsentResponse.ResponseCode.OK, result.getResponseCode());
        assertFalse(Strings.isNullOrEmpty(result.getResponseMessage()));
        assertEquals(DEFAULT_LAKARE.getHsaId(), result.getRegisteredBy());

        // 2. Do check that consent is in the store
        sd.begin().body(request)
            .expect().statusCode(OK)
            .when().get(API_ENDPOINT_STUB).then()
            .body("vardgivareId[0]", equalTo(DEFAULT_VG_HSAID))
            .body("vardenhetId[0]", equalTo(DEFAULT_VE_HSAID))
            .body("employeeId[0]", equalTo(DEFAULT_LAKARE_HSAID))
            .body("patientId[0]", equalTo(createPnr(PNR_TOLVAN_TOLVANSSON).getPersonnummer()));

    }

    @Test
    public void testRegisterConsentWithNoPatientId() {
        SessionData sd = getAuthSession(DEFAULT_LAKARE);

        sd.begin().expect().statusCode(OK).when().delete(API_ENDPOINT_STUB);

        RegisterExtendedConsentRequest request = new RegisterExtendedConsentRequest();
        request.setDays(30);

        Response response = sd.begin().body(request).expect().statusCode(OK)
            .when().post(API_ENDPOINT)
            .then().body(matchesJsonSchemaInClasspath("jsonschema/rhs-registerextendedconsent-response-schema.json"))
            .extract().response();

        assertNotNull(response);

        RegisterExtendedConsentResponse result = response.body().as(RegisterExtendedConsentResponse.class);
        assertEquals(RegisterExtendedConsentResponse.ResponseCode.ERROR, result.getResponseCode());
        assertFalse(Strings.isNullOrEmpty(result.getResponseMessage()));
    }

    @Test
    public void testRegisterConsentMoreThanMaxDays() {
        SessionData sd = getAuthSession(DEFAULT_LAKARE);

        sd.begin().expect().statusCode(OK).when().delete(API_ENDPOINT_STUB);

        // 1. Register a consent
        RegisterExtendedConsentRequest request = new RegisterExtendedConsentRequest();
        request.setPatientId(PNR_TOLVAN_TOLVANSSON);
        request.setDays(MAX_DAYS_FOR_CONSENT + 1);

        Response response;

        response = sd.begin().body(request)
            .expect().statusCode(OK)
            .when().post(API_ENDPOINT)
            .then().body(matchesJsonSchemaInClasspath("jsonschema/rhs-registerextendedconsent-response-schema.json"))
            .extract().response();

        assertNotNull(response);

        RegisterExtendedConsentResponse result = response.body().as(RegisterExtendedConsentResponse.class);
        assertEquals(RegisterExtendedConsentResponse.ResponseCode.ERROR, result.getResponseCode());
        assertFalse(Strings.isNullOrEmpty(result.getResponseMessage()));
    }

    private static Personnummer createPnr(String personId) {
        return Personnummer.createPersonnummer(personId)
            .orElseThrow(() -> new IllegalArgumentException("Could not parse passed personnummer: " + personId));
    }

}
