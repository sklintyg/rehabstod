angular.module('privatlakareApp')
    .config(function ($stateProvider) {
        'use strict';

        var abortUrl = '/avbryt';

        function showAbortDialog($state, dialogService) {
            dialogService.open({
                templateUrl: 'app/register/abort/abort.html',
                controller: 'RegisterAbortCtrl'
            }).result.finally(function() { //jshint ignore:line
                if ($state.current.url === abortUrl) {
                    $state.go('^');
                }
            });
        }

        function closeAbortDialog(dialogService) {
            dialogService.close();
        }

        $stateProvider
            .state('app.register.step1.abort', {
                url: abortUrl,
                onEnter: showAbortDialog,
                onExit: closeAbortDialog
            })
            .state('app.register.step2.abort', {
                url: abortUrl,
                onEnter: showAbortDialog,
                onExit: closeAbortDialog
            })
            .state('app.register.step3.abort', {
                url: abortUrl,
                onEnter: showAbortDialog,
                onExit: closeAbortDialog
            });
    });