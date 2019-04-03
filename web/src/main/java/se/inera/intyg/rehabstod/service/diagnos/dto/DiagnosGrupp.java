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
package se.inera.intyg.rehabstod.service.diagnos.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author marced on 14/03/16.
 */
public class DiagnosGrupp {
    public static final Pattern VALID_DIAGNOSGRUPP_ROW_FORMAT = Pattern.compile("^([A-Z0-9-,]+):(.+)");
    private static final int DIAGNOSKAPITEL_STRING = 1;
    private static final int GROUP_NAME = 2;
    private static final String KAPITELLIST_SEPARATOR = ",";

    // fields
    private String id;
    private List<DiagnosKapitel> kapitelList;
    private String name;

    public DiagnosGrupp(String id, List<DiagnosKapitel> kapitelList, String name) {
        this.id = id;
        this.kapitelList = kapitelList;
        this.name = name;
    }

    /**
     * Constructor that only accepts a diagnose group interval source string in the form
     * "AXX-BXX,CXX-CXX:Some description".
     * This is mainly to accommodate simple ingestion of config from a flat file.
     *
     * @param diagnosGruppString
     * @see DiagnosKapitel#VALID_DIAGNOSKAPITEL_ROW_FORMAT
     */
    public DiagnosGrupp(String diagnosGruppString) {
        Matcher matcher = VALID_DIAGNOSGRUPP_ROW_FORMAT.matcher(diagnosGruppString);
        if (matcher.find()) {
            this.id = matcher.group(DIAGNOSKAPITEL_STRING);
            this.kapitelList = convertToKapitelList(matcher.group(DIAGNOSKAPITEL_STRING));
            this.name = matcher.group(GROUP_NAME);
        } else {
            throw new IllegalArgumentException(
                    "rangeString argument '" + diagnosGruppString + "' does not match expected format for a diagnosGrupp definition: "
                            + VALID_DIAGNOSGRUPP_ROW_FORMAT.pattern());
        }
    }

    /**
     * Return a list of matching diagnoskapitel's.
     * The kapitelListString must have the form "AXX-BXX,CXX-CXX" and the corresponding diagnoskapitel must be defined
     * in DiagnosKapitels.
     *
     * @param kapitelListString
     * @return
     */
    private List<DiagnosKapitel> convertToKapitelList(String kapitelListString) {
        List<DiagnosKapitel> list = new ArrayList<>();
        final String[] arr = kapitelListString.split(KAPITELLIST_SEPARATOR);

        for (String kapitelId : arr) {
            list.add(new DiagnosKapitel(kapitelId));
        }

        return list;
    }

    public boolean includes(String diagnosKod) {
        return kapitelList.stream().filter(dk -> dk.includes(DiagnosKategori.extractFromString(diagnosKod))).findAny().isPresent();
    }

    public String getId() {
        return id;
    }

    public List<DiagnosKapitel> getKapitelList() {
        return kapitelList;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DiagnosGrupp that = (DiagnosGrupp) o;
        return Objects.equals(id, that.id) && Objects.equals(kapitelList, that.kapitelList) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, kapitelList, name);
    }

}
