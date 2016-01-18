/**
 * Form directive to enter grunduppgifter
 */
angular.module('privatlakareApp').directive('formGrundUppgifter',
        function($log,
            FormGrundUppgifterViewState, messageService, ObjectHelper) {
            'use strict';

            return {
                restrict: 'A',
                transclude: true,
                scope: {
                    'user': '=',
                    'registerModel': '='
                },
                controller: function($scope) {

                    $scope.focusBefattning = true;

                    if(!ObjectHelper.isDefined($scope.registerModel) || !ObjectHelper.isDefined($scope.user)) {
                        $log.debug('formGrundUppgifter requires parameters register-model and user');
                    }

                    $scope.message = messageService;
                    $scope.viewState = FormGrundUppgifterViewState;
                },
                templateUrl: 'components/appDirectives/formGrundUppgifter/formGrundUppgifter.directive.html'
            };
        });
