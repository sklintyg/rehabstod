angular.module('rehabstodApp')
    .config(function($stateProvider) {
        'use strict';
        $stateProvider
            .state('app.start', {
                url: '/',
                views: {
                    'content@app': {
                        templateUrl: 'app/start/start.page.html'
                    },
                    'header@app.start': {
                        templateUrl: 'app/start/header/header.html'
                    }
                },
                data: {
                    rule: function(fromState, toState, UserModel) {

                        if (toState.name !== 'app.start') {
                            return;
                        }

                        if (UserModel.isLakare()) {
                            return {
                                to: 'app.start.lakare',
                                params: {},
                                options: {location: 'replace'}
                            };
                        } else {
                            return {
                                to: 'app.start.rehabkoordinator',
                                params: {},
                                options: {location: 'replace'}
                            };
                        }
                    }
                }
            })
            .state('app.start.lakare', {
                views: {
                    'selection@app.start': {
                        templateUrl: 'app/start/selection/lakare.html',
                        controller: 'SelectionCtrl'
                    }
                }
            })
            .state('app.start.rehabkoordinator', {
                views: {
                    'selection@app.start': {
                        templateUrl: 'app/start/selection/rehabkoordinator.html',
                        controller: 'SelectionCtrl'
                    }
                }
            });


    });