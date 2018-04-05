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
package se.inera.intyg.rehabstod.auth.fake;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.List;

/**
 * @author andreaskaltenbach
 */
public class FakeCredentials implements Serializable {

    private static final long serialVersionUID = -7620199916206349045L;

    private static final String LAKARE = "Läkare";
    private static final String TANDLAKARE = "Tandläkare";

    private String hsaId;
    private String forNamn;
    private String efterNamn;
    private String enhetId;
    private String befattningsKod;
    private String forskrivarKod;

    private List<String> legitimeradeYrkesgrupper;
    private List<String> systemRoles;

    private Boolean pdlConsentGiven = null;

    public FakeCredentials() {
        // Needed for deserialization
    }

    public FakeCredentials(FakeCredentialsBuilder builder) {
        this.hsaId = builder.hsaId;
        this.forNamn = builder.forNamn;
        this.efterNamn = builder.efterNamn;
        this.enhetId = builder.enhetId;
        this.legitimeradeYrkesgrupper = builder.legitimeradeYrkesgrupper;
        this.systemRoles = builder.systemRoles;
        this.befattningsKod = builder.befattningsKod;
        this.forskrivarKod = builder.forskrivarKod;
        this.pdlConsentGiven = builder.pdlConsentGiven;
    }

    // ~ Getter and setters
    // ~====================================================================================

    public String getHsaId() {
        return hsaId;
    }

    public String getForNamn() {
        return forNamn;
    }

    public String getEfterNamn() {
        return efterNamn;
    }

    public String getEnhetId() {
        return enhetId;
    }

    public String getBefattningsKod() {
        return befattningsKod;
    }

    public String getForskrivarKod() {
        return forskrivarKod;
    }

    public List<String> getLegitimeradeYrkesgrupper() {
        return legitimeradeYrkesgrupper;
    }

    public List<String> getSystemRoles() {
        return systemRoles;
    }

    public Boolean isPdlConsentGiven() {
        return pdlConsentGiven;
    }

    // ~ Public methods
    // ~====================================================================================

    @JsonIgnore
    public boolean isLakare() {
        if (legitimeradeYrkesgrupper == null) {
            return false;
        }
        return legitimeradeYrkesgrupper.contains(LAKARE);
    }

    @JsonIgnore
    public boolean isTandlakare() {
        if (legitimeradeYrkesgrupper == null) {
            return false;
        }
        return legitimeradeYrkesgrupper.contains(TANDLAKARE);
    }

    @Override
    public String toString() {
        return "FakeCredentials{"
                + "hsaId='" + hsaId + '\''
                + ", fornamn='" + forNamn + '\''
                + ", efternamn='" + efterNamn + '\''
                + ", lakare=" + isLakare()
                + ", systemRoles=" + "[" + (systemRoles == null ? "" : String.join(",", systemRoles)) + "]"
                + '}';
    }

    // ~ Builder class
    // ~====================================================================================

    public static class FakeCredentialsBuilder {
        private String hsaId;
        private String forNamn;
        private String efterNamn;
        private String enhetId;
        private String befattningsKod;
        private String forskrivarKod;
        private List<String> legitimeradeYrkesgrupper;
        private List<String> systemRoles;
        private Boolean pdlConsentGiven;

        public FakeCredentialsBuilder(String hsaId, String enhetId) {
            this.hsaId = hsaId;
            this.enhetId = enhetId;
        }

        public FakeCredentialsBuilder hsaId(String hsaId) {
            this.hsaId = hsaId;
            return this;
        }

        public FakeCredentialsBuilder forNamn(String forNamn) {
            this.forNamn = forNamn;
            return this;
        }

        public FakeCredentialsBuilder efterNamn(String efterNamn) {
            this.efterNamn = efterNamn;
            return this;
        }

        public FakeCredentialsBuilder enhetId(String enhetId) {
            this.enhetId = enhetId;
            return this;
        }

        public FakeCredentialsBuilder systemRoles(List<String> systemRoles) {
            this.systemRoles = systemRoles;
            return this;
        }

        public FakeCredentialsBuilder forskrivarKod(String forskrivarKod) {
            this.forskrivarKod = forskrivarKod;
            return this;
        }

        public FakeCredentialsBuilder befattningsKod(String befattningsKod) {
            this.befattningsKod = befattningsKod;
            return this;
        }

        public FakeCredentialsBuilder legitimeradeYrkesgrupper(List<String> legitimeradeYrkesgrupper) {
            this.legitimeradeYrkesgrupper = legitimeradeYrkesgrupper;
            return this;
        }

        public FakeCredentialsBuilder pdlConsentGiven(Boolean pdlConsentGiven) {
            this.pdlConsentGiven = pdlConsentGiven;
            return this;
        }

        public FakeCredentials build() {
            return new FakeCredentials(this);
        }
    }

}
