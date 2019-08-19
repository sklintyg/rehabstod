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

angular.module('rehabstodApp')
.controller('pdlConsentDialogCtrl', function($scope, $uibModalInstance, $state, UserProxy, UserModel, msgConfig) {
  'use strict';

  $scope.msgConfig = msgConfig;
  $scope.user = UserModel;

  $scope.approveChecked = false;

  $scope.giveConsent = function() {

    if (!$scope.approveChecked) {
      return;
    }

    UserProxy.givePdlConsent($scope.user.hsaId).then(function(updatedUserModel) {
      UserModel.set(updatedUserModel);
      $uibModalInstance.close(updatedUserModel);
      $state.go('app.sjukfall.result');
    }, function() {

    });
  };

  $scope.check = function() {
    $scope.approveChecked = !$scope.approveChecked;
  };

  $scope.closeModal = function() {
    $uibModalInstance.close();
  };
});
