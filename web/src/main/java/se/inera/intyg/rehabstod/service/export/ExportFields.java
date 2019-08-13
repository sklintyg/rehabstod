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
package se.inera.intyg.rehabstod.service.export;

import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Stream;

public enum ExportFields {

  LINE_NR("number", "#"),
  PATIENT_ID("patientId", "Personnr"),
  PATIENT_AGE("patientAge", "Ålder"),
  PATIENT_NAME("patientName", "Namn"),
  PATIENT_GENDER("gender", "Kön"),
  DIAGNOSE("dxs", "Diagnos(er)"),
  STARTDATE("startDate", "Startdatum"),
  ENDDATE("endDate", "Slutdatum"),
  DAYS("days", "Längd"),
  NR_OF_INTYG("antal", "Antal intyg"),
  GRADER("degree", "Grad"),
  KOMPLETTERINGAR("Kompletteringar", "Komplettering"),
  LAKARE("doctor", "Läkare"),
  SRS("srs", "Risk");

  private final String jsonId;
  private final String label;

  ExportFields(String jsonId, String label) {
    this.jsonId = jsonId;
    this.label = label;
  }

  public static List<ExportFields> fromJson(String jsonPreferenceString) {
    final EnumSet<ExportFields> allFields = EnumSet.allOf(ExportFields.class);

    if (Strings.isNullOrEmpty(jsonPreferenceString)) {
      return Arrays.asList(ExportFields.values());
    }
    // jsonPreferenceString is separated by "|" e.g
    // number|patientId|patientAge|patientName|gender|dxs|startDate|endDate|days|antal|degree|kompletteringar|doctor|srs

    List<ExportFields> enabledFields = new ArrayList();

    Stream.of(jsonPreferenceString.split("\\|"))
        .forEach(jsonId -> allFields
            .stream()
            .filter(ef -> ef.getJsonId().equalsIgnoreCase(jsonId))
            .findFirst()
            .ifPresent(enabledFields::add));

    return enabledFields;
  }

  public String getJsonId() {
    return jsonId;
  }

  public String getLabel() {
    return label;
  }
}
