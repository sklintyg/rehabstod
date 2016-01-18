/**
 * Enable tooltips for other components than wcFields
 */
angular.module('privatlakareApp').directive('ppField',
    [ 'messageService',
        function(messageService) {
            'use strict';

            return {
                require: '^form',
                restrict: 'A',
                transclude: true,
                scope: {

                },
                link: function($scope, $element, $attrs, FormController) {

                    $scope.registerForm = FormController;

                    $scope.controlName = $attrs.ppField;
                    $scope.showHelp = messageService.propertyExists('label.form.' + $scope.controlName + '.help');
                    $scope.isValid = function() {
                        if (!$scope.registerForm[$scope.controlName]) {
                            return true;
                        }
                        return !($scope.registerForm[$scope.controlName].$invalid && $scope.registerForm.$submitted);
                    };
                },
                templateUrl: 'components/commonDirectives/ppField/ppField.directive.html'
            };
        }]);
