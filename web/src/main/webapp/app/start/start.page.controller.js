angular.module('rehabstodApp')
    .controller('StartPageCtrl', function($scope, $log, $state, UserModel) {
        'use strict';
        // UserModel properties are used by almost all sub-view's.
        // We expose it here in the applications main controller's scope and it will prototypically trickle
        // down to those sub-state scopes as well. If we feel this is a bad idea, we have to inject them in almost
        // every controller.

        $scope.user = UserModel.get();


    });