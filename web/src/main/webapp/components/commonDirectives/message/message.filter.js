angular.module('privatlakareApp').filter('message', function(messageService) {
    'use strict';
    return function(input) {
        return messageService.getProperty(input);
    };
});