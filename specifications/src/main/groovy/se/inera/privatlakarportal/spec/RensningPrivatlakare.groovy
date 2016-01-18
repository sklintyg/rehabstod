package se.inera.privatlakarportal.spec

import se.inera.privatlakarportal.spec.util.RestClientFixture

import static groovyx.net.http.ContentType.JSON

/**
 * Created by pebe on 2015-09-30.
 */
class RensningPrivatlakare extends RestClientFixture{

    String restPath = 'test/'

    public String sattRegistreringsdatumForPrivatlakare(String date, String id) {
        def restClient = createRestClient()
        def resp = restClient.post(
                path: restPath + '/registration/setregistrationdate/' + id,
                body: date,
                requestContentType: JSON
        )
        resp.status
    }

    public String korRensning() {
        def restClient = createRestClient()
        def resp = restClient.post(
                path: restPath + '/cleanup/trigger',
                requestContentType: JSON
        )
        resp.status
    }

    public boolean finnsPrivatlakare(String id) {
        def restClient = createRestClient()
        def resp = restClient.get(
                path: restPath + '/registration/' + id,
                requestContentType: JSON
        )
        resp.data
    }
}
