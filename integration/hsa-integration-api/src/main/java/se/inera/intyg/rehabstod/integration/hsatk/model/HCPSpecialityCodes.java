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
<<<<<<<< HEAD:integration/pu-integration-intyg-proxy-service/src/main/java/se/inera/intyg/rehabstod/pu/integration/intygproxyservice/dto/PersonRequestDTO.java
package se.inera.intyg.rehabstod.pu.integration.intygproxyservice.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PersonRequestDTO {

  String personId;
  boolean queryCache;
========
package se.inera.intyg.rehabstod.integration.hsatk.model;

import java.io.Serializable;
import lombok.Data;

@Data
public class HCPSpecialityCodes implements Serializable {

  private static final long serialVersionUID = 1L;
  protected String healthCareProfessionalLicenceCode;
  protected String specialityCode;
  protected String specialityName;
>>>>>>>> f1ea1a5635c5953c3133731533b3a4e6114a89ba:integration/hsa-integration-api/src/main/java/se/inera/intyg/rehabstod/integration/hsatk/model/HCPSpecialityCodes.java
}
