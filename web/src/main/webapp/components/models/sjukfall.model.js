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
    function($parse, $filter) {
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

        function _getGradShow(aktivGrad, grader) {

            var gradShow = {show: ''};

            angular.forEach(grader, function(grad) {
                if (grad === aktivGrad) {
                    this.show += '<span class="rhs-table-grad-active">' + grad + '% </span>';
                }
                else {
                    this.show += grad + '% ';
                }
            }, gradShow);


            return gradShow.show;
        }

        return {

            reset: _reset,
            init: function() {
                return _reset();
            },

            set: function(newData) {
                hasError = false;
                data = newData;

                angular.forEach(data, function(item) {
                    item.patient.konShow = _getKon(item.patient.kon);
                    item.dagarShow = _getDagar(item.dagar);
                    item.gradShow = _getGradShow(item.aktivGrad, item.grader);
                    item.quickSearchString = '';
                    _addQuickSearchContentFromProperty(item, 'patient.id');
                    _addQuickSearchContentFromProperty(item, 'patient.namn');
                    _addQuickSearchContent(item, '(' + item.patient.alder + ' år)');
                    _addQuickSearchContentFromProperty(item, 'patient.konShow');
                    _addQuickSearchContentFromProperty(item, 'diagnos.intygsVarde');
                    _addQuickSearchContentFromProperty(item, 'start');
                    _addQuickSearchContentFromProperty(item, 'slut');
                    _addQuickSearchContent(item, item.dagarShow + '(' + item.intyg + ' intyg)');
                    _addQuickSearchContentFromProperty(item, 'intyg');
                    _addQuickSearchContent(item, angular.isArray(item.grader) ? item.grader.join('%,') + '%' : '');
                    _addQuickSearchContentFromProperty(item, 'lakare.namn');
                });
            },
            get: function() {
                return data;
            },
            setError: function(){
                _reset();
                hasError = true;
            },
            hasError: function() {
                return hasError;
            }
        };
    }
);