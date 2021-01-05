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
.controller('RhsLakarutlatandeTableCtrl',
    function($scope, $uibModal, LakarutlatandeFilterViewState, LakarutlatandeModel, UserModel, messageService,
        featureService, $document, TableService) {
      'use strict';

      $scope.preferenceKey = TableService.lakarutlatandeUnitTableKey;
      $scope.model = LakarutlatandeModel;
      $scope.user = UserModel.get();
      $scope.displayedCollection = [].concat($scope.model.get());
      $scope.columns = TableService.getAllLakarutlatandeUnitColumns();
      $scope.columnsForTable = [];
      $scope.tableTextKey = 'label.table.column';

      $scope.$watch(function() {
        return LakarutlatandeFilterViewState.get().showPatientId + UserModel.get().preferences[$scope.preferenceKey];
      }, function() {
        $scope.columnsForTable = TableService.getSelectedColumns($scope.columns, $scope.preferenceKey, undefined, true);
        LakarutlatandeModel.updateQuickSearchContent();
      });


      $scope.getToolTip = function(diagnosis) {
        var desc = angular.isString(diagnosis.beskrivning) ? diagnosis.beskrivning :
            messageService.getProperty('label.table.diagnosbeskrivning.okand', {'kod': diagnosis.kod});
        return '<b>' + diagnosis.kod + '</b><br>' + desc;
      };

      $scope.showPatientHistory = function(patientModel, nyligenAvslutat, certificateId, signingTimeStamp) {
        $uibModal.open({
          windowClass: 'patient-history-dialog',
          templateUrl: '/app/sjukfall/patientHistory/patientHistory.dialog.html',
          controller: 'patientHistoryController',
          size: 'lg',
          backdrop: 'static',
          keyboard: false,
          resolve: {
            patient: function() {
              return patientModel;
            },
            nyligenAvslutat: function() {
              return nyligenAvslutat;
            },
            openLU: function() {
              return true;
            },
            certificate: function() {
              return {certificateId:certificateId,signingTimeStamp:signingTimeStamp};
            }
          }
        });
      };

      $scope.showMoreInTable = function() {
        $scope.limit += 50;
      };

      $scope.resetLimit = function() {
        $scope.limit = 100;
      };

      $scope.resetLimit();

      $document.on('scroll', handleScroll);

      $scope.$on('$destroy', function() {
        $document.off('scroll', handleScroll);
      });

      function handleScroll() {
        var scrollLeft = $document.scrollLeft();
        var header = $('#rhs-table-fixed-header');
        var left = 30;

        if (scrollLeft > 0) {
          left -= scrollLeft;
        }

        header.css('left', left + 'px');
      }
    }
)
.directive('rhsLakarutlatandeTable',
    function() {
      'use strict';

      return {
        restrict: 'E',
        scope: {},
        controller: 'RhsLakarutlatandeTableCtrl',
        templateUrl: '/components/appDirectives/sjukfall/rhsLakarutlatandeTable/rhsLakarutlatandeTable.directive.html'
      };
    });
