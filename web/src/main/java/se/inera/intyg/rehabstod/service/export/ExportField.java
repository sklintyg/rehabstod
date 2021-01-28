/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
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
import java.util.Optional;
import java.util.stream.Stream;

public enum ExportField {

    LINE_NR("number", "#", "#"),
    PATIENT_ID("patientId", "Personnr", "Personnummer"),
    PATIENT_AGE("patientAge", "Ålder", "Ålder"),
    PATIENT_NAME("patientName", "Namn", "Namn"),
    PATIENT_GENDER("gender", "Kön", "Kön"),
    DIAGNOSE("dxs", "Diagnos(er)", "Diagnos/diagnoser"),
    STARTDATE("startDate", "Startdatum", "Startdatum"),
    ENDDATE("endDate", "Slutdatum", "Slutdatum"),
    DAYS("days", "Längd", "Längd"),
    NR_OF_INTYG("antal", "Antal intyg", "Antal"),
    GRADER("degree", "Grad", "Grad"),
    ARENDEN("qa", "Ärenden", "Ärenden"),
    SRS("srs", "Risk", "Risk"),
    LAKARE("doctor", "Läkare", "Läkare");

    private final String jsonId;
    private final String labelPdf;
    private final String labelXlsx;

    ExportField(String jsonId, String labelPdf, String labelXlsx) {
        this.jsonId = jsonId;
        this.labelPdf = labelPdf;
        this.labelXlsx = labelXlsx;
    }

    public static List<ExportField> fromJson(String jsonPreferenceString) {
        final EnumSet<ExportField> allFields = EnumSet.allOf(ExportField.class);

        if (Strings.isNullOrEmpty(jsonPreferenceString)) {
            return Arrays.asList(ExportField.values());
        }
        // jsonPreferenceString is separated by "|" e.g
        // example: number:1|patientId:0|patientAge:1

        List<ExportField> enabledFields = new ArrayList<>();

        Stream.of(jsonPreferenceString.split("\\|"))
            .forEach(jsonString -> addFieldIfItExists(allFields, enabledFields, jsonString));

        return enabledFields;
    }

    private static void addFieldIfItExists(EnumSet<ExportField> allFields, List<ExportField> enabledFields, String jsonString) {
        String[] jsonSplit = jsonString.split(":");

        if (jsonSplit.length > 1 && jsonSplit[1].equalsIgnoreCase("0")) {
            return;
        }

        allFields
            .stream()
            .filter(ef -> ef.getJsonId().equalsIgnoreCase(jsonSplit[0]))
            .findFirst()
            .ifPresent(enabledFields::add);
    }

    public static Optional<ExportField> fromJsonId(String jsonId) {
        EnumSet<ExportField> allFields = EnumSet.allOf(ExportField.class);

        return allFields.stream()
            .filter(ef -> ef.getJsonId().equalsIgnoreCase(jsonId))
            .findAny();
    }

    public String getJsonId() {
        return jsonId;
    }

    public String getLabelPdf() {
        return labelPdf;
    }

    public String getLabelXlsx() {
        return labelXlsx;
    }
}
