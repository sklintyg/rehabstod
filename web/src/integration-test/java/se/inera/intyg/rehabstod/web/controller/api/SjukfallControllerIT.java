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
package se.inera.intyg.rehabstod.web.controller.api;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import org.junit.Test;
import se.inera.intyg.rehabstod.service.Urval;
import se.inera.intyg.rehabstod.web.BaseRestIntegrationTest;
import se.inera.intyg.rehabstod.web.controller.api.dto.GetSjukfallRequest;
import se.inera.intyg.rehabstod.web.model.Sjukfall;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.junit.Assert.assertEquals;

/**
 * Basic test suite that verifies that the endpoint (/api/sjukfall) is available
 * and respond according to specification.
 *
 * Created by martin on 02/02/16.
 */
public class SjukfallControllerIT extends BaseRestIntegrationTest {

    private static final String API_ENDPOINT = "api/sjukfall";

    private static final int EXPECTED_ON_CENTRUM_VAST = 175;
    private static final int EXPECTED_ON_AKUTEN = 58;
    private static final int EXPECTED_ON_DIALYSEN = 58;

    @Test
    public void testGetSjukfallSummaryNotLoggedIn() {

        RestAssured.sessionId = null;

        given().expect().statusCode(FORBIDDEN).when().get(API_ENDPOINT + "/summary");
    }

    @Test
    public void testGetSjukfallSummary() {

        RestAssured.sessionId = getAuthSession(DEFAULT_LAKARE);

        given().expect().statusCode(OK).when().get(API_ENDPOINT + "/summary").then()
                .body(matchesJsonSchemaInClasspath("jsonschema/rhs-sjukfallsummary-response-schema.json"));
    }

    @Test
    public void testGetSjukfall() {

        RestAssured.sessionId = getAuthSession(DEFAULT_LAKARE);
        changeUrvalTo(Urval.ISSUED_BY_ME);

        GetSjukfallRequest request = new GetSjukfallRequest();
        request.setMaxIntygsGlapp(0);

        given().contentType(ContentType.JSON).and().body(request).expect().statusCode(OK).when().post(API_ENDPOINT).then()
                .body(matchesJsonSchemaInClasspath("jsonschema/rhs-sjukfall-response-schema.json"));

    }

    @Test
    public void testGetSjukfallOnEnhetWithUnderenheter() {
        testAntalOnEnhet("centrum-vast", EXPECTED_ON_CENTRUM_VAST);
    }

    @Test
    public void testGetSjukfallOnUnderenhetAkuten() {
        testAntalOnEnhet("akuten", EXPECTED_ON_AKUTEN);
    }

    @Test
    public void testGetSjukfallOnUnderenhetDialysen() {
        testAntalOnEnhet("dialys", EXPECTED_ON_DIALYSEN);
    }

    private void testAntalOnEnhet(String enhetId, int expectedCount) {
        RestAssured.sessionId = getAuthSession(EVA_H_LAKARE);
        changeUrvalTo(Urval.ISSUED_BY_ME);
        selectUnitByHsaId(enhetId);
        GetSjukfallRequest request = new GetSjukfallRequest();
        request.setMaxIntygsGlapp(0);

        Response response = given().contentType(ContentType.JSON).and().body(request).expect().statusCode(OK).when().post(API_ENDPOINT).then()
                .body(matchesJsonSchemaInClasspath("jsonschema/rhs-sjukfall-response-schema.json")).extract().response();

        Sjukfall[] resultList = response.body().as(Sjukfall[].class);
        assertEquals(expectedCount, resultList.length);
    }
}
