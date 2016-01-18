angular.module('privatlakareApp')
    .config(function($stateProvider) {
        'use strict';
        $stateProvider
            .state('app.register.waiting', {
                url: '/vanta',
                views: {
                    'content@app': {
                        templateUrl: 'app/register/waiting/waiting.html',
                        controller: 'WaitingCtrl'
                    }
                }
            });
    });