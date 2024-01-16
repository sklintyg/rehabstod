/*
 * Copyright (C) 2024 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.service.sjukfall.testdata.builders;

import se.riv.clinicalprocess.healthcond.certificate.types.v2.HsaId;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.Enhet;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.Vardgivare;

/**
 * Created by Magnus Ekstrand on 2016-02-10.
 */
public final class EnhetT {

    public EnhetT() {
    }

    public static class EnhetBuilder implements Builder<Enhet> {

        private HsaId enhetsId;
        private String enhetsnamn;
        private Vardgivare vardgivare;

        public EnhetBuilder() {
        }

        public EnhetBuilder enhetsId(HsaId enhetsId) {
            this.enhetsId = enhetsId;
            return this;
        }

        public EnhetBuilder enhetsnamn(String enhetsnamn) {
            this.enhetsnamn = enhetsnamn;
            return this;
        }

        public EnhetBuilder vardgivare(Vardgivare vardgivare) {
            this.vardgivare = vardgivare;
            return this;
        }

        @Override
        public Enhet build() {
            Enhet enhet = new Enhet();
            enhet.setEnhetsId(enhetsId);
            enhet.setEnhetsnamn(enhetsnamn);
            enhet.setVardgivare(vardgivare);

            return enhet;
        }
    }
}
