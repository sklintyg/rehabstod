angular.module('rehabstodApp').factory('SjukfallModel',
    function() {
        'use strict';

        var data = [];

        function _reset() {
            data = [];
            return data;
        }

        return {

            reset: _reset,
            init: function() {
                return _reset();
            },

            set: function(newData) {
                _reset();
                data = newData;
            },
            get: function() {
                return data;
            }
        };
    }
);