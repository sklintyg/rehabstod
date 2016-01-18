angular.module('privatlakareApp').factory('HospModel',
    function() {
        'use strict';

        var data = {};

        function _init() {
            return _reset();
        }

        function _reset() {
            data.legitimeradYrkesgrupp = null;
            data.specialitet = null;
            data.forskrivarkod = null;
            return data;
        }

        function _set(newData) {
            data = angular.copy(newData);
        }

        function _get() {
            return data;
        }
        
        return {
            init: _init,
            reset: _reset,
            set: _set,
            get: _get
        };
    }
);