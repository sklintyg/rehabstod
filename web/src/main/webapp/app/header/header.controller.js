angular.module('rehabstodApp').controller('HeaderController',
    function($scope, $window, $state, $log, UserProxy, UserModel, $uibModal) {
        'use strict';

        //Expose 'now' as a model property for the template to render as todays date
        $scope.today = new Date();
        $scope.user = UserModel.get();

        /**
         * Private functions
         */

        /**
         * Exposed scope interaction functions
         */

        $scope.logoutLocation = UserModel.getLogoutLocation();

        $scope.showRoleDescription = function(role) {
            return role.name === 'LAKARE';
        };

        $scope.openChangeCareUnitDialog = function() {
            var modalInstance = $uibModal.open({
                animation: true,
                templateUrl: 'app/header/careunit/select-care-unit-dialog.html',
                controller: 'SelectCareUnitCtrl',
                size: 'md'
            });

            modalInstance.result.then(function(enhet) {
                $log.debug('SelectCareUnit Modal closed with a selection :' + enhet.id);
                UserProxy.changeSelectedUnit(enhet.id).then(function(updatedUserModel) {
                    UserModel.set(updatedUserModel);

                    $scope.$emit('SelectedUnitChanged', {enhet: enhet.id});
                }, function() {
                    //Handle errors
                });
            }, function() {
                $log.debug('SelectCareUnit Modal cancelled');
            });

        };

    }
);
