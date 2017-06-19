package se.inera.intyg.rehabstod.web.controller.api.dto;

/**
 * Created by eriklupander on 2017-06-19.
 */
public class GivePdlLoggingConsentRequest {

    private String hsaId;
    private boolean consentGiven;

    public GivePdlLoggingConsentRequest() {
    }

    public GivePdlLoggingConsentRequest(String hsaId, boolean consentGiven) {
        this.hsaId = hsaId;
        this.consentGiven = consentGiven;
    }

    public String getHsaId() {
        return hsaId;
    }

    public void setHsaId(String hsaId) {
        this.hsaId = hsaId;
    }

    public boolean isConsentGiven() {
        return consentGiven;
    }

    public void setConsentGiven(boolean consentGiven) {
        this.consentGiven = consentGiven;
    }
}
