package se.inera.intyg.rehabstod.web.controller.api;

import org.junit.Test;
import se.inera.intyg.rehabstod.web.BaseRestIntegrationTest;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

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
}
