package se.inera.intyg.rehabstod.web.controller.api.dto;

import java.util.List;
import java.util.Map;

import se.inera.intyg.common.integration.hsa.model.SelectableVardenhet;
import se.inera.intyg.common.integration.hsa.model.Vardgivare;
import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.auth.authorities.Role;

/**
 * Reponse dto for the getUser api.
 */
public class GetUserResponse {

    private String hsaId;
    private String namn;
    private String titel;
    private String authenticationScheme;

    private List<Vardgivare> vardgivare;
    private List<String> befattningar;

    private SelectableVardenhet valdVardenhet;
    private SelectableVardenhet valdVardgivare;

    private Map<String, Role> roles;

    private int totaltAntalVardenheter;

    public GetUserResponse(RehabstodUser user) {
        this.hsaId = user.getHsaId();
        this.namn = user.getNamn();
        this.roles = user.getRoles();
        this.titel = user.getTitel();

        this.authenticationScheme = user.getAuthenticationScheme();
        this.befattningar = user.getBefattningar();

        this.vardgivare = user.getVardgivare();
        this.valdVardgivare = user.getValdVardgivare();
        this.valdVardenhet = user.getValdVardenhet();
        this.totaltAntalVardenheter = user.getTotaltAntalVardenheter();
    }

    public String getHsaId() {
        return hsaId;
    }

    public void setHsaId(String hsaId) {
        this.hsaId = hsaId;
    }

    public String getNamn() {
        return namn;
    }

    public void setNamn(String namn) {
        this.namn = namn;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public String getAuthenticationScheme() {
        return authenticationScheme;
    }

    public void setAuthenticationScheme(String authenticationScheme) {
        this.authenticationScheme = authenticationScheme;
    }

    public List<Vardgivare> getVardgivare() {
        return vardgivare;
    }

    public void setVardgivare(List<Vardgivare> vardgivare) {
        this.vardgivare = vardgivare;
    }

    public List<String> getBefattningar() {
        return befattningar;
    }

    public void setBefattningar(List<String> befattningar) {
        this.befattningar = befattningar;
    }

    public SelectableVardenhet getValdVardenhet() {
        return valdVardenhet;
    }

    public void setValdVardenhet(SelectableVardenhet valdVardenhet) {
        this.valdVardenhet = valdVardenhet;
    }

    public SelectableVardenhet getValdVardgivare() {
        return valdVardgivare;
    }

    public void setValdVardgivare(SelectableVardenhet valdVardgivare) {
        this.valdVardgivare = valdVardgivare;
    }

    public Map<String, Role> getRoles() {
        return roles;
    }

    public void setRoles(Map<String, Role> roles) {
        this.roles = roles;
    }

    public int getTotaltAntalVardenheter() {
        return totaltAntalVardenheter;
    }

    public void setTotaltAntalVardenheter(int totaltAntalVardenheter) {
        this.totaltAntalVardenheter = totaltAntalVardenheter;
    }

}
