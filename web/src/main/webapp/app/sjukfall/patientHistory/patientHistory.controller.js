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

angular.module('rehabstodApp').controller('patientHistoryController',
    function($scope, $http, $uibModalInstance, $state, APP_CONFIG, patientHistoryProxy, SjukfallFilterViewState,
        patientHistoryViewState, patient, nyligenAvslutat, UserModel, TableService, UserProxy, messageService) {
      'use strict';

      //Create initial default details tab (cannot be closed)
      patientHistoryViewState.reset();
      patientHistoryViewState.addTab('', 'Sjukfall', true, true);
      patientHistoryViewState.addTab('', 'Läkarutlåtanden', true, false);
      $scope.nyligenAvslutat = nyligenAvslutat;
      $scope.showAgTable = false;

      //expose tabs model to view
      $scope.tabs = patientHistoryViewState.getTabs();
      patientHistoryViewState.selectTab($scope.tabs[0]);

      $scope.$watch(function() {
        return UserModel.get().preferences[TableService.patientTableKey];
      }, function() {
        $scope.tableColumns = TableService.getSelectedPatientTableColumns(nyligenAvslutat);
      }, true);

      $scope.errorMessageKey = '';
      $scope.patientHistoryViewState = patientHistoryViewState;
      $scope.patient = patient;
      $scope.showPatientId = SjukfallFilterViewState.get().showPatientId;
      $scope.showSpinner = true;
      //Constant needed in template
      $scope.radius = 30;

      $scope.loadIntyg = function(intyg) {
        //Either select or create new tab if not already opened..
        var intygsId = intyg.certificateId ? intyg.certificateId : intyg.intygsId;
        var title = intyg.start ? intyg.start : intyg.signingTimeStamp;
        var existingTab = patientHistoryViewState.getTabById(intygsId);
        if (existingTab) {
          patientHistoryViewState.selectTab(existingTab);
        } else {
          patientHistoryViewState.addTab(intygsId, title, false, false, $scope.accessToken);
        }

      };

      $scope.onSelectSjukfall = function(timelineItem) {
        patientHistoryViewState.selectTimelineItem(timelineItem);
      };

      $scope.close = function() {
        wcLogout();
        $uibModalInstance.close();
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

      $scope.getToolTip = function(diagnos) {
        var desc = angular.isString(diagnos.beskrivning) ? diagnos.beskrivning :
            messageService.getProperty('label.table.diagnosbeskrivning.okand', {'kod': diagnos.kod});
        return '<b>' + diagnos.kod + '</b><br>' + desc;
      };

      $scope.updateShowAgTable = function(isChecked) {
        $scope.showAgTable = isChecked;
      };

      function updatePatientSjukfall(patient) {
        //Start by requesting data

        patientHistoryProxy.get(patient).then(function(sjukfallResponse) {
          $scope.showSpinner = false;
          patientHistoryViewState.setTimelineItems(sjukfallResponse.sjukfallList);
          patientHistoryViewState.setSjfMetaData(sjukfallResponse.sjfMetaData);
          patientHistoryViewState.setKompletteringInfoError(sjukfallResponse.kompletteringInfoError);

          $scope.timeline = patientHistoryViewState.getTimelineItems();
        }, function() {
          $scope.showSpinner = false;
          $scope.errorMessageKey = 'server.error.loadpatienthistory.text';
        });
      }

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

      $scope.$on('patientHistory.update', function() {
        updatePatientSjukfall(patient);
      });

      updatePatientSjukfall(patient);

      function fetchAccessToken() {
        UserProxy.fetchAccessToken().then(function(token) {
          $scope.accessToken = token;
        }, function() {
          $scope.accessToken = '';
        });

      }

      fetchAccessToken();

      function wcLogout() {

        var logoutUrl = APP_CONFIG.webcertViewIntygLogoutUrl;

        var form = document.createElement('form');

        form.method = 'post';
        form.action = logoutUrl;
        form.target = 'wc_logout';

        // To be sent, the form needs to be attached to the main document.
        form.style.display = 'none';
        document.body.appendChild(form);

        form.submit();

        // Once the form is sent, remove it.
        document.body.removeChild(form);
      }
    });
