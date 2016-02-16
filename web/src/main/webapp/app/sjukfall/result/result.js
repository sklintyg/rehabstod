angular.module('rehabstodApp')
    .config(function($stateProvider) {
        'use strict';
        $stateProvider
            .state('app.sjukfall.result', {
                url: '/sjukfall',
                views: {
                    'content@app': {
                        templateUrl: 'app/sjukfall/result/result.page.html',
                        controller: 'SjukfallResultPageCtrl'
                    },
                    'searchfilter@app.sjukfall.result': {
                        templateUrl: 'app/sjukfall/result/searchfilter/searchfilter.html',
                        controller: 'SjukfallSearchFilterCtrl'
                    },
                    'searchresults@app.sjukfall.result': {
                        templateUrl: 'app/sjukfall/result/searchresults/searchresults.html',
                        controller: 'SjukfallSearchResultsCtrl'
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