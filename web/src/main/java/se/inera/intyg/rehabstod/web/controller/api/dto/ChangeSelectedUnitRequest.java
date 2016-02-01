package se.inera.intyg.rehabstod.web.controller.api.dto;

/**
 * Created by marced on 01/02/16.
 */
public class ChangeSelectedUnitRequest {
    private String id;

    public ChangeSelectedUnitRequest() {
    }

    public ChangeSelectedUnitRequest(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
