angular.module('rehabstodApp')
    .config(function($stateProvider) {
        'use strict';
        $stateProvider
            .state('app.sjukfall', {
                url: '/sjukfall',
                views: {
                    'content@app': { templateUrl: 'app/sjukfall/sjukfall.page.html', controller: 'SjukfallPageCtrl' },
                    'statistics@app.sjukfall': { templateUrl: 'app/sjukfall/statistics/statistics.html' },
                    'searchfilter@app.sjukfall': { templateUrl: 'app/sjukfall/searchfilter/searchfilter.html', controller: 'SearchFilterCtrl'},
                    'searchresults@app.sjukfall': { templateUrl: 'app/sjukfall/searchresults/searchresults.html', controller: 'SearchResultsCtrl' }
                }
            });


    });