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

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import org.junit.Test;
import se.inera.intyg.rehabstod.common.integration.json.CustomObjectMapper;
import se.inera.intyg.rehabstod.web.BaseRestIntegrationTest;
import se.inera.intyg.rehabstod.web.controller.api.dto.RegisterExtendedConsentRequest;
import se.inera.intyg.rehabstod.web.controller.api.dto.RegisterExtendedConsentResponse;
import se.inera.intyg.rehabstod.web.controller.api.dto.RegisterIncludeInSjukfallRequest;
import se.inera.intyg.schemas.contract.Personnummer;

import javax.ws.rs.core.UriBuilder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by Magnus Ekstrand on 2018-10-28.
 */
public class ConsentControllerIT extends BaseRestIntegrationTest {

    private static final String API_ENDPOINT = "api/consent";
    private static final String API_STUB_ENDPOINT = "services/api/stub/samtyckestjanst-api/consent";

    // Use Tolvan Tolvansson since he exist in the stubbed data.
    private static final String PNR_TOLVAN_TOLVANSSON = "19121212-1212";

    @Test
    public void testRegisterConsent() throws Exception {
        RestAssured.sessionId = getAuthSession(DEFAULT_LAKARE);
        cleanupConsentsInStore();

        // 1. Register a consent
        RegisterExtendedConsentRequest request = new RegisterExtendedConsentRequest();
        request.setPatientId(PNR_TOLVAN_TOLVANSSON);
        request.setDays(7);

        Response response = given().contentType(ContentType.JSON).and().body(request).expect().statusCode(OK)
                .when().post(API_ENDPOINT)
                .then().body(matchesJsonSchemaInClasspath("jsonschema/rhs-registerextendedconsent-response-schema.json"))
                .extract().response();

        assertNotNull(response);
        RegisterExtendedConsentResponse result = response.body().as(RegisterExtendedConsentResponse.class);
        assertEquals(RegisterExtendedConsentResponse.ResponseCode.OK, result.getResponseCode());
        assertTrue(Strings.isNullOrEmpty(result.getResponseMessage()));
        assertEquals(DEFAULT_LAKARE.getHsaId(), result.getRegisteredBy());

        // 2. Do check that consent is in the store
        assertTrue(hasConsentBeenRegistered(DEFAULT_VG_HSAID, DEFAULT_VE_HSAID, PNR_TOLVAN_TOLVANSSON));
        assertFalse(hasConsentBeenRegistered(DEFAULT_VG_HSAID, DEFAULT_VE_HSAID, "20121212-1212"));
    }

    @Test
    public void testRegisterConsentForCurrentUserOnly() throws Exception {
        RestAssured.sessionId = getAuthSession(DEFAULT_LAKARE);
        cleanupConsentsInStore();

        // 1. Register a consent
        RegisterExtendedConsentRequest request = new RegisterExtendedConsentRequest();
        request.setOnlyCurrentUser(true);
        request.setPatientId(PNR_TOLVAN_TOLVANSSON);
        request.setDays(7);

        Response response = given().contentType(ContentType.JSON).and().body(request).expect().statusCode(OK)
                .when().post(API_ENDPOINT)
                .then().body(matchesJsonSchemaInClasspath("jsonschema/rhs-registerextendedconsent-response-schema.json"))
                .extract().response();

        assertNotNull(response);
        RegisterExtendedConsentResponse result = response.body().as(RegisterExtendedConsentResponse.class);
        assertEquals(RegisterExtendedConsentResponse.ResponseCode.OK, result.getResponseCode());
        assertTrue(Strings.isNullOrEmpty(result.getResponseMessage()));
        assertEquals(DEFAULT_LAKARE.getHsaId(), result.getRegisteredBy());

        // 2. Do check that consent is in the store
        assertTrue(hasConsentBeenRegisteredToCurrentUserOnly(
                DEFAULT_VG_HSAID, DEFAULT_VE_HSAID, DEFAULT_LAKARE_HSAID, PNR_TOLVAN_TOLVANSSON));
    }

    @Test
    public void testRegisterConsentWithNoPatientId() {
        RestAssured.sessionId = getAuthSession(DEFAULT_LAKARE);
        cleanupConsentsInStore();

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

    @Test
    public void testIncludeVgInSjukfall() {
        RestAssured.sessionId = getAuthSession(DEFAULT_LAKARE);
        cleanupConsentsInStore();

        RegisterIncludeInSjukfallRequest request = new RegisterIncludeInSjukfallRequest();
        request.setPatientId(PNR_TOLVAN_TOLVANSSON);
        request.setVardgivareId("vg1");

        Response response = given().contentType(ContentType.JSON).and().body(request).expect().statusCode(OK)
                .when().post(API_ENDPOINT + "/includeVg")
                .then().body(matchesJsonSchemaInClasspath("jsonschema/rhs-include-vg-in-sjukfall-response-schema.json"))
                .extract().response();

        assertNotNull(response);

        Collection<String> result = response.body().as(Collection.class);
        assertEquals(1, result.size());
    }

    private void cleanupConsentsInStore() {
        String contextPath = API_STUB_ENDPOINT;
        URL url = getUrl(contextPath, new HashMap<>());

        HttpURLConnection httpURLConnection = null;
        try {
            httpURLConnection = getHttpURLConnection(url, "DELETE", false);
            int statusCode = httpURLConnection.getResponseCode();
            assertEquals(200, statusCode);
        } catch (IOException e) {
            fail(e.getMessage());
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
    }

    private static Personnummer createPnr(String personId) {
        return Personnummer.createPersonnummer(personId)
                .orElseThrow(() -> new IllegalArgumentException("Could not parse passed personnummer: " + personId));
    }

    private StringBuffer getContent(HttpURLConnection con, boolean closeConnection) throws IOException {
        StringBuffer content = new StringBuffer();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
        }

        if (closeConnection) {
            if (con != null) {
                con.disconnect();
            }
        }

        return content;
    }

    private HttpURLConnection getHttpURLConnection(URL url) throws IOException {
        return getHttpURLConnection(url, null, true);
    }

    private HttpURLConnection getHttpURLConnection(URL url, String httpMethod, boolean doOutput) {
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(doOutput);

            if (!Strings.isNullOrEmpty(httpMethod)) {
                con.setRequestMethod(httpMethod);
            }
        } catch (IOException e) {
            fail(e.getMessage());
        }

        return con;
    }

    private URL getUrl(String contextPath, Map<String, String> parameters) {
        try {
            return UriBuilder.fromPath(RestAssured.baseURI)
                    .path(contextPath)
                    .queryParam(ParameterStringBuilder.getParamsString(parameters))
                    .build()
                    .toURL();
        } catch (MalformedURLException | UnsupportedEncodingException exception) {
            fail(exception.getMessage());
        }

        return null;
    }
    private boolean hasConsentBeenRegistered(String vgHsaId, String veHsaId, String personId) throws Exception {
        Map<String, String> parameters = ImmutableMap.of(
                "vardgivareId", vgHsaId,
                "vardenhetId", veHsaId);

        String contextPath = String.format("services/api/stub/samtyckestjanst-api/consent/%s", personId);

        URL url = getUrl(contextPath, parameters);
        HttpURLConnection con = getHttpURLConnection(url);
        StringBuffer content = getContent(con, true);

        return new Boolean(content.toString());
    }

    private boolean hasConsentBeenRegisteredToCurrentUserOnly(
            String vgHsaId, String veHsaId, String userHsaId, String personId) throws Exception {

        Personnummer pnr = createPnr(personId);

        String contextPath = "services/api/stub/samtyckestjanst-api/consent";

        URL url = getUrl(contextPath, new HashMap<>());
        HttpURLConnection con = getHttpURLConnection(url);
        StringBuffer content = getContent(con, true);

        JsonNode jsonNode = parseContentAsJson(content.toString());
        if (!jsonNode.isNull() && jsonNode.isArray()) {
            Iterator<JsonNode> it = jsonNode.iterator();
            while (it.hasNext()) {
                JsonNode n = it.next();
                System.out.println(n.toString());
                if (hasValue(n, "vardgivareId", vgHsaId)
                        && hasValue(n, "vardenhetId", veHsaId)
                        && hasValue(n, "employeeId", userHsaId)
                        && hasValue(n, "patientId", pnr.getPersonnummer())) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean hasValue(JsonNode jsonNode, String fieldName, String value) {
        try {
            return jsonNode.get(fieldName).asText().equals(value);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    private JsonNode parseContentAsJson(String jsonString) throws JsonParseException, IOException {
        CustomObjectMapper mapper = new CustomObjectMapper();
        JsonFactory factory = mapper.getFactory();
        JsonParser parser = factory.createParser(jsonString);
        return mapper.readTree(parser);
    }

    static class ParameterStringBuilder {
        public static String getParamsString(Map<String, String> params)
                throws UnsupportedEncodingException {
            StringBuilder result = new StringBuilder();

            for (Map.Entry<String, String> entry : params.entrySet()) {
                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                result.append("&");
            }

            String resultString = result.toString();
            return resultString.length() > 0
                    ? resultString.substring(0, resultString.length() - 1)
                    : resultString;
        }
    }
}
