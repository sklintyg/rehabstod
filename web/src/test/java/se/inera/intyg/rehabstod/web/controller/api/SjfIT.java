/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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

import org.junit.After;
import org.junit.Test;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import se.inera.intyg.rehabstod.web.BaseRestIntegrationTest;
import se.inera.intyg.rehabstod.web.controller.api.dto.AddVeToPatientViewRequest;
import se.inera.intyg.rehabstod.web.controller.api.dto.AddVgToPatientViewRequest;
import se.inera.intyg.rehabstod.web.controller.api.dto.GetSjukfallRequest;
import se.inera.intyg.rehabstod.web.controller.api.dto.RegisterExtendedConsentRequest;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;

public class SjfIT extends BaseRestIntegrationTest {

    private static final String API_ENDPOINT_INCLUDE_VG = "api/sjukfall/patient/addVardgivare";
    private static final String API_ENDPOINT_INCLUDE_VE = "api/sjukfall/patient/addVardenhet";
    private static final String API_ENDPOINT_PATIENT = "api/sjukfall/patient";
    private static final String API_ENDPOINT_CONSENT = "api/consent";

    private static final String JSONSCHEMA_PATIENT = "jsonschema/rhs-sjukfallpatient-response-schema.json";
    private static final String JSONSCHEMA_CONSENT = "jsonschema/rhs-registerextendedconsent-response-schema.json";

    @After
    public void cleanup() {
        sleep(200);
    }

    @Test
    public void testGetSjukfallByPatient() {
        String patientId = "20121212-1212";

        // Vårdgivare
        String vgOther_id = "TSTNMT2321000156-1061";
        String vgOtherBlocked_id = "vastmanland";
        String vgOtherBlocked_name = "Landstinget Västmanland";

        // Vårdenheter inom samma vårdgivare
        String veOther_id = "TSTNMT2321000156-105P";
        String veOther_name = "Rehabstöd Enhet 2";
        String veOtherBlocked_id = "TSTNMT2321000156-105Q";
        String veOtherBlocked_name = "Rehabstöd Enhet 3";

        RestAssured.sessionId = getAuthSession(DEFAULT_LAKARE);

        // reset samtycke
        given().contentType(ContentType.JSON)
                .expect().statusCode(OK)
                .when().delete("services/api/stub/samtyckestjanst-api/consent");

        GetSjukfallRequest request = new GetSjukfallRequest();
        request.setPatientId(patientId);

        // Registerar samtycke
        RegisterExtendedConsentRequest consentRequest = new RegisterExtendedConsentRequest();
        consentRequest.setDays(1);
        consentRequest.setPatientId(patientId);

        given().contentType(ContentType.JSON).and().body(consentRequest)
                .expect().statusCode(OK)
                .when().post(API_ENDPOINT_CONSENT).then()
                .body(matchesJsonSchemaInClasspath(JSONSCHEMA_CONSENT))
                .body("responseCode", equalTo("OK"));

        sleep(200);

        // Kollar att samtycke finns och hur många som har spärr
        given().contentType(ContentType.JSON).and().body(request)
                .expect().statusCode(OK)
                .when().post(API_ENDPOINT_PATIENT).then()
                .body(matchesJsonSchemaInClasspath(JSONSCHEMA_PATIENT))
                .body("sjfMetaData.samtyckeFinns", equalTo(true))
                .body("sjfMetaData.kraverSamtycke.size()", equalTo(2))
                .body("sjfMetaData.kraverInteSamtycke.size()", equalTo(2))
                .body("sjfMetaData.kraverSamtycke.find { it.itemId == '" + vgOther_id + "' }.includedInSjukfall",
                        equalTo(false))
                .body("sjfMetaData.kraverSamtycke.find { it.itemId == '" + vgOtherBlocked_id + "' }.includedInSjukfall",
                        equalTo(false))
                .body("sjfMetaData.kraverInteSamtycke.find { it.itemId == '" + veOther_id + "' }.includedInSjukfall",
                        equalTo(false))
                .body("sjfMetaData.kraverInteSamtycke.find { it.itemId == '" + veOtherBlocked_id + "' }.includedInSjukfall",
                        equalTo(false))
                .body("sjfMetaData.vardenheterInomVGMedSparr.size()", equalTo(1))
                .body("sjfMetaData.andraVardgivareMedSparr.size()", equalTo(2))
                .body("sjfMetaData.blockingServiceError", equalTo(false))
                .body("sjfMetaData.consentServiceError", equalTo(false));

        String puSekretessUrl = "services/api/pu-api/person/" + patientId + "/sekretessmarkerad?value=";

        // Sätter sekretessmarkerad
        given().contentType(ContentType.JSON)
                .expect().statusCode(OK)
                .when().get(puSekretessUrl + "true");

        // Kollar att ingen sjf info kommer med
        given().contentType(ContentType.JSON).and().body(request)
                .expect().statusCode(OK)
                .when().post(API_ENDPOINT_PATIENT).then()
                .body(matchesJsonSchemaInClasspath(JSONSCHEMA_PATIENT))
                .body("sjfMetaData.samtyckeFinns", equalTo(false))
                .body("sjfMetaData.kraverSamtycke.size()", equalTo(0))
                .body("sjfMetaData.kraverInteSamtycke.size()", equalTo(0))
                .body("sjfMetaData.vardenheterInomVGMedSparr.size()", equalTo(0))
                .body("sjfMetaData.andraVardgivareMedSparr.size()", equalTo(0))
                .body("sjfMetaData.blockingServiceError", equalTo(false))
                .body("sjfMetaData.consentServiceError", equalTo(false));

        // Tar bort sekretess markerad
        given().contentType(ContentType.JSON)
                .expect().statusCode(OK)
                .when().get(puSekretessUrl + "false");

        // Inkludera vårdgivare
        AddVgToPatientViewRequest includeVgInSjukfallRequest = new AddVgToPatientViewRequest();
        includeVgInSjukfallRequest.setPatientId(patientId);
        includeVgInSjukfallRequest.setVardgivareId(vgOther_id);

        given().contentType(ContentType.JSON).and().body(includeVgInSjukfallRequest)
                .expect().statusCode(OK)
                .when().post(API_ENDPOINT_INCLUDE_VG);

        // Kollar att vårdgivaren kommer med
        given().contentType(ContentType.JSON).and().body(request)
                .expect().statusCode(OK)
                .when().post(API_ENDPOINT_PATIENT).then()
                .body(matchesJsonSchemaInClasspath(JSONSCHEMA_PATIENT))
                .body("sjfMetaData.samtyckeFinns", equalTo(true))
                .body("sjfMetaData.kraverSamtycke.size()", equalTo(2))
                .body("sjfMetaData.kraverInteSamtycke.size()", equalTo(2))
                .body("sjfMetaData.kraverSamtycke.find { it.itemId == '" + vgOther_id + "' }.includedInSjukfall",
                        equalTo(true))
                .body("sjfMetaData.kraverSamtycke.find { it.itemId == '" + vgOtherBlocked_id + "' }.includedInSjukfall",
                        equalTo(false))
                .body("sjfMetaData.kraverInteSamtycke.find { it.itemId == '" + veOther_id + "' }.includedInSjukfall",
                        equalTo(false))
                .body("sjfMetaData.kraverInteSamtycke.find { it.itemId == '" + veOtherBlocked_id + "' }.includedInSjukfall",
                        equalTo(false))
                .body("sjfMetaData.vardenheterInomVGMedSparr.size()", equalTo(1))
                .body("sjfMetaData.andraVardgivareMedSparr.size()", equalTo(2))
                .body("sjfMetaData.blockingServiceError", equalTo(false))
                .body("sjfMetaData.consentServiceError", equalTo(false));


        // Inkludera vårdenhet
        AddVeToPatientViewRequest includeVeInSjukfallRequest = new AddVeToPatientViewRequest();
        includeVeInSjukfallRequest.setPatientId(patientId);
        includeVeInSjukfallRequest.setVardenhetId(veOther_id);

        given().contentType(ContentType.JSON).and().body(includeVeInSjukfallRequest)
                .expect().statusCode(OK)
                .when().post(API_ENDPOINT_INCLUDE_VE);

        // Kollar att vårdenheten kommer med
        given().contentType(ContentType.JSON).and().body(request)
                .expect().statusCode(OK)
                .when().post(API_ENDPOINT_PATIENT).then()
                .body(matchesJsonSchemaInClasspath(JSONSCHEMA_PATIENT))
                .body("sjfMetaData.samtyckeFinns", equalTo(true))
                .body("sjfMetaData.kraverSamtycke.size()", equalTo(2))
                .body("sjfMetaData.kraverInteSamtycke.size()", equalTo(2))
                .body("sjfMetaData.kraverSamtycke.find { it.itemId == '" + vgOther_id + "' }.includedInSjukfall",
                        equalTo(true))
                .body("sjfMetaData.kraverSamtycke.find { it.itemId == '" + vgOtherBlocked_id + "' }.includedInSjukfall",
                        equalTo(false))
                .body("sjfMetaData.kraverInteSamtycke.find { it.itemId == '" + veOther_id + "' }.includedInSjukfall",
                        equalTo(true))
                .body("sjfMetaData.kraverInteSamtycke.find { it.itemId == '" + veOtherBlocked_id + "' }.includedInSjukfall",
                        equalTo(false))
                .body("sjfMetaData.vardenheterInomVGMedSparr.size()", equalTo(1))
                .body("sjfMetaData.andraVardgivareMedSparr.size()", equalTo(2))
                .body("sjfMetaData.blockingServiceError", equalTo(false))
                .body("sjfMetaData.consentServiceError", equalTo(false));
    }
}
