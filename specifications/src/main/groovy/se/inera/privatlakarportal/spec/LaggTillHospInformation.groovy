package se.inera.privatlakarportal.spec

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.springframework.core.io.ClassPathResource
import se.inera.privatlakarportal.spec.util.RestClientFixture
import se.inera.privatlakarportal.spec.util.RestClientUtils

import static groovyx.net.http.ContentType.JSON

public class LaggTillHospInformation extends RestClientFixture {
    String personNummer
    boolean lakarbehorighet

	String responseStatus;
    def privatlakare

    public String respons() {
        return responseStatus;
    }
	
    public void execute() {
        def restClient = createRestClient()

        def resp = restClient.post(
            path: 'test/hosp/add',
            body: createPayload(),
            requestContentType: JSON)
        responseStatus = resp.status
    }

    protected createPayload() {
        def hospInformation = [
            "personalIdentityNumber": personNummer,
            "personalPrescriptionCode": "1234567",
            "educationCodes":[],
            "restrictions":[],
            "restrictionCodes":[],
            "specialityCodes":[],
            "specialityNames":[],
            "titleCodes":[],
            "hsaTitles":[]
        ]

        if (lakarbehorighet) {
            hospInformation.titleCodes = ["LK"];
            hospInformation.hsaTitles = ["Läkare"];
            hospInformation.specialityCodes = ["32","74"];
            hospInformation.specialityNames = ["Klinisk fysiologi","Nukleärmedicin"];
        }
        JsonOutput.toJson(hospInformation)
    }
}
