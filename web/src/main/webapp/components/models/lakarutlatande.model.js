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

angular.module('rehabstodApp').factory('LakarutlatandeModel',
    function($parse, $filter, LakarutlatandeViewState, TableService, _) {
      'use strict';

      var data = [];
      var hasError = false;
      var initialDataState = true;

      function _reset() {
        data = [];
        hasError = false;
        initialDataState = true;
        return data;
      }

      function _getKon(kon) {
        if (angular.isDefined(kon)) {
          return $filter('rhsKon')(kon);
        }
        return '';
      }

      function _addQuickSearchContentFromProperty(item, propertyExpression) {
        item.quickSearchString += $parse(propertyExpression)(item) + ':';
      }

      function _addQuickSearchContent(item, content) {
        item.quickSearchString += content + ':';
      }

      function _getBiDiagnoserSearch(diagnoser) {
        var asText = '';
        if (diagnoser) {
          angular.forEach(diagnoser, function(diagnos) {
            asText += diagnos.intygsVarde;
          });
        }
        return asText;
      }

      function _updateQuickSearchContent() {
        angular.forEach(data, function(item) {
          item.patient.konShow = _getKon(item.patient.kon);
          item.quickSearchString = '';

          item.highestNbrOfArenden = item.unAnsweredComplement ? item.unAnsweredComplement : item.unAnsweredOther;
          item.signingTimeStamp = item.signingTimeStamp.split('T')[0];

          var columns = TableService.getSelectedLakarutlatandeUnitColumns();

          _.each(columns, function(column) {
            /*jshint maxcomplexity:14 */
            switch (column.id) {
            case 'patientId':
            case 'patientName':
            case 'gender':
            case 'signeringsdatum':
            case 'doctor':
            case 'certType':
              _addQuickSearchContentFromProperty(item, column.dataColumn);
              break;
            case 'dxs':
              _addQuickSearchContentFromProperty(item, 'diagnos.intygsVarde');
              _addQuickSearchContentFromProperty(item, 'diagnos.beskrivning');
              _addQuickSearchContent(item, _getBiDiagnoserSearch(item.biDiagnoser));
              break;
            case 'patientAge':
              _addQuickSearchContent(item, item.patient.alder);
              break;
            }
          });

        });
      }

      return {

        reset: _reset,
        init: function() {
          return _reset();
        },

        set: function(newData) {
          initialDataState = false;
          hasError = false;
          data = newData;
          _updateQuickSearchContent();
        },
        get: function() {
          return data;
        },
        setError: function() {
          _reset();
          hasError = true;
          initialDataState = true;
        },
        hasError: function() {
          return hasError;
        },
        isInitialState: function() {
          return initialDataState;
        },
        updateQuickSearchContent: _updateQuickSearchContent
      };
    }
);