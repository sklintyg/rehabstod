angular.module('privatlakareApp')
    .controller('RegisterCtrl', function($scope, $state, UserModel, RegisterModel, RegisterNavigationService) {
        'use strict';
        $scope.user = UserModel.get();
        $scope.registerModel = RegisterModel.init();
        $scope.step = 1;
        $scope.$on('$stateChangeSuccess',
            function(event, toState) {
                $scope.step = RegisterNavigationService.getStepFromState(toState);
            }
        );
    });
