angular.module('rehabstodApp')
    .controller('SelectionCtrl', function($scope, $state, UserModel, UserProxy) {
        'use strict';

        $scope.onSelectUrval = function(urval) {
            UserProxy.changeUrval(urval).then(function(updatedUserModel) {
                UserModel.set(updatedUserModel);

                $state.go('app.sjukfall');
            }, function() {
                //Handle errors
            });

        };
    });
