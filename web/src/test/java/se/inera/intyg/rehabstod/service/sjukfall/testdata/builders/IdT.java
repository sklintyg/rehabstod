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

import se.riv.clinicalprocess.healthcond.certificate.types.v2.IIType;

/**
 * Created by Magnus Ekstrand on 2016-02-10.
 */
public final class IdT extends IIType {

    public IdT() {
    }

    public static class IITypeBuilder implements Builder<IIType> {

        private String root;
        private String extension;

        public IITypeBuilder() {
        }

        public IITypeBuilder root(String root) {
            this.root = root;
            return this;
        }

        public IITypeBuilder extension(String extension) {
            this.extension = extension;
            return this;
        }

        @Override
        public IIType build() {
            IIType iiType = new IIType();
            iiType.setRoot(root);
            iiType.setExtension(extension);

            return iiType;
        }
    }
}
