package se.inera.privatlakarportal.web.controller.api.dto;

import se.inera.privatlakarportal.integration.terms.services.dto.Terms;

/**
 * Created by pebe on 2015-08-25.
 */
public class GetTermsResponse {

    private Terms terms;

    public GetTermsResponse(Terms terms) {
        this.terms = terms;
    }

    public Terms getTerms() {
        return terms;
    }

    public void setTerms(Terms terms) {
        this.terms = terms;
    }

}
