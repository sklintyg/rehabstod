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
angular.module('rehabstodApp').directive('rhsChartLegend',
      function($rootScope) {
        'use strict';

        return {
          restrict: 'E',
          scope: {
            data: '=',
            config: '='
          },
          templateUrl: '/components/commonDirectives/rhsChartLegend/rhsChartLegend.directive.html',
          link: function(scope) {
            scope.getLegendText = function(item) {
              return scope.config.legend.labelFormatter.apply(item);
            };

            scope.getLegendColorStyle = function(item) {
              var index = scope.data.indexOf(item);
              return scope.config.colors[index];
            };

            scope.hidden = false;

            var unregisterFn = $rootScope.$on('settings.closed', function() {
              scope.hidden = false;
            });

            var unregisterFn2 = $rootScope.$on('settings.open', function() {
              scope.hidden = true;
            });
            //rootscope on event listeners aren't unregistered automatically when 'this' directives
            //scope is destroyed, so let's take care of that.
            scope.$on('$destroy', unregisterFn);
            scope.$on('$destroy', unregisterFn2);

          }
        };
      });
