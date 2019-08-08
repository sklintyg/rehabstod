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

angular.module('rehabstodApp').factory('SjukfallModel',
    function($parse, $filter, SjukfallViewState, UserService, _) {
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
                }
                else {
                    this.push(grad + '% ');
                }
            }, items);

            //Add right-arrow symbol
            return items.join(' &#10142; ');
        }

        function _getObesvaradeKomplShow(obesvaradeKompl) {
            if (SjukfallViewState.get().kompletteringInfoError) {
                //indicate no value due to error
                return '';
            }
            return (obesvaradeKompl > 0) ? 'Obesvarad (' + obesvaradeKompl + ')' : '-';
        }


        function _updateQuickSearchContent() {
            angular.forEach(data, function(item) {
                item.patient.konShow = _getKon(item.patient.kon);
                item.dagarShow = _getDagar(item.dagar);
                item.gradShow = _getGradShow(item.aktivGrad, item.grader);
                item.obesvaradeKomplShow = _getObesvaradeKomplShow(item.obesvaradeKompl);
                item.quickSearchString = '';

                var columns = UserService.getSelectedColumns(UserService.getAllSjukfallTableColumns(), UserService.sjukfallTableKey);

                _.each(columns, function(column) {
                  /*jshint maxcomplexity:14 */
                  switch (column.id) {
                  case 'patient.id':
                  case 'patient.namn':
                  case 'patient.konShow':
                  case 'start':
                  case 'slut':
                  case 'intyg':
                  case 'lakare.namn':
                    _addQuickSearchContentFromProperty(item, column.id);
                    break;
                  case 'diagnos.intygsVarde':
                    _addQuickSearchContentFromProperty(item, 'diagnos.intygsVarde');
                    _addQuickSearchContentFromProperty(item, 'diagnos.beskrivning');
                    _addQuickSearchContent(item, _getBiDiagnoserSearch(item.biDiagnoser));
                    break;
                  case 'patient.alder':
                    _addQuickSearchContent(item, item.patient.alder);
                    break;
                  case 'dagar':
                    _addQuickSearchContent(item, item.dagarShow);
                    break;
                  case 'obesvaradeKompl':
                    _addQuickSearchContent(item, item.obesvaradeKomplShow);
                    break;
                  case 'aktivGrad':
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