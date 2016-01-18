package se.inera.intyg.rehabstod.web.controller.api.dto;

/**
 * Created by marced on 2016-01-18.
 */
public class GetConfigResponse {
    private String webcertUrl;
    private String webcertStartUrl;


    public GetConfigResponse(String webcertUrl, String webcertStartUrl) {
        this.webcertUrl = webcertUrl;
        this.webcertStartUrl = webcertStartUrl;

    }

    public String getWebcertUrl() {
        return webcertUrl;
    }

    public String getWebcertStartUrl() {
        return webcertStartUrl;
    }


}
