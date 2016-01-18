angular.module('privatlakareApp').directive('message',
        function($log, $rootScope, $sce, $compile,
            messageService) {
            'use strict';

            return {
                restrict: 'EA',
                scope: {
                    'key': '@',
                    'param': '=',
                    'params': '='
                },
                replace: true,
                template: '<span ng-bind-html="resultValue"></span>',
                link: function(scope, element, attr) {
                    var result;
                    // observe changes to interpolated attribute
                    attr.$observe('key', function(interpolatedKey) {
                        var normalizedKey = angular.lowercase(interpolatedKey);
                        var useLanguage;
                        if (typeof attr.lang !== 'undefined') {
                            useLanguage = attr.lang;
                        } else {
                            useLanguage = $rootScope.lang;
                        }

                        result = messageService.getProperty(normalizedKey, null, attr.fallback, useLanguage,
                            (typeof attr.fallbackDefaultLang !== 'undefined'));

                        if (typeof scope.param !== 'undefined') {
                            $log.debug(scope.param);
                            result = result.replace('%0', scope.param);
                        } else {
                            if (typeof scope.params !== 'undefined') {
                                var myparams = scope.params;
                                for (var i = 0; i < myparams.length; i++) {
                                    result = result.replace('%' + i, myparams[i]);
                                }
                            }
                        }

                        // now get the value to display..
                        scope.resultValue = $compile($sce.trustAsHtml(result))(scope)[0];
                    });
                }
            };
        });
