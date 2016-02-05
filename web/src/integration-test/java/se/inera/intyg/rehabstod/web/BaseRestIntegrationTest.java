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

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertNotNull;

import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.springframework.http.HttpStatus;

import se.inera.intyg.common.util.integration.integration.json.CustomObjectMapper;
import se.inera.intyg.rehabstod.auth.fake.FakeCredentials;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;

/**
 * Base class for "REST-ish" integrationTests using RestAssured.
 * <p/>
 * Created by marced on 19/11/15.
 */
public abstract class BaseRestIntegrationTest {

    private static final String USER_JSON_FORM_PARAMETER = "userJsonDisplay";
    private static final String FAKE_LOGIN_URI = "/fake";

    public static final int OK = HttpStatus.OK.value();
    public static final int SERVER_ERROR = HttpStatus.INTERNAL_SERVER_ERROR.value();
    public static final int FORBIDDEN = HttpStatus.FORBIDDEN.value();

    protected static final FakeCredentials DEFAULT_LAKARE = new FakeCredentials.FakeCredentialsBuilder("IFV1239877878-1049", "rest", "testman",
            "IFV1239877878-1042").lakare(true).build();
    protected CustomObjectMapper objectMapper = new CustomObjectMapper();

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
}
