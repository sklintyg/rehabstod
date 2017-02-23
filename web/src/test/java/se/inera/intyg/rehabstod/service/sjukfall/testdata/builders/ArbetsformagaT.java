/**
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.service.sjukfall.testdata.builders;

import se.riv.clinicalprocess.healthcond.rehabilitation.v1.Arbetsformaga;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.Formaga;

import java.util.List;

/**
 * Created by Magnus Ekstrand on 2016-02-10.
 */
public final class ArbetsformagaT {

    public ArbetsformagaT() {
    }

    public static class ArbetsformagaBuilder implements Builder<Arbetsformaga> {

        protected List<Formaga> formaga;

        public ArbetsformagaBuilder() {
        }

        public ArbetsformagaBuilder formaga(List<Formaga> formaga) {
            this.formaga = formaga;
            return this;
        }

        @Override
        public Arbetsformaga build() {
            Arbetsformaga arbetsformaga = new Arbetsformaga();
            arbetsformaga.getFormaga().addAll(formaga);

            return arbetsformaga;
        }
    }
}