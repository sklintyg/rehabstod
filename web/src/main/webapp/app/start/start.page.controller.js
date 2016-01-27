angular.module('rehabstodApp')
    .controller('StartPageCtrl', function($scope, $state, $log, AppNavViewstate) {
        'use strict';
        $log.debug('StartPageCtrl init');

        $scope.onSelectUrval = function(urval) {
            AppNavViewstate.setVisningsLage(urval);
            $state.go('app.sjukfall');
        };

    });
