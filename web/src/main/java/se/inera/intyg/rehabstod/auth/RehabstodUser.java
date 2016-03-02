/**
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
 *
 * This file is part of rehabstod (https://github.com/sklintyg/rehabstod).
 *
 * rehabstod is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * rehabstod is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.intyg.rehabstod.auth;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import se.inera.intyg.common.integration.hsa.model.SelectableVardenhet;
import se.inera.intyg.common.integration.hsa.model.Vardgivare;
import se.inera.intyg.rehabstod.auth.authorities.AuthoritiesConstants;
import se.inera.intyg.rehabstod.auth.authorities.Privilege;
import se.inera.intyg.rehabstod.auth.authorities.Role;
import se.inera.intyg.rehabstod.auth.pdl.PDLActivityStore;
import se.inera.intyg.rehabstod.auth.pdl.PDLActivityStoreImpl;
import se.inera.intyg.rehabstod.service.Urval;

/**
 * Created by pebe on 2015-08-11.
 */
public class RehabstodUser implements Serializable {

    private static final long serialVersionUID = 8711015219408194075L;

    private String personId;
    private String hsaId;
    private String namn;
    private String titel;
    private String forskrivarkod;
    private String authenticationScheme;

    private List<Vardgivare> vardgivare;
    private List<String> befattningar;
    private List<String> specialiseringar;
    private List<String> legitimeradeYrkesgrupper;

    private SelectableVardenhet valdVardenhet;
    private SelectableVardenhet valdVardgivare;
    private Urval urval;

    // Fields related to the authority context
    private Set<String> features;
    private Map<String, Role> roles;
    private Map<String, Privilege> authorities;
    private String origin;

    // Handles PDL logging state
    private PDLActivityStore pdlActivityStore = new PDLActivityStoreImpl();

    public RehabstodUser(String hsaId, String namn) {
        this.hsaId = hsaId;
        this.namn = namn;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
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

    public String getForskrivarkod() {
        return forskrivarkod;
    }

    public void setForskrivarkod(String forskrivarkod) {
        this.forskrivarkod = forskrivarkod;
    }

    public String getAuthenticationScheme() {
        return authenticationScheme;
    }

    public void setAuthenticationScheme(String authenticationScheme) {
        this.authenticationScheme = authenticationScheme;
    }

    public List<String> getBefattningar() {
        return befattningar;
    }

    public void setBefattningar(List<String> befattningar) {
        this.befattningar = befattningar;
    }

    public List<String> getSpecialiseringar() {
        return specialiseringar;
    }

    public void setSpecialiseringar(List<String> specialiseringar) {
        this.specialiseringar = specialiseringar;
    }

    public List<String> getLegitimeradeYrkesgrupper() {
        return legitimeradeYrkesgrupper;
    }

    public void setLegitimeradeYrkesgrupper(List<String> legitimeradeYrkesgrupper) {
        this.legitimeradeYrkesgrupper = legitimeradeYrkesgrupper;
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

    public List<Vardgivare> getVardgivare() {
        return vardgivare;
    }

    public void setVardgivare(List<Vardgivare> vardgivare) {
        this.vardgivare = vardgivare;
    }

    public Urval getUrval() {
        return urval;
    }

    public boolean changeSelectedUrval(Urval urval) {
        if (isValidUrvalChange(urval)) {
            this.urval = urval;
            return true;
        } else {
            return false;
        }

    }

    @SuppressWarnings("PMD.SimplifyBooleanReturns")
    private boolean isValidUrvalChange(Urval urval) {
        // Unset is always allowed
        if (urval == null) {
            return true;
        }

        // If we dont have a role, we can't decide which urval change is allowed, so..
        if (roles == null) {
            return false;
        }

        // Case 1: Lakare is only allowed to set ISSUED_BY_ME
        if (urval == Urval.ISSUED_BY_ME && roles.containsKey(AuthoritiesConstants.ROLE_LAKARE)) {
            return true;
        }

        // Case 2: Koordinator is only allowed to set ALL
        if (urval == Urval.ALL && roles.containsKey(AuthoritiesConstants.ROLE_KOORDINATOR)) {
            return true;
        }

        // No other case allowed
        return false;

    }

    public boolean changeValdVardenhet(String vardenhetId) {
        if (vardenhetId == null) {
            return false;
        }

        for (Vardgivare vg : getVardgivare()) {
            SelectableVardenhet ve = vg.findVardenhet(vardenhetId);
            if (ve != null) {
                setValdVardenhet(ve);
                setValdVardgivare(vg);
                return true;
            }
        }

        return false;
    }

    public int getTotaltAntalVardenheter() {
        // count all hasid's in the datastructure
        return (int) getVardgivare().stream().flatMap(vg -> vg.getHsaIds().stream()).count();
    }

    public Set<String> getFeatures() {
        return features;
    }

    public void setFeatures(Set<String> features) {
        this.features = features;
    }

    public Map<String, Role> getRoles() {
        return roles;
    }

    public void setRoles(Map<String, Role> roles) {
        this.roles = roles;
    }

    public Map<String, Privilege> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Map<String, Privilege> authorities) {
        this.authorities = authorities;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public PDLActivityStore getPdlActivityStore() {
        return pdlActivityStore;
    }

    public void setPdlActivityStore(PDLActivityStore pdlActivityStore) {
        this.pdlActivityStore = pdlActivityStore;
    }
}
