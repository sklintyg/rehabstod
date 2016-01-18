angular.module('privatlakareApp').factory('StringHelper',
    function() {
        'use strict';

        return {
            replaceAll: function(str, search, replacement) {
                return str.split(search).join(replacement);
            }
        };
    }
);
