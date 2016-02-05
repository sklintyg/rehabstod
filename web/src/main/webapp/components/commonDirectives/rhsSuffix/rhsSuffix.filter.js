angular.module('rehabstodApp').filter('rhsSuffix', function() {
    'use strict';

    return function(input, suffix, noValue) {

        var returnValue = suffix ? input + ' ' + suffix : input;

        return input || input === 0 ? returnValue : noValue;
    };
});