angular.module('rehabstodApp')
    .config(function($stateProvider) {
        'use strict';
        $stateProvider
            .state('app.about', {
                url: '/about',
                views: {
                    'content@app': {templateUrl: 'app/about/about.page.html'}
                }
            });
    });