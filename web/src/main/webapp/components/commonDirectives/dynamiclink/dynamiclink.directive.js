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

angular.module('rehabstodApp').directive('dynamiclink',
        function($log, $rootScope, $sce, $compile,
            dynamicLinkService) {
            'use strict';

            return {
                restrict: 'EA',
                scope: {
                    'key': '@'
                },
                template: '<a href="{{ url }}" ng-attr-target="{{ target || undefined}}" ' +
                    'ng-attr-title="{{ tooltip || undefined }}" ng-bind-html="text"></a>',
                link: function(scope, element, attr) {
                    var dynamicLink;

                    attr.$observe('key', function(linkKey) {
                        dynamicLink = dynamicLinkService.getLink(linkKey);
                        scope.url = dynamicLink.url;
                        scope.text = dynamicLink.text;
                        scope.tooltip = dynamicLink.tooltip;
                        scope.target = dynamicLink.target;
                    });
                }
            };
        });
