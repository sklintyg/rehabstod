angular.module('privatlakareApp')
    .controller('Step1Ctrl', function($scope, $state, $window,
        RegisterNavigationService, WindowUnload, UserModel) {
        'use strict';

        if(UserModel.isRegistered()) {
            $state.go('app.register.complete');
            return;
        }

        // function to submit the form after all validation has occurred
        $scope.submitForm = function() {
            $state.go('app.register.step2');
        };

        $scope.$on('$stateChangeStart',
            function(event, toState, toParams, fromState) {
                if (!RegisterNavigationService.navigationAllowed(toState, fromState, $scope.registerForm.$valid)) {
                    event.preventDefault();
                    // transitionTo() promise will be rejected with
                    // a 'transition prevented' error
                }
            });

        // Add browser dialog to ask if user wants to save before leaving if he closes the window on an edited form.
        WindowUnload.bindUnload($scope);
    });
