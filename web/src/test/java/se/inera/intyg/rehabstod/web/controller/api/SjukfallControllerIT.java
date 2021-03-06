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

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import java.util.Collection;
import org.junit.After;
import org.junit.Test;
import se.inera.intyg.rehabstod.web.BaseRestIntegrationTest;
import se.inera.intyg.rehabstod.web.controller.api.dto.AddVgToPatientViewRequest;
import se.inera.intyg.rehabstod.web.controller.api.dto.GetSjukfallRequest;
import se.inera.intyg.rehabstod.web.model.SjukfallEnhet;

/**
 * Basic test suite that verifies that the endpoint (/api/sjukfall) is available
 * and respond according to specification.
 *
 * Created by martin on 02/02/16.
 */
public class SjukfallControllerIT extends BaseRestIntegrationTest {

    private static final String API_ENDPOINT = "api/sjukfall";

    private static final String JSONSCHEMA_SUMMARY = "jsonschema/rhs-sjukfallsummary-response-schema.json";
    private static final String JSONSCHEMA_ENHET = "jsonschema/rhs-sjukfallenhet-response-schema.json";
    private static final String JSONSCHEMA_PATIENT = "jsonschema/rhs-sjukfallpatient-response-schema.json";

    private static final String PNR_TOLVAN_TOLVANSSON = "19121212-1212";

    @After
    public void cleanup() {
        sleep(200);
    }

    @Test
    public void testGetSjukfallSummaryNotLoggedIn() {
        RestAssured.sessionId = null;

        given().expect().statusCode(FORBIDDEN).when().get(API_ENDPOINT + "/summary");
    }

    @Test
    public void testGetSjukfallSummary() {
        SessionData sd = getAuthSession(DEFAULT_LAKARE);

        sd.begin().expect().statusCode(OK).when().get(API_ENDPOINT + "/summary").then()
            .body(matchesJsonSchemaInClasspath(JSONSCHEMA_SUMMARY));
    }

    @Test
    public void testGetSjukfallNotAllowedIfPdlConsentNotGiven() {
        SessionData sd = getAuthSession(DEFAULT_LAKARE_NO_CONSENT);
        GetSjukfallRequest request = new GetSjukfallRequest();

        sd.begin().body(request).expect().statusCode(SERVER_ERROR).when().post(API_ENDPOINT);
    }

    @Test
    public void testGetSjukfallByEnhet() {
        SessionData sd = getAuthSession(DEFAULT_LAKARE);
        GetSjukfallRequest request = new GetSjukfallRequest();

        sd.begin().body(request).expect().statusCode(OK).when().post(API_ENDPOINT).then()
            .body(matchesJsonSchemaInClasspath(JSONSCHEMA_ENHET));

    }

    @Test
    public void testGetSjukfallByPatient() {
        SessionData sd = getAuthSession(DEFAULT_LAKARE);

        GetSjukfallRequest request = new GetSjukfallRequest();
        request.setPatientId(PNR_TOLVAN_TOLVANSSON);

        sd.begin().body(request).expect().statusCode(OK).when().post(API_ENDPOINT + "/patient").then()
            .body(matchesJsonSchemaInClasspath(JSONSCHEMA_PATIENT));

    }

    @Test
    public void testGetSjukfallOnEnhetWithUnderenheter() {

        SessionData sd = getAuthSession(EVA_H_LAKARE);

        int centrumVastCount = getAntalOnEnhet(sd, "centrum-vast");
        int akutenCount = getAntalOnEnhet(sd, "akuten");
        int dialysCount = getAntalOnEnhet(sd, "dialys");

        assertTrue(akutenCount > 0);
        assertTrue(dialysCount > 0);
        assertTrue(akutenCount + dialysCount < centrumVastCount);
    }

    @Test
    public void testIncludeVgInSjukfall() {
        SessionData sd = getAuthSession(DEFAULT_LAKARE);

        AddVgToPatientViewRequest request = new AddVgToPatientViewRequest();
        request.setPatientId(PNR_TOLVAN_TOLVANSSON);
        request.setVardgivareId("vg1");

        Response response = sd.begin().body(request).expect().statusCode(OK)
            .when().post(API_ENDPOINT + "/patient/addVardgivare")
            .then().body(matchesJsonSchemaInClasspath("jsonschema/rhs-include-vg-in-sjukfall-response-schema.json"))
            .extract().response();

        assertNotNull(response);

        Collection<String> result = response.body().as(Collection.class);
        assertEquals(1, result.size());
    }

    private int getAntalOnEnhet(SessionData sd, String enhetId) {
        selectUnitByHsaId(sd, enhetId);
        GetSjukfallRequest request = new GetSjukfallRequest();

        Response response = sd.begin()
            .body(request)
            .expect().statusCode(OK)
            .when().post(API_ENDPOINT)
            .then()
            .body(matchesJsonSchemaInClasspath(JSONSCHEMA_ENHET))
            .extract().response();

        SjukfallEnhet[] resultList = response.body().as(SjukfallEnhet[].class);
        return resultList.length;
    }
}
