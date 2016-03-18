angular.module('rehabstodApp')
    .controller('NoDataCtrl', function($scope, $uibModal, $state, UserModel, UserProxy) {
        'use strict';

        $scope.user = UserModel.get();

        $scope.openChangeCareUnitDialog = function() {
            var modalInstance = $uibModal.open({
                animation: true,
                templateUrl: 'app/header/careunit/select-care-unit-dialog.html',
                controller: 'SelectCareUnitCtrl',
                size: 'md',
                windowClass: 'select-care-unit-modal'
            });

            modalInstance.result.then(function(enhet) {

                UserProxy.changeSelectedUnit(enhet.id).then(function(updatedUserModel) {
                    UserModel.set(updatedUserModel);

                    $state.go('app.sjukfall.result');
                }, function() {
                    //Handle errors
                });
            }, function() {

            });

        };

    });