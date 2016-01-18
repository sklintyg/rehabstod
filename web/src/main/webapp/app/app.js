angular.module('privatlakareApp')
    .config(function($stateProvider) {
        'use strict';
        $stateProvider
            .state('app', {
                views: {
                    'app@': { templateUrl: 'app/app.html', controller: 'AppCtrl' },
                    'header@': { templateUrl: 'app/header/header.html', controller: 'HeaderController' }
                }
            });
    });