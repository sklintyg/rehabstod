angular.module('rehabstodApp').config(function($stateProvider) {
    'use strict';
    $stateProvider.state('app.index', {
        url: '/index',
        views: {
            'header@': {
                templateUrl: '/app/index/index.header.html'
            },
            'navbar@': {},
            'app@': {
                templateUrl: '/app/index/index.body.html'
            }

        }
    });

});
