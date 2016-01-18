/**
 * Adds validation to confirm values are identical between two fields.
 */
angular.module('privatlakareApp').directive('validateConfirmEmail',
    [
        function() {
            'use strict';

            return {
                restrict: 'A',
                require: ['^form', 'ngModel'],
                scope: {
                    validateConfirmEmail: '='
                },
                link: function(scope, elem, attrs, ctrl){
                    var form = ctrl[0];
                    var model = ctrl[1];
                    scope.epostCtrl = form.epost;
                    scope.epost2Ctrl = model;
                    scope.$watch('[epostCtrl.$viewValue, epost2Ctrl.$viewValue]', function(newVal){
                        if(newVal[0] && newVal[1]){
                            ctrl[1].$setValidity('confirmEmail', newVal[0] === newVal[1]);
                        }
                    });
                }
            };
        }]);