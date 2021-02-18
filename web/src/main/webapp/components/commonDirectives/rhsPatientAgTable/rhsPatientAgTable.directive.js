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
  .directive('rhsPatientAgTable',
    function(agCertificateProxy, TableService, UserModel) {
      'use strict';

      return {
        restrict: 'E',
        scope: {
          patient: '=',
          index: '=',
          onLoadIntyg: '&',
          activeUnit: '=',
          getToolTip: '&',
          formatGrader: '&',
          showAgTable: '='
        },
        templateUrl: '/components/commonDirectives/rhsPatientAgTable/rhsPatientAgTable.directive.html',

        link: function($scope) {
          $scope.agCertificates = [];
          $scope.preferenceKey = TableService.patientTableKey;

          var date = new Date();
          $scope.todaysDate = date.getFullYear() + '-' + String(date.getMonth() + 1).padStart(2, '0') + '-' +
              String(date.getDate()).padStart(2, '0');

          $scope.$watch(function() {
            return UserModel.get().preferences[$scope.preferenceKey];
          }, function() {
            $scope.tableColumns = TableService.getSelectedAgTableColumns(true);
          }, true);

          $scope.$watch('showAgTable', function() {
            if ($scope.showAgTable) {
              $scope.showSpinner = true;
              $scope.errorMessageKey = null;
              $scope.agCertificates = [];
              agCertificateProxy.getAgCertificates($scope.patient).then(function(response) {
                $scope.agCertificates = response.certificates;
                $scope.showSpinner = false;
              }, function() {
                $scope.errorMessageKey = 'server.error.loadagcertificates.text';
                $scope.showSpinner = false;
              });
            }
          });

          $scope.isExpiredCertificate = function(enddate){
            return Date.parse(enddate) < Date.parse($scope.todaysDate);
          };
        }
      };
    });
