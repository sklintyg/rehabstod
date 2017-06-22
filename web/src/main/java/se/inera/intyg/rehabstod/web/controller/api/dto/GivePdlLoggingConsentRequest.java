package se.inera.intyg.rehabstod.web.controller.api.dto;

/**
 * Created by eriklupander on 2017-06-19.
 */
public class GivePdlLoggingConsentRequest {

    private boolean consentGiven;

    public GivePdlLoggingConsentRequest() {
    }

    public boolean isConsentGiven() {
        return consentGiven;
    }

    public void setConsentGiven(boolean consentGiven) {
        this.consentGiven = consentGiven;
    }
}
