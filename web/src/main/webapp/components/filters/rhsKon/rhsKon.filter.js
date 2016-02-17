angular.module('rehabstodApp').filter('rhsKon', [
    'messageService',
    function(messageService) {
        'use strict';

        return function(input) {

            switch(input) {
            case 'f':
            case 'F':
                return messageService.getProperty('label.gender.female');
            case 'm':
            case 'M':
                return messageService.getProperty('label.gender.male');
            default:
                return messageService.getProperty('label.gender.undefined');
            }
        };
    }]);