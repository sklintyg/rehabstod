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

angular.module('rehabstodApp').factory('SjukfallFilterViewState',
    function(DiagnosKapitelModel, LakareModel, KompletteringModel, APP_CONFIG) {
        'use strict';

        var state = {
            diagnosKapitelModel: DiagnosKapitelModel,
            lakareModel: LakareModel,
            kompletteringModel: KompletteringModel, //null = show all, 0 = exactly 0 and 1 means all > 0
            showPatientId: true,
            slutdatumModel: {
                from: null,
                to: null
            }
        };

        //Kanske initiera diagnoskapitelmodellen samtidigt som UserModel i appmain eller något?
        state.diagnosKapitelModel.set(APP_CONFIG.diagnosKapitelList);

        state.kompletteringModel.set([
            {id: null, displayValue: 'Visa alla', defaultSelected: true},
            {id: 0, displayValue: 'Visa sjukfall utan obesvarade kompletteringar'},
            {id: 1, displayValue: 'Visa sjukfall med obesvarade kompletteringar'}]);


        function _reset() {
            state.diagnosKapitelModel.reset();
            state.sjukskrivningslangdModel = [1, 366];
            state.aldersModel = [0, 101];
            state.slutdatumModel.from = null;
            state.slutdatumModel.to = null;
            state.lakareModel.reset();
            state.kompletteringModel.reset();
            state.showPatientId = true;
            state.freeTextModel = '';
        }

        function _resetIfColumnsHidden(columnsByKey) {
          if (!columnsByKey.dxs) {
            state.diagnosKapitelModel.reset();
          }

          if (!columnsByKey.days) {
            state.sjukskrivningslangdModel = [1, 366];
          }

          if (!columnsByKey.doctor) {
            state.lakareModel.reset();
          }

          if (!columnsByKey.kompletteringar) {
            state.kompletteringModel.reset();
          }

          if (!columnsByKey.patientAge) {
            state.aldersModel = [0, 101];
          }

          if (!columnsByKey.endDate) {
            state.slutdatumModel.from = null;
            state.slutdatumModel.to = null;
          }
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

            var selectedKompletteringOptions = [];
            angular.forEach(state.kompletteringModel.getSelected(), function(value) {
                this.push(value.id);
            }, selectedKompletteringOptions);

            return {
                diagnosKapitel: selectedDiagnosKapitel,
                lakare: selectedLakare,
                komplettering: selectedKompletteringOptions.length === 1 ? selectedKompletteringOptions[0] : null,
                sjukskrivningslangd: [state.sjukskrivningslangdModel[0],
                    state.sjukskrivningslangdModel[1] > 365 ? null : state.sjukskrivningslangdModel[1]],
                alder: [state.aldersModel[0],
                    state.aldersModel[1] > 100 ? null : state.aldersModel[1]],
                slutdatum: state.slutdatumModel,
                freeText: state.freeTextModel,
                showPatientId: state.showPatientId
            };

        }

        function _getState() {
            return state;
        }

        _reset();

        return {
            reset: _reset,
            resetIfColumnsHidden: _resetIfColumnsHidden,
            getCurrentFilterState: _getCurrentFilterState,
            get: _getState
        };
    })
;
