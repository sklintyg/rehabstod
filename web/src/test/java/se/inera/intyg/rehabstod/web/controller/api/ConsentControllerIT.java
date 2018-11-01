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

import org.junit.Test;

import com.google.common.base.Strings;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import se.inera.intyg.rehabstod.web.BaseRestIntegrationTest;
import se.inera.intyg.rehabstod.web.controller.api.dto.RegisterExtendedConsentRequest;
import se.inera.intyg.rehabstod.web.controller.api.dto.RegisterExtendedConsentResponse;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by Magnus Ekstrand on 2018-10-28.
 */
public class ConsentControllerIT extends BaseRestIntegrationTest {

    private static final String API_ENDPOINT = "api/consent";

    // Use Tolvan Tolvansson since he exist in the stubbed data.
    private static final String PNR_TOLVAN_TOLVANSSON = "19121212-1212";

    @Test
    public void testRegisteredExtendedConsent() {
        RestAssured.sessionId = getAuthSession(DEFAULT_LAKARE);

        RegisterExtendedConsentRequest request = new RegisterExtendedConsentRequest();
        request.setPatientId(PNR_TOLVAN_TOLVANSSON);
        request.setDays(30);

        Response response = given().contentType(ContentType.JSON).and().body(request).expect().statusCode(OK)
                .when().post(API_ENDPOINT)
                .then().body(matchesJsonSchemaInClasspath("jsonschema/rhs-registerextendedconsent-response-schema.json"))
                .extract().response();

        assertNotNull(response);

        RegisterExtendedConsentResponse result = response.body().as(RegisterExtendedConsentResponse.class);
        assertEquals(RegisterExtendedConsentResponse.ResponseCode.OK, result.getResponseCode());
        assertTrue(Strings.isNullOrEmpty(result.getResponseMessage()));
        assertEquals(DEFAULT_LAKARE.getHsaId(), result.getRegisteredBy());
    }

    @Test
    public void testRegisteredExtendedConsentWithNoPatientId() {
        RestAssured.sessionId = getAuthSession(DEFAULT_LAKARE);

        RegisterExtendedConsentRequest request = new RegisterExtendedConsentRequest();
        request.setDays(30);

        Response response = given().contentType(ContentType.JSON).and().body(request).expect().statusCode(OK)
                .when().post(API_ENDPOINT)
                .then().body(matchesJsonSchemaInClasspath("jsonschema/rhs-registerextendedconsent-response-schema.json"))
                .extract().response();

        assertNotNull(response);

        RegisterExtendedConsentResponse result = response.body().as(RegisterExtendedConsentResponse.class);
        assertEquals(RegisterExtendedConsentResponse.ResponseCode.ERROR, result.getResponseCode());
        assertTrue(!Strings.isNullOrEmpty(result.getResponseMessage()));
    }

}
