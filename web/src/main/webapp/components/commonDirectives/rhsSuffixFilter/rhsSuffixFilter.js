angular.module('rehabstodApp').filter('rhsSuffixFilter', function() {
    'use strict';
    
    return function(input, suffix, noValue) {
        return input ? input + ' '+ suffix : noValue;
    };
});