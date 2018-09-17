/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.auth;

import se.inera.intyg.infra.integration.hsa.model.AbstractVardenhet;
import se.inera.intyg.infra.integration.hsa.model.Mottagning;
import se.inera.intyg.infra.integration.hsa.model.Vardenhet;
import se.inera.intyg.infra.integration.hsa.model.Vardgivare;
import se.inera.intyg.infra.security.common.model.IntygUser;
import se.inera.intyg.rehabstod.auth.authorities.AuthoritiesConstants;
import se.inera.intyg.rehabstod.auth.pdl.PDLActivityEntry;
import se.inera.intyg.rehabstod.auth.util.SystemRolesParser;
import se.inera.intyg.rehabstod.service.Urval;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author pebe on 2015-08-11.
 */
public class RehabstodUser extends IntygUser implements Serializable {

    private static final long serialVersionUID = 8711015219408194075L;
    public static final int THIRTYONE = 31;

    // Handles PDL logging state
    private Map<String, List<PDLActivityEntry>> storedActivities;

    private boolean pdlConsentGiven = false;
    private boolean isLakare = false;

    /**
     * Typically used by unit tests.
     */
    public RehabstodUser(String hsaId, String namn, boolean isLakare) {
        super(hsaId);
        this.storedActivities = new HashMap<>();
        this.hsaId = hsaId;
        this.namn = namn;
        this.isLakare = isLakare;
    }

    /**
     * Copy-constructor that takes a populated {@link IntygUser} and booleans for whether the user has given PDL consent
     * and whether the user has LAKARE privileges.
     *
     * The "isLakare" backs the overridden "isLakare()" method, i.e. the RehabstodUser class doesn't derive LAKARE status
     * from the underlying roles once the isLakare value has been set. This is due to the requirement that LAKARE having
     * systemRoles for being Rehabkoordinator on one or more care units must be able to "switch" between roles when
     * changing units without losing the original "isLakare" information. See INTYG-5068.
     *
     * @param intygUser
     *      User principal, typically constructed in the {@link org.springframework.security.saml.userdetails.SAMLUserDetailsService}
     *      implementor.
     * @param pdlConsentGiven
     *      Whether the user has given PDL logging consent.
     * @param isLakare
     *      Wheter the user is LAKARE or not. Immutable once set.
     */
    public RehabstodUser(IntygUser intygUser, boolean pdlConsentGiven, boolean isLakare) {
        super(intygUser.getHsaId());
        this.privatLakareAvtalGodkand = intygUser.isPrivatLakareAvtalGodkand();
        this.personId = intygUser.getPersonId();

        this.namn = intygUser.getNamn();
        this.titel = intygUser.getTitel();
        this.forskrivarkod = intygUser.getForskrivarkod();
        this.authenticationScheme = intygUser.getAuthenticationScheme();
        this.vardgivare = intygUser.getVardgivare();
        this.befattningar = intygUser.getBefattningar();
        this.specialiseringar = intygUser.getSpecialiseringar();
        this.legitimeradeYrkesgrupper = intygUser.getLegitimeradeYrkesgrupper();
        this.systemRoles = intygUser.getSystemRoles();

        this.valdVardenhet = intygUser.getValdVardenhet();
        this.valdVardgivare = intygUser.getValdVardgivare();
        this.authenticationMethod = intygUser.getAuthenticationMethod();

        this.features = intygUser.getFeatures();
        this.roles = intygUser.getRoles();
        this.authorities = intygUser.getAuthorities();
        this.origin = intygUser.getOrigin();

        this.storedActivities = new HashMap<>();

        this.miuNamnPerEnhetsId = intygUser.getMiuNamnPerEnhetsId();
        this.pdlConsentGiven = pdlConsentGiven;

        this.isLakare = isLakare;
    }

    public Urval getUrval() {
        // If we dont have a role, we can't decide which urval change is allowed, so..
        if (roles == null) {
            return null;
        }

        // Case 1: Lakare should get ISSUED_BY_ME
        if (roles.containsKey(AuthoritiesConstants.ROLE_LAKARE)) {
            return Urval.ISSUED_BY_ME;
        }

        // Case 2: Koordinator should get ALL
        if (roles.containsKey(AuthoritiesConstants.ROLE_KOORDINATOR)) {
            return Urval.ALL;
        }

       return null;
    }

    public Map<String, List<PDLActivityEntry>> getStoredActivities() {
        return storedActivities;
    }


    public Urval getDefaultUrval() {
        return roles.containsKey(AuthoritiesConstants.ROLE_LAKARE) ? Urval.ISSUED_BY_ME : Urval.ALL;
    }

    @Override
    public int getTotaltAntalVardenheter() {
        // count all hasid's in the datastructure
        return (int) getVardgivare().stream().flatMap(vg -> vg.getHsaIds().stream()).count();
    }




    /**
     * If the currently selected vardenhet is not null and is an underenhet/mottagning, this method returns true.
     *
     * @return true if UE, false if not.
     */
    public boolean isValdVardenhetMottagning() {
        if (valdVardenhet == null) {
            return false;
        }

        for (Vardgivare vg : vardgivare) {
            for (Vardenhet ve : vg.getVardenheter()) {
                if (ve.getId().equals(valdVardenhet.getId())) {
                    return false;
                }
                for (Mottagning m : ve.getMottagningar()) {
                    if (m.getId().equals(valdVardenhet.getId())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isPdlConsentGiven() {
        return pdlConsentGiven;
    }

    public void setPdlConsentGiven(boolean pdlConsentGiven) {
        this.pdlConsentGiven = pdlConsentGiven;
    }

    /**
     * In rehabstöd, isLakare is an immutable field that must be set when logging in. This is due to us
     * sometimes changing the ROLE of a doctor to be a Rehabkoordinator based on systemRoles.
     *
     * @return
     *      true if doctor, false if not.
     */
    @Override
    public boolean isLakare() {
        return isLakare;
    }

    /**
     * Returns true if the user is a doctor and at least one vardenhet Id matches an Intyg;Rehab-[enhetId] systemRole.
     */
    public boolean isRoleSwitchPossible() {
        if (!isLakare()) {
            return false;
        }

        // Check if this doctor has a
        return this.vardgivare.stream()
                .flatMap(vg -> vg.getVardenheter().stream())
                .map(AbstractVardenhet::getId)
                .anyMatch(enhetId -> SystemRolesParser.parseEnhetsIdsFromSystemRoles(systemRoles).stream()
                        .anyMatch(systemRoleEnhetId -> systemRoleEnhetId.equals(enhetId))
        );
    }

    // CHECKSTYLE:OFF NeedBraces
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RehabstodUser)) return false;
        if (!super.equals(o)) return false;

        RehabstodUser that = (RehabstodUser) o;

        if (pdlConsentGiven != that.pdlConsentGiven) return false;
        if (isLakare != that.isLakare) return false;
        return storedActivities != null ? storedActivities.equals(that.storedActivities) : that.storedActivities == null;
    }
    // CHECKSTYLE:ON NeedBraces

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = THIRTYONE * result + (storedActivities != null ? storedActivities.hashCode() : 0);
        result = THIRTYONE * result + (pdlConsentGiven ? 1 : 0);
        result = THIRTYONE * result + (isLakare ? 1 : 0);
        return result;
    }

    // private scope
    private void writeObject(java.io.ObjectOutputStream stream) throws java.io.IOException {
        stream.defaultWriteObject();
    }

    private void readObject(java.io.ObjectInputStream stream) throws java.io.IOException, ClassNotFoundException {
        stream.defaultReadObject();
    }
}
