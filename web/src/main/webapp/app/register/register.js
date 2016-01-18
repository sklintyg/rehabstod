angular.module('privatlakareApp')
    .config(function($stateProvider) {
        'use strict';
        $stateProvider
            .state('app.register', {
                url: '/registrera',
                views: {
                    'content@app': {
                        templateUrl: 'app/register/register.html',
                        controller: 'RegisterCtrl'
                    }
                }
            });
    });