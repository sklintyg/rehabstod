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
package se.inera.intyg.rehabstod.web.controller.api;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

import org.junit.Test;
import se.inera.intyg.rehabstod.web.BaseRestIntegrationTest;

/**
 * Created by eriklupander on 2017-05-31.
 */
public class ConfigControllerIT extends BaseRestIntegrationTest {

    private static final String API_ENDPOINT = "api/config";

    @Test
    public void testGetDynamicLinksMatchesSchema() {
        given().expect().statusCode(OK).when().get(API_ENDPOINT + "/links").then()
            .body(matchesJsonSchemaInClasspath("jsonschema/rhs-links-schema.json"));
    }

    @Test
    public void testGetConfigMatchesSchema() {
        given().expect().statusCode(OK).when().get(API_ENDPOINT + "").then()
            .body(matchesJsonSchemaInClasspath("jsonschema/rhs-config-response-schema.json"));
    }

}
