/*
 * Copyright (C) 2024 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.intyg.rehabstod.web.controller.api.dto;

import java.util.List;
import java.util.Map;
import se.inera.intyg.infra.integration.hsatk.model.legacy.SelectableVardenhet;
import se.inera.intyg.infra.integration.hsatk.model.legacy.Vardgivare;
import se.inera.intyg.infra.security.common.model.Feature;
import se.inera.intyg.infra.security.common.model.Role;
import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.service.Urval;

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

    private Urval urval;

    private boolean pdlConsentGiven = false;
    private boolean roleSwitchPossible = false;

    private Map<String, Feature> features;
    private Map<String, String> preferences;

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
        this.urval = user.getUrval();
        this.pdlConsentGiven = user.isPdlConsentGiven();
        this.roleSwitchPossible = user.isRoleSwitchPossible();
        this.features = user.getFeatures();
        this.preferences = user.getPreferences().toFrontendMap();
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

    public Urval getUrval() {
        return urval;
    }

    public void setUrval(Urval urval) {
        this.urval = urval;
    }

    public boolean isPdlConsentGiven() {
        return pdlConsentGiven;
    }

    public void setPdlConsentGiven(boolean pdlConsentGiven) {
        this.pdlConsentGiven = pdlConsentGiven;
    }

    public boolean isRoleSwitchPossible() {
        return roleSwitchPossible;
    }

    public void setRoleSwitchPossible(boolean roleSwitchPossible) {
        this.roleSwitchPossible = roleSwitchPossible;
    }

    public Map<String, Feature> getFeatures() {
        return features;
    }

    public void setFeatures(Map<String, Feature> features) {
        this.features = features;
    }

    public Map<String, String> getPreferences() {
        return preferences;
    }

    public void setPreferences(Map<String, String> preferences) {
        this.preferences = preferences;
    }

}
