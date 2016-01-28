angular.module('rehabstodApp')
    .config(function($stateProvider) {
        'use strict';
        $stateProvider
            .state('app.start', {
                url: '/',
                views: {
                    'content@app': {
                        templateUrl: 'app/start/start.page.html',
                        controller: 'StartPageCtrl'
                    },
                    'header@app.start': {
                        templateUrl: 'app/start/header/header.html',
                        controller: 'SelectionHeaderCtrl'
                    }
                }
            })
            .state('app.start.lakare', {
                views: {
                    'selection@app.start': {
                        templateUrl: 'app/start/selection/lakare.html',
                        controller: 'SelectionCtrl'
                    }
                }
            })
            .state('app.start.rehabkoordinator', {
                views: {
                    'selection@app.start': {
                        templateUrl: 'app/start/selection/rehabkoordinator.html',
                        controller: 'SelectionCtrl'
                    }
                }
            });


    });