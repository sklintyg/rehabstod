package se.inera.intyg.rehabstod.web.controller.api.dto;

import se.inera.intyg.rehabstod.service.model.HospInformation;

/**
 * Created by pebe on 2015-08-13.
 */
public class GetHospInformationResponse {

    private HospInformation hospInformation;

    public GetHospInformationResponse(HospInformation hospInformation) {
        this.hospInformation = hospInformation;
    }

    public HospInformation getHospInformation() {
        return hospInformation;
    }

    public void setHospInformation(HospInformation hospInformation) {
        this.hospInformation = hospInformation;
    }

}
