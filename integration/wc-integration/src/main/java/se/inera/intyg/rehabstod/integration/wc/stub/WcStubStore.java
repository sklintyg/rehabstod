/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.integration.wc.stub;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import se.inera.intyg.clinicalprocess.healthcond.certificate.getcertificateadditions.v1.AdditionType;
import se.inera.intyg.clinicalprocess.healthcond.certificate.getcertificateadditions.v1.IntygAdditionsType;
import se.inera.intyg.clinicalprocess.healthcond.certificate.getcertificateadditions.v1.StatusType;
import se.inera.intyg.rehabstod.common.integration.json.CustomObjectMapper;
import se.riv.clinicalprocess.healthcond.certificate.types.v3.IntygId;

/**
 * Created by marced on 2019-05-20.
 */
@Component
public class WcStubStore {

    private static final Logger LOG = LoggerFactory.getLogger(WcStubStore.class);

    private boolean active = true;

    @Autowired
    @Qualifier("rediscache")
    private RedisTemplate<Object, Object> redisTemplate;

    // inject the template as ValueOperations
    @Resource(name = "rediscache")
    private ValueOperations<String, String> valueOps;

    private CustomObjectMapper objectMapper = new CustomObjectMapper();

    public void remove(String intygsId) {
        Set<String> keys = valueOps.getOperations().keys(assemblePattern("*" + intygsId));
        valueOps.getOperations().delete(keys);
    }

    public IntygAdditionsType getAddition(String intygsId) {
        if (!active) {
            throw new IllegalStateException("Stub is deactivated for testing purposes.");
        }
        String storeValue = valueOps.get(assembleCacheKey(intygsId));
        try {
            return storeValue != null ? objectMapper.readValue(storeValue, IntygAdditionsType.class) : null;
        } catch (IOException e) {
            LOG.error("Error while getting data from stub", e);
            throw new RuntimeException(e);
        }
    }

    public void removeAll() {
        Set<String> keys = valueOps.getOperations().keys(assemblePattern("*"));
        valueOps.getOperations().delete(keys);
    }

    private String assemblePattern(String pattern) {
        return WcIntegrationStubConfiguration.CACHE_NAME + pattern;
    }

    private String assembleCacheKey(IntygAdditionsType data) {
        return assembleCacheKey(data.getIntygsId().getExtension());
    }

    private String assembleCacheKey(String intygsId) {
        return Stream.of(WcIntegrationStubConfiguration.CACHE_NAME, intygsId)
            .collect(Collectors.joining(":"));
    }

    public void addAddition(String intygsId, LocalDateTime skapad, int antalObesvaradeKompletteringar) {
        IntygAdditionsType intygAdditionsType = new IntygAdditionsType();
        IntygId intygId = new IntygId();
        intygId.setExtension(intygsId);
        intygAdditionsType.setIntygsId(intygId);

        for (int i = 0; i < antalObesvaradeKompletteringar; i++) {
            AdditionType additionType = new AdditionType();
            additionType.setSkapad(skapad);
            additionType.setId(intygsId + "-arendeid-" + i);
            additionType.setStatus(antalObesvaradeKompletteringar == 0 ? StatusType.BESVARAD : StatusType.OBESVARAD);
            intygAdditionsType.getAddition().add(additionType);
        }
        addAddition(intygAdditionsType);
    }

    public void addAddition(IntygAdditionsType intygAdditionsType) {
        try {
            valueOps.set(assembleCacheKey(intygAdditionsType), objectMapper.writeValueAsString(intygAdditionsType));
        } catch (JsonProcessingException e) {
            LOG.error("Error while getting data from stub", e);
            throw new RuntimeException(e);
        }
        LOG.debug("Added IntygAdditionsType with " + intygAdditionsType.getAddition().size() + " additionitems for intygsId "
            + intygAdditionsType.getIntygsId().getExtension()
            + " to WcStubStore");
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return this.active;
    }
}
