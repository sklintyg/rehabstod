package se.inera.privatlakarportal.web.controller.api.dto;

import se.inera.privatlakarportal.service.postnummer.model.Omrade;

import java.util.List;

/**
 * Created by pebe on 2015-08-12.
 */
public class GetOmradeResponse {

    private List<Omrade> omradeList;

    public GetOmradeResponse(List<Omrade> omradeList) {
        this.omradeList = omradeList;
    }

    public List<Omrade> getOmradeList() {
        return omradeList;
    }

    public void setOmradeList(List<Omrade> omradeList) {
        this.omradeList = omradeList;
    }
}
