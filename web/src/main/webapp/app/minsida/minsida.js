angular.module('privatlakareApp')
    .config(function($stateProvider) {
        'use strict';
        $stateProvider
            .state('app.minsida', {
                url: '/minsida',
                views: {
                    'content@app': {
                        templateUrl: 'app/minsida/minsida.html',
                        controller: 'MinsidaCtrl'
                    }
                }
            });
    });