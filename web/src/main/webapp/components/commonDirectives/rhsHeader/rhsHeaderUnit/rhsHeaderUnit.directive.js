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
angular.module('rehabstodApp').directive('rhsHeaderUnit',
    function() {
      'use strict';

      return {
        restrict: 'E',
        scope: {},
        templateUrl: '/components/commonDirectives/rhsHeader/rhsHeaderUnit/rhsHeaderUnit.directive.html',
        controller: function($scope, $log, UserProxy, UserModel, $uibModal) {
          /**
           * Private functions
           */

          /**
           * Exposed scope interaction functions
           */
          $scope.user = UserModel.get();
          $scope.unitExpanded = false;

          $scope.toggleMenu = function($event) {
            $event.stopPropagation();
            $scope.unitExpanded = !$scope.unitExpanded;
          };

          $scope.getVardgivare = function() {
            var user = UserModel.get();
            if (user.valdVardenhet) {
              return user.valdVardgivare.namn;
            }
            return '';
          };

          $scope.getVardenhet = function() {
            var user = UserModel.get();
            if (user.valdVardenhet) {
              var eName = user.valdVardenhet.namn;
              var mName = '';
              //Is valdvardenhet actually a mottagning?
              if (user.valdVardenhet.parentHsaId) {
                eName = UserModel.getUnitNameById(user.valdVardenhet.parentHsaId);
                mName = user.valdVardenhet.namn;
              }
              return eName + ((mName.length > 0) ? ' - ' + mName : '');
            }
            return '';
          };

          $scope.openChangeCareUnitDialog = function() {
            var modalInstance = $uibModal.open({
              animation: true,
              templateUrl: '/app/header/careunit/select-care-unit-dialog.html',
              controller: 'SelectCareUnitCtrl',
              size: 'md',
              windowClass: 'select-care-unit-modal'
            });

            modalInstance.result.then(function(enhet) {
              $log.debug('SelectCareUnit Modal closed with a selection :' + enhet.id);
              UserProxy.changeSelectedUnit(enhet.id).then(function(updatedUserModel) {
                UserModel.set(updatedUserModel);

                $scope.$emit('SelectedUnitChanged', {enhet: enhet.id});
              }, function() {
                //Handle errors
              });
            }, function() {
              $log.debug('SelectCareUnit Modal cancelled');
            });

          };
        }
      };
    });
