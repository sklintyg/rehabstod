angular.module('rehabstodApp').factory('LakareModel',
    function() {
        'use strict';

        var data = [];

        function _init() {
            data.splice(0, data.length);
            return data;
        }

        //resets selection/disabled states
        function _reset() {
            angular.forEach(data, function(value) {
                value.selected = false;
                value.disabled = false;
            });
            return data;
        }

        return {

            reset: _reset,

            set: function(lakareArray) {
                _init();
                angular.forEach(lakareArray, function(value) {
                    data.push({
                        id: value,
                        displayValue: value,
                        selected: false,
                        disabled: false
                    });
                });
            },
            get: function() {
                return data;
            },

            getNames: function() {
                var names = [];

                angular.forEach(data, function(value) {
                    names.push(value.id);
                });

                return names;
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