angular.module('privatlakareApp')
    .config(function($stateProvider) {
        'use strict';
        $stateProvider
            .state('app.register.step1', {
                url: '/steg1',
                views: {
                    step: {
                        templateUrl: 'app/register/step1/step1.html',
                        controller: 'Step1Ctrl'
                    }
                },
                data: { step: 1 }
            });
    });