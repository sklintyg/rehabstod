angular.module('rehabstodApp')
    .controller('SelectionCtrl', function($scope, $state, AppNavViewstate) {
        'use strict';

        $scope.onSelectUrval = function(urval) {
            AppNavViewstate.setVisningsLage(urval);
            $state.go('app.sjukfall');
        };
    });
