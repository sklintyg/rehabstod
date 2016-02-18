angular.module('rehabstodApp')
    .controller('SjukfallStartSelectionCtrl', function($scope, $state, UserModel, UserProxy) {
        'use strict';

        $scope.onSelectUrval = function(urval) {
            UserProxy.changeUrval(urval).then(function(updatedUserModel) {
                UserModel.set(updatedUserModel);

                $state.go('app.sjukfall.result');
            }, function() {
                //Handle errors
            });

        };
    });
