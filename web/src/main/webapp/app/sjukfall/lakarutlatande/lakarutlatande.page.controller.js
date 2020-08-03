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

angular.module('rehabstodApp')
.controller('LakarutlatandeResultPageCtrl',
    function($scope, $rootScope, $state, LakarutlatandeService, UserModel) {
      'use strict';

      $scope.lakarutlatandeService = LakarutlatandeService;
      $scope.user = UserModel.get();
      $scope.kompletteringInfoError = false;

      // loadLakarUtlatanden();

      var unregisterFn = $rootScope.$on('SelectedUnitChanged', function() {
        // loadLakarUtlatanden();
        // TODO clear data?
      });
      //rootscope on event listeners aren't unregistered automatically when 'this' directives
      //scope is destroyed, so let's take care of that.
      $scope.$on('$destroy', unregisterFn);

      $scope.goBack = function() {
        $state.go('app.sjukfall.start');
      };

      $scope.$watch('lakarutlatandeService.isLoading()', function(val) {
        $scope.showSpinner = val;
      });

      // $scope.$watch('SjukfallViewState.get().kompletteringInfoError', function(val) { //TODO
      //   $scope.kompletteringInfoError = val;
      // });


      $scope.searchFn = function() {
        loadLakarUtlatanden();
      };

      function loadLakarUtlatanden() {
        $scope.showSpinner = true;
        LakarutlatandeService.loadLakarutlatande(true, true);
      }
    });
