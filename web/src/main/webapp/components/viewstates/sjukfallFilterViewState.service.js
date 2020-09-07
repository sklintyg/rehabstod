/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
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
    function(DiagnosKapitelModel, LakareModel, QAModel, APP_CONFIG) {
      'use strict';

      var state = {
        diagnosKapitelModel: DiagnosKapitelModel,
        lakareModel: LakareModel,
        qaModel: QAModel,
        showPatientId: true,
        slutdatumModel: {
          from: null,
          to: null
        }
      };

      //Kanske initiera diagnoskapitelmodellen samtidigt som UserModel i appmain eller något?
      state.diagnosKapitelModel.set(APP_CONFIG.diagnosKapitelList);

      function _initQAModel() {
        state.qaModel.set([
          {id: null, displayValue: 'Visa alla', defaultSelected: true},
          {id: 0, displayValue: 'Visa enbart sjukfall utan obesvarade ärenden'},
          {id: 1, displayValue: 'Visa enbart sjukfall med obesvarade ärenden'},
          {id: 2, displayValue: 'Visa sjukfall med obesvarade kompletteringar'},
          {id: 3, displayValue: 'Visa sjukfall med obesvarade administrativa frågor och svar'}]);
      }


      function _reset() {
        state.diagnosKapitelModel.reset();
        state.sjukskrivningslangdModel = [1, 366];
        state.aldersModel = [0, 101];
        state.slutdatumModel.from = null;
        state.slutdatumModel.to = null;
        state.lakareModel.reset();
        state.qaModel.reset();
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

        if (!columnsByKey.qa) {
          state.qaModel.reset();
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

        var selectedQAOptions = [];
        angular.forEach(state.qaModel.getSelected(), function(value) {
          this.push(value.id);
        }, selectedQAOptions);

        return {
          diagnosKapitel: selectedDiagnosKapitel,
          lakare: selectedLakare,
          qa: selectedQAOptions.length === 1 ? selectedQAOptions[0] : null,
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
        get: _getState,
        initQAModel: _initQAModel
      };
    })
;
