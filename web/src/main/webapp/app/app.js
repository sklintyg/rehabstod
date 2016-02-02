angular.module('rehabstodApp')
    .config(function($stateProvider) {
        'use strict';
        //This is the App's root that uses index.html as template
        $stateProvider
            .state('app', {
                views: {
                    'app@': { templateUrl: 'app/app.html'},
                    'header@': { templateUrl: 'app/header/header.html', controller: 'HeaderController' },
                    'navbar@': { templateUrl: 'app/navbar/navbar.html', controller: 'NavbarCtrl' }
                }
            });
    });