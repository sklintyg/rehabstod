angular.module('privatlakareApp').controller('HeaderController',
        function($scope, $window, $state, $log, UserModel, LinkBuilder, $sessionStorage) {
            'use strict';

            //Expose 'now' as a model property for the template to render as todays date
            $scope.today = new Date();
            $scope.userModel = UserModel;
            $scope.user = UserModel.get();
            $scope.statusText = '';

            $scope.$watch('user', function(newVal) {
                $log.debug('header controller - user status updated:');
                $log.debug(newVal.status);
                $scope.statusText = UserModel.getStatusText();
            }, true);

            $scope.$on('$stateChangeSuccess',
                function(event, toState, toParams, fromState/*, $state, fromParams*/) {
                    // sessionStorage.from is defined in boot.controller.js.
                    $scope.exitLink = LinkBuilder.getExitLink(fromState.name, toState.name, UserModel.get().status, $sessionStorage.from);
                });

            /**
             * Private functions
             */

            /**
             * Exposed scope interaction functions
             */

            $scope.logoutLocation = UserModel.getLogoutLocation();
        }
    );
