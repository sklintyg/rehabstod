/**
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
 *
 * This file is part of rehabstod (https://github.com/sklintyg/rehabstod).
 *
 * rehabstod is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * rehabstod is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.intyg.rehabstod.service.diagnos.dto;

import se.inera.intyg.rehabstod.common.util.StringUtil;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents an group of diagnoses ("DiagnosKaptiel") in an range interval, e.g "A00-C49" which would include also a
 * code of B34.
 *
 * Created by marced on 08/02/16.^
 */
public class DiagnosKapitel {
    public static final Pattern VALID_DIAGNOSKAPITEL_ROW_FORMAT = Pattern.compile("^([A-Z]{1})([0-9]{2})-([A-Z]{1})([0-9]{2})(.+)");
    private static final int FROM_CHAR = 1;
    private static final int FROM_NUMBER = 2;
    private static final int TO_CHAR = 3;
    private static final int TO_NUMBER = 4;
    private static final int NAME = 5;
    private static final String SEPARATOR = "-";
    private static final int CHAR_MULTIPLIER = 100;

    private DiagnosKategori from;
    private DiagnosKategori to;

    private String name;

    public String getName() {
        return name;
    }

    public DiagnosKategori getFrom() {
        return from;
    }

    public DiagnosKategori getTo() {
        return to;
    }

    public DiagnosKapitel(DiagnosKategori from, DiagnosKategori to, String name) {
        this.from = from;
        this.to = to;
        this.name = name;
    }

    /**
     * Constructor that only accepts a diagnose code interval source string in the form "AXX-BXXSome description".
     * This is mainly to accommodate simple ingestion of config from a flat file.
     *
     * @param rangeString
     * @see DiagnosKapitel#VALID_DIAGNOSKAPITEL_ROW_FORMAT
     */
    public DiagnosKapitel(String rangeString) {
        Matcher matcher = VALID_DIAGNOSKAPITEL_ROW_FORMAT.matcher(rangeString);
        if (matcher.find()) {
            this.from = new DiagnosKategori(matcher.group(FROM_CHAR).charAt(0), Integer.parseInt(matcher.group(FROM_NUMBER)));
            this.to = new DiagnosKategori(matcher.group(TO_CHAR).charAt(0), Integer.parseInt(matcher.group(TO_NUMBER)));
            this.name = matcher.group(NAME);
        } else {
            throw new IllegalArgumentException("rangeString argument '" + rangeString + "' does not match expected format of "
                    + VALID_DIAGNOSKAPITEL_ROW_FORMAT.pattern());
        }
    }

    /**
     * Returns an composite indentifying string for the interval in the form of "A00-B99".
     *
     * @return the composite id of the diagnosKapitel
     */
    public String getId() {
        if (StringUtil.isNullOrEmpty(from.getId() + to.getId())) {
            return "";
        }
        return from.getId() + SEPARATOR + to.getId();
    }

    /**
     * Determines if a given diagnosKategori is considered to be included in this DiagnosKapitel.
     *
     * @param diagnosKategori
     * @return
     */
    public boolean includes(Optional<DiagnosKategori> diagnosKategori) {
        if (diagnosKategori.isPresent()) {
            // We use the fact that a char has a numerical value in a natural order (for A-Z at least)
            // By using this to caluculate a numerical value for a diagnosKategori we can then easily compare.
            int min = this.from.getLetter() * CHAR_MULTIPLIER + this.from.getNumber();
            int max = this.to.getLetter() * CHAR_MULTIPLIER + this.to.getNumber();
            int value = diagnosKategori.get().getLetter() * CHAR_MULTIPLIER + diagnosKategori.get().getNumber();
            return (min <= value) && (value <= max);
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DiagnosKapitel that = (DiagnosKapitel) o;
        return Objects.equals(from, that.from)
                && Objects.equals(to, that.to)
                && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, name);
    }
}
