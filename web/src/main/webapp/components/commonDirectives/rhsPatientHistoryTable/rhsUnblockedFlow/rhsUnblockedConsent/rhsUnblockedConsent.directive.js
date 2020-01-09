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
angular.module('rehabstodApp').directive('rhsUnblockedConsent',
    function($rootScope, patientHistoryViewState, patientHistoryProxy, $uibModal) {
      'use strict';

      return {
        restrict: 'E',
        scope: {
          patient: '=',
          labelError: '@',
          boxState: '='
        },
        templateUrl: '/components/commonDirectives/rhsPatientHistoryTable/rhsUnblockedFlow/rhsUnblockedConsent/rhsUnblockedConsent.directive.html',
        link: function($scope) {
          $scope.vardgivareUtanSamtycke = patientHistoryViewState.getSjfMetaData().kraverSamtycke;

          var vardgivareUtanSamtyckeNames = $scope.vardgivareUtanSamtycke.map(function(item) {
            return item.itemName;
          });

          $scope.vardgivareMedInfo = vardgivareUtanSamtyckeNames.join(', ');

          $scope.consent = {
            confirm: false,
            onlyCurrentUser: 'ONLYCURRENT',
            days: 7,
            error: false
          };

          $scope.$watch('consent.confirm', function(newValue, oldValue) {
                if (oldValue && !newValue) {
                  $scope.consent.error = false;
                }
              }
          );

          $scope.openDialog = function(title, bodyText) {
            $uibModal.open({
              templateUrl: '/components/commonDirectives/rhsSjfModal/rhsSjf.modal.html',
              controller: 'rhsSjfModalController',
              size: 'md',
              resolve: {
                title: function() {
                  return title;
                },
                bodyText: function() {
                  return bodyText;
                }
              }
            }).result.then(function() {
            }, function() {
            });
          };

          $scope.close = function() {
            $scope.boxState.skipStart = false;
          };

          $scope.next = function() {
            patientHistoryProxy.giveConsent({
              patientId: $scope.patient.id,
              onlyCurrentUser: $scope.consent.onlyCurrentUser === 'ONLYCURRENT',
              days: $scope.consent.days
            }).then(function(response) {
              var patientSjfMeta = patientHistoryViewState.getSjfMetaData();
              if (response.responseCode === 'OK') {
                patientSjfMeta.samtyckeFinns = true;
                $scope.consent.error = false;
                $rootScope.$broadcast('rhsUnblockedFlow.next');
              } else {
                patientSjfMeta.samtyckeFinns = false;
                $scope.consent.error = true;
              }
            });
          };
        }
      };
    });
