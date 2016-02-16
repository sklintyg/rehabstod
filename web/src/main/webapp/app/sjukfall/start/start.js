angular.module('rehabstodApp')
    .config(function($stateProvider) {
        'use strict';
        $stateProvider
            .state('app.sjukfall.start', {
                url: '/',
                views: {
                    'content@app': {
                        templateUrl: 'app/sjukfall/start/start.page.html',
                        controller: 'SjukfallStartPageCtrl'
                    }
                },
                data: {
                    rule: function(fromState, toState, UserModel) {

                        if (toState.name !== 'app.sjukfall.start') {
                            return;
                        }

                        if (UserModel.isLakare()) {
                            return {
                                to: 'app.sjukfall.start.lakare',
                                params: {},
                                options: {location: 'replace'}
                            };
                        } else {
                            return {
                                to: 'app.sjukfall.start.rehabkoordinator',
                                params: {},
                                options: {location: 'replace'}
                            };
                        }
                    }
                }
            })
            .state('app.sjukfall.start.lakare', {
                views: {
                    'selection@app.sjukfall.start': {
                        templateUrl: 'app/sjukfall/start/selection/lakare.html',
                        controller: 'SjukfallStartSelectionCtrl'
                    }
                }
            })
            .state('app.sjukfall.start.rehabkoordinator', {
                views: {
                    'selection@app.sjukfall.start': {
                        templateUrl: 'app/sjukfall/start/selection/rehabkoordinator.html',
                        controller: 'SjukfallStartSelectionCtrl'
                    }
                }
            });


    });