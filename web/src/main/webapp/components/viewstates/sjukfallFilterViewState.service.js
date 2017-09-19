/*
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
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

angular.module('rehabstodApp').factory('SjukfallFilterViewState', [
    'DiagnosKapitelModel', 'LakareModel', 'APP_CONFIG',
    function(DiagnosKapitelModel, LakareModel, APP_CONFIG) {
        'use strict';

        var state = {
            diagnosKapitelModel: DiagnosKapitelModel,
            lakareModel: LakareModel,
            showPatientId: true,
            glapp: 5
        };

        //Kanske initiera diagnoskapitelmodellen samtidigt som UserModel i appmain eller något?
        state.diagnosKapitelModel.set(APP_CONFIG.diagnosKapitelList);


        function _reset() {
            state.diagnosKapitelModel.reset();
            state.sjukskrivningslangdModel = [1, 366];
            state.aldersModel = [0, 101];
            state.lakareModel.reset();
            state.showPatientId = true;
            state.freeTextModel = '';
        }

        function _getCurrentFilterState() {
            var selectedDiagnosKapitel = [];
            angular.forEach(state.diagnosKapitelModel.getSelected(), function(value) {
                this.push(value.id);
            }, selectedDiagnosKapitel);

            var selectedLakare = [];
            angular.forEach(state.lakareModel.getSelected(), function(value) {
                this.push(value.id);
            }, selectedLakare);

            return {
                diagnosKapitel: selectedDiagnosKapitel,
                lakare: selectedLakare,
                sjukskrivningslangd: [state.sjukskrivningslangdModel[0],
                    state.sjukskrivningslangdModel[1] > 365 ? null : state.sjukskrivningslangdModel[1]],
                alder: [state.aldersModel[0],
                    state.aldersModel[1] > 100 ? null : state.aldersModel[1]],
                freeText: state.freeTextModel,
                showPatientId: state.showPatientId,
                glapp: state.glapp

            };

        }

        function _getState() {
            return state;
        }

        _reset();

        return {
            reset: _reset,
            getCurrentFilterState: _getCurrentFilterState,
            get: _getState
        };
    }])
;