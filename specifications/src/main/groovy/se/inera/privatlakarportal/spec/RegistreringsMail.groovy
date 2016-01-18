package se.inera.privatlakarportal.spec

import static groovyx.net.http.ContentType.JSON
import se.inera.privatlakarportal.spec.util.RestClientFixture

/**
 * Created by pebe on 2015-09-04.
 */
class RegistreringsMail extends RestClientFixture {
    String restPath = 'stub/mails'

    String responseValue

    boolean mottaget

    public String rensaMailStubbe() {
        def restClient = createRestClient()
        def resp = restClient.delete(
            path: restPath + "/clear",
            requestContentType: JSON
        )
        resp.status
    }

    public String mailHarSkickats(String id) {
        def restClient = createRestClient()
        def resp = restClient.get(
            path: restPath,
            requestContentType: JSON
        )
        mottaget = resp.data.containsKey(id)
        responseValue = resp.data.get(id)
        //resp.data.hospInformation != null && resp.data.hospInformation.hsaTitles != null && resp.data.hospInformation.hsaTitles.contains("Läkare")
    }

    public String mailInnehåll() {
        responseValue
    }

    public Boolean mailMottaget() {
        mottaget
    }
}
