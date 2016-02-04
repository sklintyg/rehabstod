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
import static org.hamcrest.core.IsEqual.equalTo;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import org.junit.Test;
import se.inera.intyg.rehabstod.auth.fake.FakeCredentials;
import se.inera.intyg.rehabstod.service.Urval;
import se.inera.intyg.rehabstod.web.BaseRestIntegrationTest;
import se.inera.intyg.rehabstod.web.controller.api.dto.ChangeSelectedUnitRequest;
import se.inera.intyg.rehabstod.web.controller.api.dto.ChangeUrvalRequest;

/**
 * Created by martin on 03/02/16.
 */
public class UserApiControllerIT extends BaseRestIntegrationTest {

    private static final String API_ENDPOINT = "api/user";

    @Test
    public void testGetAnvandare() {

        RestAssured.sessionId = getAuthSession(DEFAULT_LAKARE);

        given().expect().statusCode(200).when().get(API_ENDPOINT).
                then().
                body(matchesJsonSchemaInClasspath("jsonschema/rhs-user-response-schema.json")).
                body("hsaId", equalTo(DEFAULT_LAKARE.getHsaId())).
                body("valdVardenhet.id", equalTo(DEFAULT_LAKARE.getEnhetId())).
                body("namn", equalTo(DEFAULT_LAKARE.getFornamn() + " " + DEFAULT_LAKARE.getEfternamn()));
    }

    @Test
    public void testGetAnvandareNotLoggedIn() {

        RestAssured.sessionId = null;

        given().expect().statusCode(403).when().get(API_ENDPOINT);
    }

    @Test
    public void testAndraValdEnhet() {
        // Log in as user having medarbetaruppdrag at several vardenheter.
        FakeCredentials user = new FakeCredentials.FakeCredentialsBuilder("IFV1239877878-104N", "Åsa", "Multi-vardenheter",
                "IFV1239877878-104D").lakare(false).build();
        RestAssured.sessionId = getAuthSession(user);

        //An improvement of this would be to call hsaStub rest api to add testa data as we want it to
        // avoid "magic" ids and the dependency to bootstrapped data?
        final String vardEnhetToChangeTo = "IFV1239877878-1045";
        ChangeSelectedUnitRequest changeRequest = new ChangeSelectedUnitRequest();
        changeRequest.setId(vardEnhetToChangeTo);

        given().contentType(ContentType.JSON).and().body(changeRequest).when().post(API_ENDPOINT + "/andraenhet").
                then().
                statusCode(200).
                body(matchesJsonSchemaInClasspath("jsonschema/rhs-user-response-schema.json")).
                body("valdVardenhet.id", equalTo(vardEnhetToChangeTo));
    }

    /**
     * Verify that trying to change vardEnhet to an invalid one gives an error response.
     */
    @Test
    public void testAndraValdEnhetMedOgiltigEnhetsId() {

        // Log in as user having medarbetaruppdrag at several vardenheter.
        FakeCredentials user = new FakeCredentials.FakeCredentialsBuilder("IFV1239877878-104N", "Åsa", "Multi-vardenheter",
                "IFV1239877878-1045").lakare(false).build();
        RestAssured.sessionId = getAuthSession(user);

        //An improvement of this would be to call hsaStub rest api to add testa data as we want it to
        // avoid "magic" ids and the dependency to bootstrapped data?
        final String vardEnhetToChangeTo = "non-existing-vardenehet-id";
        ChangeSelectedUnitRequest changeRequest = new ChangeSelectedUnitRequest();
        changeRequest.setId(vardEnhetToChangeTo);

        given().contentType(ContentType.JSON).and().body(changeRequest).expect().
                statusCode(500).when().post(API_ENDPOINT + "/andraenhet");
    }

    @Test
    public void testAndraUrval() {

        RestAssured.sessionId = getAuthSession(DEFAULT_LAKARE);

        final Urval urval = Urval.ALL;
        ChangeUrvalRequest changeRequest = new ChangeUrvalRequest();
        changeRequest.setUrval(urval);

        given().contentType(ContentType.JSON).and().body(changeRequest).when().post(API_ENDPOINT + "/urval").
                then().
                statusCode(200).
                body(matchesJsonSchemaInClasspath("jsonschema/rhs-user-response-schema.json")).
                body("urval", equalTo(urval.toString()));
    }

}
