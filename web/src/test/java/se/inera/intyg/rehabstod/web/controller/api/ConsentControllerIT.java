/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import org.junit.Test;
import se.inera.intyg.rehabstod.web.BaseRestIntegrationTest;
import se.inera.intyg.rehabstod.web.controller.api.dto.RegisterExtendedConsentRequest;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

/**
 * Created by eriklupander on 2017-05-31.
 */
public class ConsentControllerIT extends BaseRestIntegrationTest {

    private static final String API_ENDPOINT = "api/consent";

    @Test
    public void testRegisteredExtendedConsent() {
        RestAssured.sessionId = getAuthSession(DEFAULT_LAKARE);

        RegisterExtendedConsentRequest request = new RegisterExtendedConsentRequest("201212121212");

        given().contentType(ContentType.JSON).and().body(request).expect().statusCode(OK).when().post(API_ENDPOINT).then()
                .body(matchesJsonSchemaInClasspath("jsonschema/rhs-registerextendedconsent-response-schema.json"));

    }

}
