angular.module('privatlakareApp').factory('TermsModel',
    function($sessionStorage) {
        'use strict';

        var data = {};

        function _init() {
            if ($sessionStorage.termsModel) {
                data = $sessionStorage.termsModel;
            }
            else {
                $sessionStorage.termsModel = _reset();
            }
            return data;
        }

        function _reset() {

            data.text = null;
            data.date = null;
            data.version = null;

            return data;
        }

        function _set(newData) {
            data.text = newData.text;
            data.date = newData.date;
            data.version = newData.version;
        }

        function _get() {
            return data;
        }

        function _isEmpty() {
            return !data.text;
        }

        return {
            init: _init,
            reset: _reset,
            set: _set,
            get: _get,
            isEmpty: _isEmpty
        };
    }
);