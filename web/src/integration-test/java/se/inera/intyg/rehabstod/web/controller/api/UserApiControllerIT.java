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
import org.junit.Test;
import se.inera.intyg.rehabstod.auth.fake.FakeCredentials;
import se.inera.intyg.rehabstod.service.Urval;
import se.inera.intyg.rehabstod.web.BaseRestIntegrationTest;
import se.inera.intyg.rehabstod.web.controller.api.dto.ChangeSelectedUnitRequest;

import java.util.Arrays;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * @author Martin Hesslund on 03/02/16.
 */
public class UserApiControllerIT extends BaseRestIntegrationTest {

    private static final String DEFAULT_LAKARE_NAME = "Emma Nilsson";

    @Test
    public void testGetAnvandare() {
        RestAssured.sessionId = getAuthSession(DEFAULT_LAKARE);
        given().expect().statusCode(OK).when().get(USER_API_ENDPOINT).
                then().
                body(matchesJsonSchemaInClasspath("jsonschema/rhs-user-response-schema.json")).
                body("hsaId", equalTo(DEFAULT_LAKARE.getHsaId())).
                body("valdVardenhet.id", equalTo(DEFAULT_LAKARE.getEnhetId())).
                body("namn", equalTo(DEFAULT_LAKARE_NAME));
    }

    @Test
    public void testAccessApiWithoutValdVardgivare() {
        FakeCredentials user = new FakeCredentials.FakeCredentialsBuilder("eva",
                "centrum-vast").lakare(true).build();
        RestAssured.sessionId = getAuthSession(user);

        //Ingen vardgivare skall vara vald som default eftersom denna user har flera att välja på.
        given().expect().statusCode(OK).when().get(USER_API_ENDPOINT).
                then().
                body(matchesJsonSchemaInClasspath("jsonschema/rhs-user-response-schema.json")).
                body("hsaId", equalTo("eva")).
                body("valdVardgivare", equalTo(null)).
                body("valdVardenhet", equalTo(null));

        //Man skall nu heller inte få gå mot apiet(med vissa undantag) utan att ha någon vardenhet vald
        given().expect().statusCode(SERVER_ERROR).when().get(SJUKFALLSUMMARY_API_ENDPOINT);
    }

    @Test
    public void testGetAnvandareNotLoggedIn() {
        RestAssured.sessionId = null;
        given().expect().statusCode(FORBIDDEN).when().get(USER_API_ENDPOINT);
    }

    @Test
    public void testAndraValdEnhet() {

        // Log in as user having medarbetaruppdrag at several vardenheter.
        FakeCredentials user = new FakeCredentials.FakeCredentialsBuilder("TSTNMT2321000156-105S",
                "TSTNMT2321000156-105N").lakare(false)
                        .systemRoles(Arrays.asList("INTYG;Rehab-TSTNMT2321000156-105N", "INTYG;Rehab-TSTNMT2321000156-105P")).build();
        RestAssured.sessionId = getAuthSession(user);

        // An improvement of this would be to call hsaStub rest api to add testa data as we want it to
        // avoid "magic" ids and the dependency to bootstrapped data?
        final String vardEnhetToChangeTo = "TSTNMT2321000156-105P";
        ChangeSelectedUnitRequest changeRequest = new ChangeSelectedUnitRequest(vardEnhetToChangeTo);

        given().contentType(ContentType.JSON).and().body(changeRequest).when().post(USER_API_ENDPOINT + "/andraenhet").then().statusCode(OK)
                .body(matchesJsonSchemaInClasspath("jsonschema/rhs-user-response-schema.json"))
                .body("valdVardenhet.id", equalTo(vardEnhetToChangeTo));
    }

    /**
     * Verify that trying to change vardEnhet to an invalid one gives an error response.
     */
    @Test
    public void testAndraValdEnhetMedOgiltigEnhetsId() {

        // Log in as user having medarbetaruppdrag at several vardenheter.
        FakeCredentials user = new FakeCredentials.FakeCredentialsBuilder("TSTNMT2321000156-105S",
                "TSTNMT2321000156-105N").lakare(false).systemRoles(Arrays.asList("INTYG;Rehab-TSTNMT2321000156-105N")).build();
        RestAssured.sessionId = getAuthSession(user);

        // An improvement of this would be to call hsaStub rest api to add testa data as we want it to
        // avoid "magic" ids and the dependency to bootstrapped data?
        final String vardEnhetToChangeTo = "non-existing-vardenehet-id";
        ChangeSelectedUnitRequest changeRequest = new ChangeSelectedUnitRequest(vardEnhetToChangeTo);

        given().contentType(ContentType.JSON).and().body(changeRequest).expect().statusCode(SERVER_ERROR).when()
                .post(USER_API_ENDPOINT + "/andraenhet");
    }

    @Test
    public void testAndraUrval() {
        RestAssured.sessionId = getAuthSession(DEFAULT_LAKARE);

        changeUrvalTo(Urval.ISSUED_BY_ME);
    }

}
