angular.module('rehabstodApp').factory('DiagnosKapitelModel',
    function() {
        'use strict';

        var data = [];

        function _init() {
            data = [];
            return data;
        }

        function _reset() {
            angular.forEach(data, function(value) {
                value.selected = false;
                value.disabled = false;
            });
            return data;
        }

        return {

            reset: _reset,

            set: function(diagnosKapitel) {
                _init();
                angular.forEach(diagnosKapitel, function(value) {
                    data.push({
                        _definition: value,
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
                        selected.push(value);
                    }
                });
                return selected;
            }
        };
    }
);