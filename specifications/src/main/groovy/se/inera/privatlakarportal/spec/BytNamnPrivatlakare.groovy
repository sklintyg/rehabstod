package se.inera.privatlakarportal.spec

import se.inera.privatlakarportal.spec.util.RestClientFixture
import se.inera.privatlakarportal.spec.util.RestClientUtils

import static groovyx.net.http.ContentType.JSON

public class BytNamnPrivatlakare extends RestClientFixture {

    def restClient = createRestClient()

    String id
    String namn
    String restPath = 'test/registration/setname/'
    boolean responseStatus

    public void reset() {
    }

    public void execute() {
        RestClientUtils.login(restClient)
        def resp = restClient.post(
                path: restPath + id,
                body: namn,
                requestContentType: JSON
        )
        responseStatus = resp.status
    }

    public boolean resultat() {
        responseStatus
    }
}
