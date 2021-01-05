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

angular.module('rehabstodApp').factory('SjukfallModel',
    function($parse, $filter, SjukfallViewState, TableService, _) {
      'use strict';

      var data = [];
      var hasError = false;

      function _reset() {
        data = [];
        hasError = false;
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

      function _getDagar(dagar) {
        var suffix = ' dagar';
        if (dagar === 1) {
          suffix = ' dag';
        }

        return dagar + suffix;
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

      function _getGradShow(aktivGrad, grader) {

        var items = [];

        angular.forEach(grader, function(grad) {
          if (grad === aktivGrad) {
            this.push('<span class="rhs-table-grad-active">' + grad + '% </span>');
          } else {
            this.push(grad + '% ');
          }
        }, items);

        //Add right-arrow symbol
        return items.join(' &#10142; ');
      }

      function _getQAString(unansweredComplement, unansweredOther) {
        if (SjukfallViewState.get().kompletteringInfoError) {
          //indicate no value due to error
          return '';
        } else if (unansweredComplement === 0 && unansweredOther === 0) {
          return '-';
        } else {
          var s = '';
          if(unansweredComplement !== 0) {
            s = 'Komplettering (' + unansweredComplement + ')';
          }
          if(unansweredOther !== 0) {
            s = s + '\nAdministrativ fråga (' + unansweredOther + ')';
          }
          return s;
        }
      }

      function _getHighestNbrOfQA(unansweredComplement, unansweredOther) {
        return unansweredComplement > 0 ? unansweredComplement : (unansweredOther > 0 ? unansweredOther : 0);
      }

      function _updateQuickSearchContent() {
        angular.forEach(data, function(item) {
          item.patient.konShow = _getKon(item.patient.kon);
          item.dagarShow = _getDagar(item.dagar);
          item.gradShow = _getGradShow(item.aktivGrad, item.grader);
          item.qaString = _getQAString(item.obesvaradeKompl, item.unansweredOther);
          item.highestNbrOfQA = _getHighestNbrOfQA(item.obesvaradeKompl, item.unansweredOther);
          item.quickSearchString = '';

          var columns = TableService.getSelectedSjukfallColumns();

          _.each(columns, function(column) {
            /*jshint maxcomplexity:14 */
            switch (column.id) {
            case 'patientId':
            case 'patientName':
            case 'gender':
            case 'startDate':
            case 'endDate':
            case 'antal':
            case 'doctor':
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
            case 'days':
              _addQuickSearchContent(item, item.dagarShow);
              break;
            case 'qa':
              _addQuickSearchContent(item, item.qaString);
              break;
            case 'degree':
              _addQuickSearchContent(item, angular.isArray(item.grader) ? item.grader.join('%,') + '%' : '');
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
        },
        hasError: function() {
          return hasError;
        },
        updateQuickSearchContent: _updateQuickSearchContent
      };
    }
);