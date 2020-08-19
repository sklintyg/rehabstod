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
  .directive('rhsPatientAgTable',
    function(agCertificateProxy) {
      'use strict';

      return {
        restrict: 'E',
        scope: {
          patient: '=',
          index: '=',
          onLoadIntyg: '&',
          columns: '=',
          activeUnit: '=',
          getToolTip: '&',
          formatGrader: '&',
          showAgTable: '='
        },
        templateUrl: '/components/commonDirectives/rhsPatientAgTable/rhsPatientAgTable.directive.html',

        link: function($scope) {
          $scope.agCertificates = [];

          $scope.$watchCollection('columns', function() {
            $scope.filteredColumns = $scope.columns.filter(function(column ) {
              return column.id !== 'obesvaradekompl' && column.id !== 'risk';
            });
          });

          $scope.$watch('showAgTable', function() {
            if ($scope.showAgTable) {
              $scope.showSpinner = true;
              $scope.errorMessageKey = null;
              $scope.agCertificates = [];
              agCertificateProxy.getAgCertificates($scope.patient).then(function(response) {
                if (!response.qaError) {
                  $scope.agCertificates = response.certificates;
                } else {
                  $scope.errorMessageKey = 'server.error.loadagcertificates.text';
                }
                $scope.showSpinner = false;
              }, function() {
                $scope.errorMessageKey = 'server.error.loadagcertificates.text';
                $scope.showSpinner = false;
              });
            }
          });
        }
      };
    });