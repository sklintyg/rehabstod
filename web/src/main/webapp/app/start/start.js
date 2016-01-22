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
                    }
                }
            });


    });