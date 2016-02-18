angular.module('rehabstodApp')
    .config(function($stateProvider) {
        'use strict';
        $stateProvider
            .state('app.sjukfall.result', {
                url: 'sjukfall',
                views: {
                    'content@app': {
                        templateUrl: 'app/sjukfall/result/result.page.html',
                        controller: 'SjukfallResultPageCtrl'
                    }
                },
                data: {
                    rule: function(fromState, toState, UserModel) {
                        if (!UserModel.isUrvalSet()) {
                            return {
                                to: 'app.sjukfall.start',
                                params: {},
                                options: {location: 'replace'}
                            };
                        }
                    }
                }
            });
    });