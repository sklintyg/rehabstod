angular.module('rehabstodApp')
    .controller('SjukfallStartSelectionCtrl', function($scope, $state, UserModel, UserProxy, SjukfallService) {
        'use strict';

        $scope.onSelectUrval = function(urval) {
            UserProxy.changeUrval(urval).then(function(updatedUserModel) {
                UserModel.set(updatedUserModel);

                $state.go('app.sjukfall.result');

                SjukfallService.loadSjukfall(true);

            }, function() {
                //Handle errors
            });

        };
    });
