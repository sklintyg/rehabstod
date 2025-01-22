/*
 * Copyright (C) 2025 Inera AB (http://www.inera.se)
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

package se.inera.intyg.rehabstod.service.sjukfall.dto;

public enum UnansweredCommunicationFilterType {

    UNANSWERED_COMMUNICATION_FILTER_TYPE_1("Enbart sjukfall utan obesvarade ärenden"),
    UNANSWERED_COMMUNICATION_FILTER_TYPE_2("Enbart sjukfall med obesvarade ärenden"),
    UNANSWERED_COMMUNICATION_FILTER_TYPE_3("Sjukfall med obesvarade kompletteringar"),
    UNANSWERED_COMMUNICATION_FILTER_TYPE_4("Sjukfall med obesvarade frågor och svar");

    private final String name;

    UnansweredCommunicationFilterType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static UnansweredCommunicationFilterType fromId(String id) {
        for (final var type : values()) {
            if (type.toString().equals(id)) {
                return type;
            }
        }

        throw new IllegalArgumentException(String.format("Not allowed UnansweredCommunicationFilterType id: '%s'", id));
    }
}
