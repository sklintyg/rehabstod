angular.module('rehabstodApp')
    .controller('NoDataCtrl', function($scope, $rootScope, $uibModal, $state, UserModel, UserProxy) {
        'use strict';

        $scope.user = UserModel.get();

        var unregisterFn = $rootScope.$on('SelectedUnitChanged', function(/*event, value*/) {
            $state.go('app.sjukfall.start');
        });
        //rootscope on event listeners aren't unregistered automatically when 'this' directives
        //scope is destroyed, so let's take care of that.
        $scope.$on('$destroy', unregisterFn);

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