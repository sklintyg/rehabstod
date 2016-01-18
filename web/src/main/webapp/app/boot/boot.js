angular.module('privatlakareApp')
    .config(function($stateProvider) {
        'use strict';
        $stateProvider
            .state('app.boot', {
                url: '/?:from',
                views: {
                    'content@app': { templateUrl: 'app/boot/boot.html', controller: 'BootCtrl' }
                }
            }).state('app.bootLink', {
                url: '/link/:targetId',
                views: {
                    'content@app': { templateUrl: 'app/boot/boot.html', controller: 'BootLinkCtrl' }
                }
            });
        });