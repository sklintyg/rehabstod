package se.inera.privatlakarportal.spec

import se.inera.privatlakarportal.spec.util.RestClientFixture
import se.inera.privatlakarportal.spec.util.RestClientUtils

import static groovyx.net.http.ContentType.JSON

/**
 * Created by pebe on 2015-09-04.
 */
class HospUppdatering extends RestClientFixture {
    String restPath = 'test/hosp/'

    public String taBortHospInformation(String id) {
        def restClient = createRestClient()
        def resp = restClient.delete(
            path: restPath + "remove/" + id,
            requestContentType: JSON
        )
        resp.status
    }

    public String korHospUppdatering() {
        def restClient = createRestClient()
        def resp = restClient.post(
            path: restPath + "update",
            requestContentType: JSON
        )
        resp.status
    }

    public boolean harLakarbehorighet() {
        def restClient = createRestClient()
        RestClientUtils.login(restClient, "Björn Anders Daniel", "Pärsson", "195206172339");
        def resp = restClient.get(
            path: "/api/registration"
        )
        resp.data.hospInformation != null && resp.data.hospInformation.hsaTitles != null && resp.data.hospInformation.hsaTitles.contains("Läkare")
    }

    public String loggaInGenomWebcert(String personId) {
        def restClient = createRestClient()
        def resp = restClient.post(
            path: 'test/webcert/validatePrivatePractitioner/' + personId,
            requestContentType: JSON
        )
        resp.data.resultCode
    }
}
