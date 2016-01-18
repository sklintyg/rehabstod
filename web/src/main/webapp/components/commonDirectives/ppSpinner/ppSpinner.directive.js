angular.module('privatlakareApp').directive('ppSpinner',
    ['$timeout','$window', function($timeout, $window) {
        'use strict';

        return {
            restrict: 'A',
            transclude: true,
            replace: true,
            scope: {
                label: '@',
                showSpinner: '=',
                isHeader: '='
            },
            templateUrl: 'components/commonDirectives/ppSpinner/ppSpinner.directive.html',
            link: {
                pre : function (/*scope, element*/){
                    $window.rendered = false;
                },
                post : function (scope/*, element*/){
                    scope.$watch('showSpinner', function(newVal/*, oldVal*/){
                        if (!newVal && !scope.isHeader) {
                            $timeout(function(){
                                $window.rendered = true;
                            });
                        } else if(newVal){
                            $window.rendered = false;
                        }
                    });
                }
            }
        };
    }]);
