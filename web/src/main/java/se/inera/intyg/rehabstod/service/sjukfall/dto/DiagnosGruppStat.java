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
package se.inera.intyg.rehabstod.service.sjukfall.dto;

import java.util.Objects;

import se.inera.intyg.rehabstod.service.diagnos.dto.DiagnosGrupp;

/**
 * Created by marced on 14/03/16.
 */
public class DiagnosGruppStat {
    private DiagnosGrupp grupp;
    private Long count;

    public DiagnosGruppStat(DiagnosGrupp grupp, Long count) {
        this.grupp = grupp;
        this.count = count;
    }

    public DiagnosGrupp getGrupp() {
        return grupp;
    }

    public void setGrupp(DiagnosGrupp grupp) {
        this.grupp = grupp;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DiagnosGruppStat)) {
            return false;
        }
        DiagnosGruppStat that = (DiagnosGruppStat) o;
        return Objects.equals(grupp, that.grupp) && Objects.equals(count, that.count);
    }

    @Override
    public int hashCode() {
        return Objects.hash(grupp, count);
    }
}
