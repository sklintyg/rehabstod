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
angular.module('rehabstodApp').directive('rhsPatientHistoryTable',
    function(UserModel, messageService, featureService, patientHistoryViewState) {
      'use strict';

      return {
        restrict: 'E',
        scope: {
          historyItem: '=',
          patient: '=',
          index: '=',
          onSelect: '&',
          onLoadIntyg: '&',
          columns: '='
        },
        templateUrl: '/components/commonDirectives/rhsPatientHistoryTable/rhsPatientHistoryTable.directive.html',
        link: function($scope) {
          $scope.user = UserModel.get();
          $scope.patientHistoryViewState = patientHistoryViewState;

          var patientSjfMetaData = patientHistoryViewState.getSjfMetaData();

          var andraVeInomVgUtanSparr = patientSjfMetaData.kraverInteSamtycke.length;
          var andraVeInomVgMedSparr = patientSjfMetaData.vardenheterInomVGMedSparr.length;
          var andraVgUtanSparr = patientSjfMetaData.kraverSamtycke.length;
          var andraVgMedSparr = patientSjfMetaData.andraVardgivareMedSparr.length;

          $scope.$watchCollection('columns', function() {
            if (!$scope.historyItem.isActive) {
              $scope.filteredColumns = $scope.columns.filter(function(column) {
                return !angular.isFunction(column.filter) || column.filter($scope.historyItem.isActive);
              });
            } else {
              $scope.filteredColumns = $scope.columns;
            }
          });

          $scope.extraDiagnoser = {
            patientSjfMetaData: patientSjfMetaData,
            available: (andraVeInomVgUtanSparr > 0 || andraVeInomVgMedSparr > 0 || andraVgMedSparr > 0 || andraVgUtanSparr > 0) &&
              ($scope.patient.responseFromPu !== 'FOUND_NO_NAME' && $scope.patient.responseFromPu !== 'NOT_FOUND'),
            sekretess: patientSjfMetaData.haveSekretess,
            osparradInfoInomVardgivare: andraVeInomVgUtanSparr > 0,
            sparradInfoInomVardgivare: andraVeInomVgMedSparr > 0,
            osparradInfoAndraVardgivare: andraVgUtanSparr > 0,
            sparradInfoAndraVardgivare: andraVgMedSparr > 0,
            samtyckeFinns: patientHistoryViewState.hasSamtycke()
          };

          $scope.getToolTip = function(diagnos) {
            var desc = angular.isString(diagnos.beskrivning) ? diagnos.beskrivning :
                messageService.getProperty('label.table.diagnosbeskrivning.okand', {'kod': diagnos.kod});
            return '<b>' + diagnos.kod + '</b><br>' + desc;
          };

          $scope.getEffectiveVardenhetUnitName = function() {
            var user = UserModel.get();
            if (user.valdVardenhet) {
              //Is valdvardenhet actually a mottagning?
              if (user.valdVardenhet.parentHsaId) {
                //return parent unit name, since data is always returned for unit level (even if mottagning is selected)
                return UserModel.getUnitNameById(user.valdVardenhet.parentHsaId);
              }
              return user.valdVardenhet.namn;
            }
            return '';
          };

          //Requirements state that only first/last of grader should be returned
          $scope.formatGrader = function(gradArr) {

            switch (gradArr.length) {
            case 0:
              return '';
            case 1:
              return gradArr[0] + '%';
            default:
              return gradArr[0] + '% &#10142; ' + gradArr[gradArr.length - 1] + '%';
            }

          };

          $scope.formatKomplettering = function(obesvaradeKompl) {
            if ($scope.patientHistoryViewState.getKompletteringInfoError()) {
              return '';
            } else if (!angular.isNumber(obesvaradeKompl)) {
              return '<span class="hidden-value">Okänt</span>';
            } else {
              return (obesvaradeKompl > 0) ? 'Obesvarad (' + obesvaradeKompl + ')' : '-';
            }
          };

          $scope.hasFeature = function(feature) {
            return featureService.hasFeature(feature);
          };

        }
      };
    });
