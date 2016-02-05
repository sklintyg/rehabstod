angular.module('rehabstodApp').factory('SjukfallSummaryModel',
    function() {
        'use strict';

        var data = {};

        _reset();

        function _reset() {
            data.total = null;
            data.men = null;
            data.women = null;

            return data;
        }

        function _set(newData) {
            data.total = newData.total;
            data.men = newData.men;
            data.women = newData.women;
        }

        function _get() {
            return data;
        }

        return {
            reset: _reset,
            set: _set,
            get: _get
        };
    }
);