/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
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
/* globals Highcharts */
angular.module('rehabstodApp').directive('rhsPieChart',
      function($rootScope) {
        'use strict';

        return {
          restrict: 'E',
          scope: {
            data: '=',
            config: '='
          },
          templateUrl: '/components/commonDirectives/rhsPieChart/rhsPieChart.directive.html',
          link: function(scope, element) {

            var chart = Highcharts.chart(element[0], scope.config);

            //Wait for data
            scope.$watchCollection('data', function(newData) {
              if (newData) {
                if (chart.series[0]) {
                  chart.series[0].setData(newData);
                }
                chart.reflow();
                //REHAB-221: IE will include svg elements in tab sequence if focusable=true.
                //We don't want that.
                angular.element(chart.container).find('svg').attr('focusable', false);
              }
            });
            var unregisterFn = $rootScope.$on('settings.closed', function() {
              scope.config.plotOptions.series.animation = false;
              chart = Highcharts.chart(element[0], scope.config);
            });

            var unregisterFn2 = $rootScope.$on('settings.open', function() {
              chart.destroy();
            });
            //rootscope on event listeners aren't unregistered automatically when 'this' directives
            //scope is destroyed, so let's take care of that.
            scope.$on('$destroy', unregisterFn);
            scope.$on('$destroy', unregisterFn2);
          }
        };
      });
