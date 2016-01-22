angular.module('rehabstodApp').controller('HeaderController',
    function($scope, $window, $state, $log, UserModel, dialogService) {
        'use strict';

        //Expose 'now' as a model property for the template to render as todays date
        $scope.today = new Date();
        $scope.user = UserModel.get();

        $scope.$watch('user', function() {
            $log.debug('header controller - user status updated');
        }, true);

        $scope.$watch('user.valdVardenhet', function() {
            $log.debug('header controller - user valdvardenhet changed');
            $state.go('app.start');
        }, true);

        /**
         * Private functions
         */

        /**
         * Exposed scope interaction functions
         */

        $scope.logoutLocation = UserModel.getLogoutLocation();

        $scope.openChangeCareUnitDialog = function() {
            dialogService.open({
                templateUrl: 'app/header/careunit/select-care-unit-dialog.html',
                controller: 'SelectCareUnitCtrl'
            }).result.finally(function() { //jshint ignore:line
                $log.debug('select-care-unit-dialog closed');
            });
        };

    }
);
