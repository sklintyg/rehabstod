angular.module('privatlakareApp')
    .config(function($stateProvider) {
        'use strict';
        $stateProvider
            .state('app.register.complete', {
                url: '/klar',
                views: {
                    'content@app': {
                        templateUrl: 'app/register/complete/complete.html',
                        controller: 'CompleteCtrl'
                    }
                }
            });
    });