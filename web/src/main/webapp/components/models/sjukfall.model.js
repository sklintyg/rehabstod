angular.module('rehabstodApp').factory('SjukfallModel',
    function($parse, $filter) {
        'use strict';

        var data = [];

        function _reset() {
            data = [];
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

            var gradShow = { show: ''};

            angular.forEach(grader, function(grad) {
                if (grad === aktivGrad) {
                    this.show += '<span class="rhs-table-grad-active">'+ grad +'% </span>';
                }
                else {
                    this.show += grad + '% ';
                }
            },gradShow);


            return gradShow.show;
        }

        return {

            reset: _reset,
            init: function() {
                return _reset();
            },

            set: function(newData) {
                data = newData;

                angular.forEach(data, function(item) {
                    item.patient.konShow = _getKon(item.patient.kon);
                    item.dagarShow = _getDagar(item.dagar);
                    item.gradShow = _getGradShow(item.aktivGrad, item.grader);
                    item.quickSearchString = '';
                    _addQuickSearchContentFromProperty(item, 'patient.id');
                    _addQuickSearchContentFromProperty(item, 'patient.namn');
                    _addQuickSearchContentFromProperty(item, 'patient.alder');
                    _addQuickSearchContentFromProperty(item, 'patient.konShow');
                    _addQuickSearchContentFromProperty(item, 'diagnos.intygsVarde');
                    _addQuickSearchContentFromProperty(item, 'start');
                    _addQuickSearchContentFromProperty(item, 'slut');
                    _addQuickSearchContentFromProperty(item, 'dagar');
                    _addQuickSearchContentFromProperty(item, 'intyg');
                    _addQuickSearchContent(item, angular.isArray(item.grader) ? item.grader.join(',') : '');
                    _addQuickSearchContentFromProperty(item, 'lakare');
                });
            },
            get: function() {
                return data;
            }
        };
    }
);