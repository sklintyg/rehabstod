angular.module('rehabstodApp').factory('DiagnosGruppModel',
    function() {
        'use strict';

        var data = [];

        function _reset() {
            data = [];
            return data;
        }

        return {

            reset: _reset,

            set: function(diagnosGrupper) {
                _reset();
                angular.forEach(diagnosGrupper, function(value) {
                    data.push({
                        id: value.id,
                        displayValue: value.id + ': ' + value.name,
                        selected: false,
                        disabled: false
                    });
                });

            },
            get: function() {
                return data;
            },
            getSelected: function() {
                var selected = [];
                angular.forEach(data, function(value) {
                    if (value.selected) {
                        selected.push(value.id);
                    }
                });
                return selected;
            }
        };
    }
);