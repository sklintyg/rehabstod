package se.inera.privatlakarportal.spec

import se.inera.privatlakarportal.spec.util.RestClientFixture
import se.inera.privatlakarportal.spec.util.RestClientUtils

import static groovyx.net.http.ContentType.JSON

public class TaBortPrivatlakare extends RestClientFixture {

    def restClient = createRestClient()

    String id
    String restPath = 'test/registration/remove/'
    boolean responseStatus

    public void reset() {
    }

    public void execute() {
        RestClientUtils.login(restClient)
        def resp = restClient.delete(
                path: restPath + id,
                requestContentType: JSON
        )
        responseStatus = resp.status
    }

    public boolean resultat() {
        responseStatus
    }
}
