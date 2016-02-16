angular.module('rehabstodApp')
    .config(function($stateProvider) {
        'use strict';
        $stateProvider
            .state('app.sjukfall', {

                data: {
                    rule: function(fromState, toState, UserModel) {

                        if (toState.name !== 'app.sjukfall') {
                            return;
                        }

                        var to = 'app.sjukfall.start';

                        if (UserModel.isUrvalSet()) {
                            to =  'app.sjukfall.result';
                        }

                        return {
                            to: to,
                            params: {},
                            options: {location: 'replace'}
                        };
                    }
                }
            });


    });