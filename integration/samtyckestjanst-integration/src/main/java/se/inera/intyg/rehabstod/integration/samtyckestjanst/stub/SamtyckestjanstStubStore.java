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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Simple embedded redis cache store for consents.
 * Created by Magnus Ekstrand on 2018-10-10.
 */
@Component
public class SamtyckestjanstStubStore {

    // inject the actual template
    @Autowired
    @Qualifier("rediscache")
    private RedisTemplate<Object, Object> redisTemplate;

    // inject the template as ValueOperations
    @Resource(name = "rediscache")
    private ValueOperations<String, ConsentData> valueOps;

    public void add(ConsentData data) {
        valueOps.set(assembleCacheKey(data), data);
    }

    public List<ConsentData> getAll() {
        Set<String> keys = valueOps.getOperations().keys(assemblePattern("*"));
        return valueOps.multiGet(keys);
    }

    public boolean hasConsent(String vgHsaId, String veHsaId, String patientId, LocalDate queryDate) {
        ConsentData consentData = valueOps.get(assembleCacheKey(vgHsaId, veHsaId, patientId));
        return consentData != null && isWithinInterval(consentData, queryDate);
    }

    public void remove(String personId) {
        Set<String> keys = valueOps.getOperations().keys(assemblePattern("*" + personId));
        valueOps.getOperations().delete(keys);
    }

    public void removeAll() {
        Set<String> keys = valueOps.getOperations().keys(assemblePattern("*"));
        valueOps.getOperations().delete(keys);
    }

    private String assemblePattern(String pattern) {
        return SamtyckestjanstStubConfiguration.CACHE_NAME + pattern;
    }

    private String assembleCacheKey(ConsentData data) {
        return assembleCacheKey(data.getVardgivareId(), data.getVardenhetId(), data.getPatientId());
    }

    private String assembleCacheKey(String vgHsaId, String veHsaId, String patientId) {
        return Stream.of(SamtyckestjanstStubConfiguration.CACHE_NAME, vgHsaId, veHsaId, patientId)
                .collect(Collectors.joining(":"));
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
