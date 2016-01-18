angular.module('privatlakareApp')
    .config(function ($stateProvider) {
        'use strict';
        $stateProvider
            .state('app.register.step3', {
                url: '/steg3',
                views: {
                    step: {
                        templateUrl: 'app/register/step3/step3.html',
                        controller: 'Step3Ctrl'
                    }
                },
                data: { step:3 }
            });
    });