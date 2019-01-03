/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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

import java.io.Serializable;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public final class RehabstodUserPreferences implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Map<Preference, String> map;

    private RehabstodUserPreferences(Map<Preference, String> map) {
        this.map = map;
    }

    public static RehabstodUserPreferences empty() {
        return new RehabstodUserPreferences(new EnumMap<>(Preference.class));
    }

    public static RehabstodUserPreferences fromBackend(Map<String, String> prefs) {
        Map<Preference, String> map = new EnumMap<>(Preference.class);
        for (Preference p : Preference.values()) {
            if (prefs.containsKey(p.getBackendKeyName())) {
                map.put(p, prefs.get(p.getBackendKeyName()));
            } else {
                map.put(p, p.getDefaultValue());
            }
        }
        return new RehabstodUserPreferences(map);
    }

    public static RehabstodUserPreferences fromFrontend(Map<String, String> prefs) {
        Map<Preference, String> map = new EnumMap<>(Preference.class);
        for (Preference p : Preference.values()) {
            if (prefs.containsKey(p.getFrontendKeyName())) {
                map.put(p, prefs.get(p.getFrontendKeyName()));
            } else {
                map.put(p, p.getDefaultValue());
            }
        }
        return new RehabstodUserPreferences(map);
    }

    public void updatePreference(Preference pref, String value) {
        map.put(pref, value);
    }

    public String get(Preference pref) {
        return map.get(pref);
    }

    public Map<Preference, String> preferences() {
        return Collections.unmodifiableMap(map);
    }

    public Map<String, String> toFrontendMap() {
        Map<String, String> result = new HashMap<>();
        for (Map.Entry<Preference, String> entry : map.entrySet()) {
            result.put(entry.getKey().getFrontendKeyName(), entry.getValue());
        }
        return result;
    }

    // CHECKSTYLE:OFF NeedBraces
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RehabstodUserPreferences)) return false;

        RehabstodUserPreferences that = (RehabstodUserPreferences) o;

        return map != null ? map.equals(that.map) : that.map == null;
    }

    @Override
    public int hashCode() {
        return map != null ? map.hashCode() : 0;
    }
    // CHECKSTYLE:ON NeedBraces

    public enum Preference {
        PDL_CONSENT_GIVEN("user_pdl_consent_given", "pdlConsentGiven", "false"),
        MAX_ANTAL_DAGAR_MELLAN_INTYG("maxAntalDagarMellanIntyg", "maxAntalDagarMellanIntyg", "5"),
        MAX_ANTAL_DAGAR_SEDAN_SJUKFALL_AVSLUT("maxAntalDagarSedanSjukfallAvslut", "maxAntalDagarSedanSjukfallAvslut", "0");

        private final String backendKeyName;
        private final String defaultValue;
        private final String frontendKeyName;

        Preference(String backendKeyName, String frontendKeyName, String defaultValue) {
            this.backendKeyName = backendKeyName;
            this.frontendKeyName = frontendKeyName;
            this.defaultValue = defaultValue;
        }

        public String getBackendKeyName() {
            return backendKeyName;
        }

        public String getFrontendKeyName() {
            return frontendKeyName;
        }

        public String getDefaultValue() {
            return defaultValue;
        }
    }
}
