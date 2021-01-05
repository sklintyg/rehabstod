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

angular.module('rehabstodApp')
.controller('NoDataCtrl', function($scope, $rootScope, $uibModal, $state, UserModel, UserProxy) {
  'use strict';

  $scope.user = UserModel.get();

  var unregisterFn = $rootScope.$on('SelectedUnitChanged', function(/*event, value*/) {
    $state.go('app.sjukfall.start');
  });
  //rootscope on event listeners aren't unregistered automatically when 'this' directives
  //scope is destroyed, so let's take care of that.
  $scope.$on('$destroy', unregisterFn);

  $scope.openChangeCareUnitDialog = function() {
    var modalInstance = $uibModal.open({
      animation: true,
      templateUrl: '/app/header/careunit/select-care-unit-dialog.html',
      controller: 'SelectCareUnitCtrl',
      size: 'md',
      windowClass: 'select-care-unit-modal'
    });

    modalInstance.result.then(function(enhet) {

      UserProxy.changeSelectedUnit(enhet.id).then(function(updatedUserModel) {
        UserModel.set(updatedUserModel);
        $state.go('app.sjukfall.start');
      }, function() {
        //Handle errors
      });
    }, function() {

    });

  };

});