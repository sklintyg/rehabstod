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

        function _createDisplayValue(kapitel) {
            return kapitel.id + ((kapitel.id !== '') ? ': ' : '') + kapitel.name;
        }

        return {

            reset: _reset,

            set: function(diagnosKapitel) {
                _init();
                angular.forEach(diagnosKapitel, function(value) {
                    data.push({
                        _definition: value,
                        id: value.id,
                        displayValue: _createDisplayValue(value),
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
            },
            setActivDiagnosKapitelIdlist: function(activeKapitelIds) {
                if (activeKapitelIds.length > 0) {
                    angular.forEach(data, function(kapitel) {
                        kapitel.disabled = activeKapitelIds.indexOf(kapitel.id) === -1;
                        if (kapitel.disabled) {
                            kapitel.selected = false;
                        }
                    });
                }
            }
        };
    }
);