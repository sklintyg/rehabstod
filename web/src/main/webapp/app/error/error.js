angular.module('privatlakareApp')
    .config(function ($stateProvider) {
        'use strict';
        $stateProvider
            .state('app.error', {
                url: '/error',
                views: {
                    'content@app': {
                        templateUrl: 'app/error/error.html',
                        controller: 'ErrorCtrl'
                    }
                },
                params: {
                    errorMessage : ''
                }
            });
    });