/**
 * FocusMe directive. Used to set focus to an element via model value
 */
angular.module('privatlakareApp').directive('ppFocusMe',
    function($timeout) {
        'use strict';

        return {
            scope: { trigger: '=ppFocusMe' },
            link: function(scope, element) {
                scope.$watch('trigger', function(value) {
                    if (value === true) {
                        $timeout(function() {
                            element[0].focus();
                            scope.trigger = false;
                        });
                    }
                });
            }
        };
    });
