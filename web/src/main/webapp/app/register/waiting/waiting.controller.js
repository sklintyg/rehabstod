angular.module('privatlakareApp')
    .controller('WaitingCtrl', function($scope, UserModel) {
        'use strict';

        $scope.logout = function() {
            UserModel.logout();
        };
    });
