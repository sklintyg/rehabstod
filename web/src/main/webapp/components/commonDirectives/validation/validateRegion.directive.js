/**
 * Adds validation to postnummer region to field.
 */
angular.module('privatlakareApp').directive('validateRegion',
    function(PostnummerHelper, ObjectHelper) {
        'use strict';

        return {
            restrict: 'A',
            require: 'ngModel',
            scope: {
                regionModel: '='
            },
            link: function(scope, elem, attrs, ctrl){

                // Validate kommun and lan
                scope.$watch('regionModel', function(newVal) {
                    if(!ObjectHelper.isDefined(newVal) || !ObjectHelper.isDefined(newVal.kommun) || !ObjectHelper.isDefined(newVal.lan)) {
                        ctrl.$setValidity('region', false);
                    } else {
                        ctrl.$setValidity('region', true);
                    }
                }, true);

                // Validate postnummer format
                var validator = function(value){
                    ctrl.$setValidity('format', PostnummerHelper.isValidPostnummer(value));
                    return value;
                };

                ctrl.$parsers.unshift(validator);
                ctrl.$formatters.unshift(validator);
            }
        };
    });