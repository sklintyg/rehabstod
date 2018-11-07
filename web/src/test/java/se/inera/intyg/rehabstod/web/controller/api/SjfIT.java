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

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import se.inera.intyg.rehabstod.web.BaseRestIntegrationTest;
import se.inera.intyg.rehabstod.web.controller.api.dto.GetSjukfallRequest;
import se.inera.intyg.rehabstod.web.controller.api.dto.RegisterExtendedConsentRequest;
import se.inera.intyg.rehabstod.web.controller.api.dto.RegisterIncludeInSjukfallRequest;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;

public class SjfIT extends BaseRestIntegrationTest {

    private static final String API_ENDPOINT_PATIENT = "api/sjukfall/patient";
    private static final String API_ENDPOINT_CONSENT = "api/consent";

    private static final String JSONSCHEMA_PATIENT = "jsonschema/rhs-sjukfallpatient-response-schema.json";
    private static final String JSONSCHEMA_CONSENT = "jsonschema/rhs-registerextendedconsent-response-schema.json";

    @Test
    public void testGetSjukfallByPatient() {
        String patientId = "19121212-1212";
        String vgId = "no-user-vg";

        RestAssured.sessionId = getAuthSession(DEFAULT_LAKARE);

        GetSjukfallRequest request = new GetSjukfallRequest();
        request.setPatientId(patientId);

        // reset samtycke
        given().contentType(ContentType.JSON)
                .expect().statusCode(OK)
                .when().delete("services/api/stub/samtyckestjanst-api/consent");


        // Kollar att inget samtycke finns
        given().contentType(ContentType.JSON).and().body(request)
                .expect().statusCode(OK)
                .when().post(API_ENDPOINT_PATIENT).then()
                .body(matchesJsonSchemaInClasspath(JSONSCHEMA_PATIENT))
                .body("sjfMetaData.samtyckeFinns", equalTo(false))
                .body("sjfMetaData.kraverSamtycke.size()", equalTo(1))
                .body("sjfMetaData.kraverSamtycke[0].includedInSjukfall", equalTo(false));


        // Registerar samtycke

        RegisterExtendedConsentRequest consentRequest = new RegisterExtendedConsentRequest();
        consentRequest.setDays(1);
        consentRequest.setPatientId(patientId);

        given().contentType(ContentType.JSON).and().body(consentRequest)
                .expect().statusCode(OK)
                .when().post(API_ENDPOINT_CONSENT).then()
                .body(matchesJsonSchemaInClasspath(JSONSCHEMA_CONSENT))
                .body("responseCode", equalTo("OK"));


        // Kollar att samtycke finns
        given().contentType(ContentType.JSON).and().body(request)
                .expect().statusCode(OK)
                .when().post(API_ENDPOINT_PATIENT).then()
                .body(matchesJsonSchemaInClasspath(JSONSCHEMA_PATIENT))
                .body("sjfMetaData.samtyckeFinns", equalTo(true))
                .body("sjfMetaData.kraverSamtycke.size()", equalTo(1))
                .body("sjfMetaData.kraverSamtycke[0].includedInSjukfall", equalTo(false));


        // Inkludera vårdgivare
        RegisterIncludeInSjukfallRequest includeInSjukfallRequest = new RegisterIncludeInSjukfallRequest();
        includeInSjukfallRequest.setPatientId(patientId);
        includeInSjukfallRequest.setVardgivareId(vgId);

        given().contentType(ContentType.JSON).and().body(includeInSjukfallRequest)
                .expect().statusCode(OK)
                .when().post(API_ENDPOINT_CONSENT + "/includeVg");


        // Kollar att vårdgivaren kommer med
        given().contentType(ContentType.JSON).and().body(request)
                .expect().statusCode(OK)
                .when().post(API_ENDPOINT_PATIENT).then()
                .body(matchesJsonSchemaInClasspath(JSONSCHEMA_PATIENT))
                .body("sjfMetaData.samtyckeFinns", equalTo(true))
                .body("sjfMetaData.kraverSamtycke.size()", equalTo(1))
                .body("sjfMetaData.kraverSamtycke[0].includedInSjukfall", equalTo(true));


    }
}
