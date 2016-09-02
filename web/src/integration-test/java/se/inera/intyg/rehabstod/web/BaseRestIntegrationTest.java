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
package se.inera.intyg.rehabstod.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import se.inera.intyg.rehabstod.auth.fake.FakeCredentials;
import se.inera.intyg.rehabstod.common.integration.json.CustomObjectMapper;
import se.inera.intyg.rehabstod.service.Urval;
import se.inera.intyg.rehabstod.web.controller.api.dto.ChangeSelectedUnitRequest;
import se.inera.intyg.rehabstod.web.controller.api.dto.ChangeUrvalRequest;

import javax.servlet.http.HttpServletResponse;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertNotNull;

/**
 * Base class for "REST-ish" integrationTests using RestAssured.
 * <p/>
 * Created by marced on 19/11/15.
 */
public abstract class BaseRestIntegrationTest {

    private static final Logger LOG = LoggerFactory.getLogger(BaseRestIntegrationTest.class);

    private static final String USER_JSON_FORM_PARAMETER = "userJsonDisplay";
    private static final String FAKE_LOGIN_URI = "/fake";


    public static final int OK = HttpStatus.OK.value();
    public static final int SERVER_ERROR = HttpStatus.INTERNAL_SERVER_ERROR.value();
    public static final int FORBIDDEN = HttpStatus.FORBIDDEN.value();

    protected static final FakeCredentials DEFAULT_LAKARE = new FakeCredentials.FakeCredentialsBuilder("TSTNMT2321000156-105R",
            "TSTNMT2321000156-105N").lakare(true).build();
    protected static final FakeCredentials EVA_H_LAKARE = new FakeCredentials.FakeCredentialsBuilder("eva",
            "centrum-vast").lakare(true).build();
    protected CustomObjectMapper objectMapper = new CustomObjectMapper();

    protected static final String USER_API_ENDPOINT = "api/user";
    protected static final String CHANGE_UNIT_URL = USER_API_ENDPOINT + "/andraenhet";
    protected static final String SJUKFALLSUMMARY_API_ENDPOINT = "/api/sjukfall/summary";


    /**
     * Common setup for all tests.
     */
    @Before
    public void setup() {
        RestAssured.reset();
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.baseURI = System.getProperty("integration.tests.baseUrl");
    }

    /**
     * Log in to rehabstod using the supplied FakeCredentials.
     *
     * @param fakeCredentials
     *            who to log in as
     * @return sessionId for the now authorized user session
     */
    protected String getAuthSession(FakeCredentials fakeCredentials) {
        String credentialsJson;
        try {
            credentialsJson = objectMapper.writeValueAsString(fakeCredentials);
            return getAuthSession(credentialsJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private String getAuthSession(String credentialsJson) {
        Response response = given().contentType(ContentType.URLENC).and().redirects().follow(false).and()
                .formParam(USER_JSON_FORM_PARAMETER, credentialsJson).expect()
                .statusCode(HttpServletResponse.SC_FOUND).when()
                .post(FAKE_LOGIN_URI).then().extract().response();

        assertNotNull(response.sessionId());
        return response.sessionId();
    }

    protected void selectUnitByHsaId(String unitHsaId) {
        ChangeSelectedUnitRequest req = new ChangeSelectedUnitRequest(unitHsaId);
        try {
            selectUnit(objectMapper.writeValueAsString(req));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void selectUnit(String changeUnitAsJson) throws JsonProcessingException {
        Response response = given().contentType(ContentType.JSON).and()
                .body(changeUnitAsJson).expect().statusCode(OK)
                .when()
                .post(CHANGE_UNIT_URL).then().extract().response();
        assertNotNull(response);
        LOG.info("Test selected unit " + changeUnitAsJson + ". Resp: " + response.statusCode());

    }


    public void changeUrvalTo(Urval urval) {

        ChangeUrvalRequest changeRequest = new ChangeUrvalRequest();
        changeRequest.setUrval(urval);

        given().contentType(ContentType.JSON).and().body(changeRequest).when().post(USER_API_ENDPOINT + "/urval").
                then().
                statusCode(OK).
                body(matchesJsonSchemaInClasspath("jsonschema/rhs-user-response-schema.json")).
                body("urval", equalTo(urval.toString()));
    }
}
