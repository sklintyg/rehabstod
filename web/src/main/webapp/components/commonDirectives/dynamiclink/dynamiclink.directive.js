/*
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

angular.module('rehabstodcommon.dynamiclink').directive(
        'dynamiclink',
        function($log, $rootScope, $sce, $compile, dynamicLinkService) {
            'use strict';

            return {
                restrict: 'EA',
                scope: {
                    'key': '@',
                    'linkclass': '@'
                },
                transclude: true,
                template: '<a href="{{ url }}" class="external-link {{ linkclass }}" ng-attr-target="{{ target || undefined}}" '
                        + 'ng-attr-title="{{ tooltip || undefined }}"><span ng-bind-html="text"></span><ng-transclude></ng-transclude> '
                        + '<i ng-if="target" class="glyphicon glyphicon-new-window"></i></a>',

                link: function(scope) {
                    scope.$watch(function() {
                        return dynamicLinkService.getLink(scope.key);
                    }, function(value) {
                        if (angular.isDefined(value)) {
                            scope.url = value.url;
                            scope.text = $sce.trustAsHtml(value.text);
                            scope.tooltip = value.tooltip;
                            scope.target = value.target;
                        } else {
                            scope.url = '#';
                            scope.text = $sce.trustAsHtml('WARNING: could not resolve dynamic link: ' + scope.key);
                            scope.tooltip = null;
                            scope.target = null;
                        }
                    });
                }
            };
        });
