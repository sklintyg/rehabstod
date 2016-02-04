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

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

import com.jayway.restassured.RestAssured;
import org.junit.Test;
import se.inera.intyg.rehabstod.web.BaseRestIntegrationTest;

/**
 * Basic test suite that verifies that the endpoint (/api/sjukfall-summary) is available and repond according to
 * specification.
 *
 * Created by martin on 02/02/16.
 */
public class SjukfallSummaryControllerIT extends BaseRestIntegrationTest {

    private static final String API_ENDPOINT = "api/sjukfall/summary";

    @Test
    public void testGetSjukfallSummaryNotLoggedIn() {

        RestAssured.sessionId = null;

        given().expect().statusCode(403).when().get(API_ENDPOINT);
    }

    @Test
    public void testGetSjukfallSummary() {

        RestAssured.sessionId = getAuthSession(DEFAULT_LAKARE);

        given().expect().statusCode(200).when().get(API_ENDPOINT).then().
                body(matchesJsonSchemaInClasspath("jsonschema/rhs-sjukfallsummary-response-schema.json"));
    }
}
