/*
 * Copyright (C) 2026 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.text.SimpleDateFormat;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;

public final class CustomObjectMapper {

  private CustomObjectMapper() {}

  public static JsonMapper create() {
    return JsonMapper.builder()
        .changeDefaultPropertyInclusion(v -> v.withValueInclusion(JsonInclude.Include.ALWAYS))
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
        .defaultDateFormat(new SimpleDateFormat("yyyy-MM-dd"))
        .build();
  }
}
