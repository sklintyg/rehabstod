/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
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
angular.module('rehabstodApp').directive('rhsDayInRehabChain', ['moment', function(moment) {
  'use strict';

  return {
    restrict: 'E',
    scope: {
      items: '='
    },
    templateUrl: '/components/commonDirectives/rhsDayInRehabChain/rhsDayInRehabChain.directive.html',
    link: function($scope) {
      $scope.shouldDisplay = function() {
        return $scope.items && $scope.items.length > 0 && $scope.items[0].isActive;
      };

      $scope.getDagRehabKedja = function() {
        if ($scope.shouldDisplay()) {
          var started = moment($scope.items[0].sjukfall.start);
          var duration = moment.duration(moment().diff(started));
          var years = duration.years();
          var durationMinusWholeYears = duration.subtract(years, 'y');
          var days = Math.floor(durationMinusWholeYears.asDays());

          return ((years > 0) ? years + ' år ' : '') + days + ' dagar';

        }

      };

    }
  };
}]);
