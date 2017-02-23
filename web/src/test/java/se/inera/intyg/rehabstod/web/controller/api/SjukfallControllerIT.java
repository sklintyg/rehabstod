/**
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.junit.Assert.assertTrue;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import org.junit.Test;
import se.inera.intyg.rehabstod.service.Urval;
import se.inera.intyg.rehabstod.web.BaseRestIntegrationTest;
import se.inera.intyg.rehabstod.web.controller.api.dto.GetSjukfallRequest;
import se.inera.intyg.rehabstod.web.model.InternalSjukfall;

/**
 * Basic test suite that verifies that the endpoint (/api/sjukfall) is available
 * and respond according to specification.
 *
 * Created by martin on 02/02/16.
 */
public class SjukfallControllerIT extends BaseRestIntegrationTest {

    private static final String API_ENDPOINT = "api/sjukfall";

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

        RestAssured.sessionId = getAuthSession(EVA_H_LAKARE);
        changeUrvalTo(Urval.ISSUED_BY_ME);


        int centrumVastCount = getAntalOnEnhet("centrum-vast");
        int akutenCount = getAntalOnEnhet("akuten");
        int dialysCount = getAntalOnEnhet("dialys");

        assertTrue(akutenCount > 0);
        assertTrue(dialysCount > 0);
        assertTrue(akutenCount + dialysCount < centrumVastCount);
    }

    private int getAntalOnEnhet(String enhetId) {
        selectUnitByHsaId(enhetId);
        GetSjukfallRequest request = new GetSjukfallRequest();
        request.setMaxIntygsGlapp(0);

        Response response = given().contentType(ContentType.JSON).and().body(request).expect().statusCode(OK).when().post(API_ENDPOINT).then()
                .body(matchesJsonSchemaInClasspath("jsonschema/rhs-sjukfall-response-schema.json")).extract().response();

        InternalSjukfall[] resultList = response.body().as(InternalSjukfall[].class);
        return resultList.length;
    }
}