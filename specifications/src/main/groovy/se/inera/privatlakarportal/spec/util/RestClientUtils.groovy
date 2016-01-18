package se.inera.privatlakarportal.spec.util

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC
import groovy.json.JsonOutput
import groovyx.net.http.HttpResponseDecorator

class RestClientUtils extends RestClientFixture{

    static def login() {
        def restclient = super.createRestClient()
        login(restclient)
    }

    static def login(def restClient, def firstName = "Oskar", def lastName = "Johansson", def personId = "199008252398") {
        def loginData = JsonOutput.toJson([ firstName: firstName, lastName: lastName, personId: personId ])
        def response = restClient.post(path: '/fake', body: "userJsonDisplay=${loginData}", requestContentType : URLENC )
        assert response.status == 302
        System.out.println("Using logindata: " + loginData)
    }
}
