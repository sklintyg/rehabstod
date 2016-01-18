angular.module('privatlakareApp')
    .config(function($stateProvider) {
        'use strict';
        $stateProvider
            .state('app.register.step2', {
                url: '/steg2',
                views: {
                    step: {
                        templateUrl: 'app/register/step2/step2.html',
                        controller: 'Step2Ctrl'
                    }
                },
                data: { step: 2 }
            });
    });