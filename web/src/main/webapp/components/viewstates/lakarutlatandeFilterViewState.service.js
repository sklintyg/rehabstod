/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
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

angular.module('rehabstodApp').factory('LakarutlatandeFilterViewState',
    function($window, DiagnosKapitelModel, LakareModel, QAModel, CertTypeModel, APP_CONFIG, lakarutlatandenProxy, $log) {
      'use strict';

      var showPatientId;
      if($window.sessionStorage.getItem('lakarutlatandeShowPatientId')) {
        showPatientId = $window.sessionStorage.getItem('lakarutlatandeShowPatientId') === 'true';
      } else {
        showPatientId = true;
      }

      var state = {
        diagnosKapitelModel: DiagnosKapitelModel,
        lakareModel: LakareModel,
        qaModel: QAModel, //null = show all
        showPatientId: showPatientId,
        signDateModel: {
          from: null, to: null
        },
        certTypeModel: CertTypeModel
      };

      //Kanske initiera diagnoskapitelmodellen samtidigt som UserModel i appmain eller något?
      state.diagnosKapitelModel.set(APP_CONFIG.diagnosKapitelList);

      function _initQAModel() {
        state.qaModel.set([
          {id: null, displayValue: 'Visa alla', defaultSelected: true},
          {id: 1, displayValue: 'Visa enbart läkarutlåtanden utan obesvarade ärenden'},
          {id: 2, displayValue: 'Visa enbart läkarutlåtanden med obesvarade ärenden'},
          {id: 3, displayValue: 'Visa läkarutlåtanden med obesvarade kompletteringar'},
          {id: 4, displayValue: 'Visa läkarutlåtanden med obesvarade administrativa frågor och svar'}]);
      }

      state.certTypeModel.set([
        {id: 'FK7800', displayValue: 'Läkarutlåtande för sjukersättning, FK7800'},
        {id: 'FK7801', displayValue: 'Läkarutlåtande för aktivitetsersättning vid nedsatt arbetsförmåga, FK7801'},
        {id: 'FK7802', displayValue: 'Läkarutlåtande för aktivitetsersättning vid förlängd skolgång, FK7802'}]);


      function loadDoctors() {
        lakarutlatandenProxy.getDoctorsForUnit().then(function(response){
          state.lakareModel.setDoctors(response.doctors);
        }, function(errorData) {
          $log.debug('Failed to get signing doctors.');
          $log.debug(errorData);
        });

      }

      function _partialReset() {
        state.diagnosKapitelModel.reset();
        state.aldersModel = [0, 101];
        state.signDateModel.from = null;
        state.signDateModel.to = null;
        state.lakareModel.reset();
        state.qaModel.reset();
        state.freeTextModel = '';
        state.certTypeModel.reset();
        loadDoctors();
      }

      function _reset() {
        state.diagnosKapitelModel.reset();
        state.aldersModel = [0, 101];
        state.signDateModel.from = null;
        state.signDateModel.to = null;
        state.lakareModel.reset();
        state.qaModel.reset();
        state.showPatientId = true;
        state.freeTextModel = '';
        state.certTypeModel.reset();
        loadDoctors();
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
          state.qaModel.reset();
        }

        if (!columnsByKey.patientAge) {
          state.aldersModel = [0, 101];
        }

        if (!columnsByKey.signDate) {
          state.signDateModel.from = null;
          state.signDateModel.to = null;
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
        angular.forEach(state.qaModel.getSelected(), function(value) {
          this.push(value.id);
        }, selectedKompletteringOptions);

        var selectedCertType = [];
        angular.forEach(state.certTypeModel.getSelected(), function(value) {
          this.push(value.id);
        }, selectedCertType);

        return {
          diagnosKapitel: selectedDiagnosKapitel,
          lakare: selectedLakare,
          komplettering: selectedKompletteringOptions.length === 1 ? selectedKompletteringOptions[0] : null,
          alder: [state.aldersModel[0],
            state.aldersModel[1] > 100 ? null : state.aldersModel[1]],
          signDate: state.signDateModel,
          freeText: state.freeTextModel,
          showPatientId: state.showPatientId,
          certType: selectedCertType
        };

      }

      function _getState() {
        return state;
      }

      function _isAnyFilterSet() {
        var selectedFilter = _getCurrentFilterState();
        var isSet = selectedFilter.signDate.from === null && selectedFilter.signDate.to === null &&
            selectedFilter.alder[0] === 0 && selectedFilter.alder[1] === null &&
            selectedFilter.freeText === '' && selectedFilter.certType.length === 0 &&
            selectedFilter.diagnosKapitel.length === 0 && selectedFilter.komplettering === null &&
            selectedFilter.lakare.length === 0;
        return !isSet;
      }

      return {
        partialReset: _partialReset,
        reset: _reset,
        resetIfColumnsHidden: _resetIfColumnsHidden,
        getCurrentFilterState: _getCurrentFilterState,
        get: _getState,
        initQAModel: _initQAModel,
        isAnyFilterSet: _isAnyFilterSet
      };
    })
;
