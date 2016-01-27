angular.module('rehabstodApp').controller('HeaderController',
    function($scope, $window, $state, $log, UserModel, $uibModal) {
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

        $scope.openChangeCareUnitDialog = function() {
            var modalInstance = $uibModal.open({
                animation: true,
                templateUrl: 'app/header/careunit/select-care-unit-dialog.html',
                controller: 'SelectCareUnitCtrl',
                size: 'md'
            });

            modalInstance.result.then(function(selectedItem) {
                $log.debug('SelectCareUnit Modal closed with a selection :' + selectedItem);
            }, function() {
                $log.debug('SelectCareUnit Modal cancelled');
            });

        };

    }
);
