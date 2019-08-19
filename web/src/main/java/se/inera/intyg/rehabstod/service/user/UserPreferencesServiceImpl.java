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
package se.inera.intyg.rehabstod.service.user;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.auth.RehabstodUserPreferences;
import se.inera.intyg.rehabstod.auth.RehabstodUserPreferences.Preference;
import se.inera.intyg.rehabstod.persistence.model.AnvandarPreference;
import se.inera.intyg.rehabstod.persistence.repository.AnvandarPreferenceRepository;

@Service
public class UserPreferencesServiceImpl implements UserPreferencesService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private UserService userService;

    @Autowired
    private AnvandarPreferenceRepository anvandarPreferenceRepository;

    @Override
    public RehabstodUserPreferences getAllPreferences() {
        RehabstodUser user = userService.getUser();
        log.debug("Getting all preferences for {}.", user.getHsaId());
        return RehabstodUserPreferences.fromBackend(anvandarPreferenceRepository.getAnvandarPreference(user.getHsaId()));
    }

    @Override
    @Transactional
    public void updatePreferences(RehabstodUserPreferences keyValueMap) {
        RehabstodUser user = userService.getUser();
        log.debug("Updating preference {} for {}.", keyValueMap.toFrontendMap(), user.getHsaId());
        for (Map.Entry<Preference, String> pref : keyValueMap.preferences().entrySet()) {
            AnvandarPreference anvPref = anvandarPreferenceRepository.findByHsaIdAndKey(user.getHsaId(),
                pref.getKey().getBackendKeyName());
            if (anvPref == null) {
                anvPref = new AnvandarPreference(user.getHsaId(), pref.getKey().getBackendKeyName(), pref.getValue());
            } else {
                anvPref.setValue(pref.getValue());
            }
            anvandarPreferenceRepository.save(anvPref);
            user.getPreferences().updatePreference(pref.getKey(), pref.getValue());
        }
    }

    @Override
    public String getPreferenceValue(Preference pref) {
        RehabstodUserPreferences allPreferences = getAllPreferences();
        return allPreferences.get(pref);
    }

}
