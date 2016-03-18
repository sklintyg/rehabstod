angular.module('rehabstodApp')
    .config(function($stateProvider) {
        'use strict';
        $stateProvider
            .state('app.sjukfall.start', {
                url: 'start',
                views: {
                    'content@app': {
                        templateUrl: 'app/sjukfall/start/start.html',
                        controller: 'SjukfallStartCtrl'
                    }
                }
            }).state('app.sjukfall.start.nodata', {
                views: {
                    'content@app': {
                        templateUrl: 'app/sjukfall/start/selection/nodata.html',
                        controller: 'NoDataCtrl'
                    }
                }
            })
            .state('app.sjukfall.start.lakare', {
                views: {
                    'content@app': {
                        templateUrl: 'app/sjukfall/start/selection/lakare.html',
                        controller: 'SjukfallStartSelectionCtrl'
                    }
                }
            })
            .state('app.sjukfall.start.rehabkoordinator', {
                views: {
                    'content@app': {
                        templateUrl: 'app/sjukfall/start/selection/rehabkoordinator.html',
                        controller: 'SjukfallStartSelectionCtrl'
                    }
                }
            });


    });