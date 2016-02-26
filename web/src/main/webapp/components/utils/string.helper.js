angular.module('rehabstodApp').factory('StringHelper',
    function() {
        'use strict';

        function escapeRegExp(str) {
            return str.replace(/([.*+?^=!:${}()|\[\]\/\\])/g, '\\$1');
        }

        return {
            replaceAll: function(str, search, replacement) {
                return str.replace(new RegExp(escapeRegExp(search), 'g'), replacement);
            }
        };
    }
);
