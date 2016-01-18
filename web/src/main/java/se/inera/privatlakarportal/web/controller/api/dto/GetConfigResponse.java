package se.inera.privatlakarportal.web.controller.api.dto;

import java.util.Map;

/**
 * Created by pebe on 2015-08-28.
 */
public class GetConfigResponse {
    private String webcertUrl;
    private String webcertStartUrl;
    private Map<String, String> befattningar;
    private Map<String, String> vardformer;
    private Map<String, String> verksamhetstyper;

    public GetConfigResponse(String webcertUrl, String webcertStartUrl, Map<String, String> befattningar,
                             Map<String, String> vardformer, Map<String, String> verksamhetstyper) {
        this.webcertUrl = webcertUrl;
        this.webcertStartUrl = webcertStartUrl;
        this.befattningar = befattningar;
        this.vardformer = vardformer;
        this.verksamhetstyper = verksamhetstyper;
    }

    public String getWebcertUrl() {
        return webcertUrl;
    }

    public String getWebcertStartUrl() {
        return webcertStartUrl;
    }

    public Map<String, String> getBefattningar() {
        return befattningar;
    }

    public Map<String, String> getVardformer() {
        return vardformer;
    }

    public Map<String, String> getVerksamhetstyper() {
        return verksamhetstyper;
    }
}
