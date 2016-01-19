angular.module('rehabstodApp').filter('message', function(messageService) {
    'use strict';
    return function(input) {
        return messageService.getProperty(input);
    };
});