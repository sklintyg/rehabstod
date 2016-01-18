package se.inera.privatlakarportal.spec.util

import groovyx.net.http.RESTClient

class RestClientFixture {
    String baseUrl = System.getProperty("privatlakarportal.baseUrl") + "/api/"
    
    /**
     * Creates a RestClient which accepts all server certificates
     * @return
     */
    def createRestClient() {
        createRestClient(baseUrl)
    }

    /**
     * Creates a RestClient which accepts all server certificates
     * @return
     */
    static def createRestClient(String url) {
        def restClient = new RESTClient(url)
        restClient.ignoreSSLIssues()
        restClient
    }
}
