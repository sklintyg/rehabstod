angular.module('privatlakareApp').factory('AppTermsModalModel',
    function($sessionStorage) {
        'use strict';

        var data = {};

        function _init() {
            if ($sessionStorage.appTermsModalModel) {
                data = $sessionStorage.appTermsModalModel;
            }
            else {
                $sessionStorage.appTermsModalModel = _reset();
            }
            return data;
        }

        function _reset() {
            data.modalInstance = null;
            data.options = {
                controller: null,
                modalBodyTemplateUrl: 'app/terms/terms.body.html',
                titleId: null,
                extraDlgClass: undefined,
                width: '600px',
                height: '90%',
                maxWidth: '600px',
                maxHeight: undefined,
                minWidth: undefined,
                minHeight: undefined,
                contentHeight: '100%',
                contentOverflowY: undefined,
                contentMinHeight: undefined,
                bodyOverflowY: 'scroll',
                buttons: [],
                showClose: false
            };

            return data;
        }

        function _set(newData) {
            data.modalInstance = newData.modalInstance;
            data.options = newData.options;
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