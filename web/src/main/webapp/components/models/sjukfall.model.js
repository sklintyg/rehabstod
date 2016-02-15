angular.module('rehabstodApp').factory('SjukfallModel',
    function($parse) {
        'use strict';

        var data = [];

        function _reset() {
            data = [];
            return data;
        }

        function _getKon(kon) {
            if (angular.isDefined(kon)) {
                return (kon === 'M') ? 'Man' : 'Kvinna';
            }
            return '';
        }

        function _addQuickSearchContentFromProperty(item, propertyExpression) {
            item.quickSearchString += $parse(propertyExpression)(item) + ':';
        }
        function _addQuickSearchContent(item, content) {
            item.quickSearchString += content + ':';
        }

        return {

            reset: _reset,
            init: function() {
                return _reset();
            },

            set: function(newData) {
                data = newData;

                angular.forEach(data, function(item) {
                    _addQuickSearchContentFromProperty(item, 'patient.id');
                    _addQuickSearchContentFromProperty(item, 'patient.namn');
                    _addQuickSearchContentFromProperty(item, 'patient.alder');
                    _addQuickSearchContent(item, angular.isDefined(item.patient)?_getKon(item.patient.kon):'');
                    _addQuickSearchContentFromProperty(item, 'diagnos.intygsVarde');
                    _addQuickSearchContentFromProperty(item, 'start');
                    _addQuickSearchContentFromProperty(item, 'slut');
                    _addQuickSearchContentFromProperty(item, 'dagar');
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