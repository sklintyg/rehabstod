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
package se.inera.intyg.rehabstod.integration.samtyckestjanst.stub;

import com.google.common.base.Strings;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Simple in-memory store for consents.
 * Created by Magnus Ekstrand on 2018-10-10.
 */
@Component
public class SamtyckestjanstStubStore {

    private Set<ConsentData> repository = new HashSet<>();

    public void add(ConsentData data) {
        repository.add(data);
    }

    public List<ConsentData> getAll() {
        return repository.stream().collect(Collectors.toList());
    }

    public List<ConsentData> getConsents(String vgHsaId, String veHsaId, String patientId) {
        return repository.stream()
                .filter(consent -> consent.getPatientId().equals(patientId)
                        && consent.getVardgivareId().equals(vgHsaId)
                        && consent.getVardenhetId().equals(veHsaId))
                .collect(Collectors.toList());
    }

    public boolean hasConsent(String vgHsaId, String veHsaId, String patientId, LocalDate queryDate) {
        return getConsents(vgHsaId, veHsaId, patientId).stream()
                .anyMatch(consent -> isWithinInterval(consent, queryDate));
    }

    public void removeAll() {
        repository.clear();
    }

    public void remove(String personId) {
        if (!Strings.isNullOrEmpty(personId)) {
            repository.removeIf(consent -> consent.getPatientId().equals(personId));
        }
    }

    private boolean isWithinInterval(ConsentData consent, LocalDate queryDate) {
        // Om queryDate är lika med startDatum så är det inom intervallet.
        if (queryDate.isEqual(consent.getConsentFrom().toLocalDate())) {
            return true;
        }
        // Om slutDatum inte finns så finns det inte en bortre datumgräns för samtycket.
        // Men queryDate ska i så fall vara efter startDatum.
        if (consent.getConsentTo() == null) {
            if (queryDate.isAfter(consent.getConsentFrom().toLocalDate())) {
                return true;
            }
            return false;
        }
        // Om queryDate är lika med slutDatum så är det inom intervallet.
        if (queryDate.isEqual(consent.getConsentTo().toLocalDate())) {
            return true;
        }
        // Kontrollera att queryDate är mellan start- och slutdatum.
        return queryDate.isAfter(consent.getConsentFrom().toLocalDate())
                && queryDate.isBefore(consent.getConsentTo().toLocalDate());
    }

}
