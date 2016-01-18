angular.module('privatlakareApp')
    .controller('RegisterAbortCtrl', function($scope, $sessionStorage, UserModel, RegisterModel, WindowUnload) {
        'use strict';

        $scope.dismiss = function() {
            WindowUnload.enable();
            $scope.$dismiss();
        };

        $scope.abort = function() {
            WindowUnload.disable();
            $sessionStorage.registerModel = RegisterModel.reset();
            UserModel.logout();
        };
    });
