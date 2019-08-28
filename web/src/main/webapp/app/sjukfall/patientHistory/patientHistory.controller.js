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

angular.module('rehabstodApp').controller('patientHistoryController',
    function($scope, $http, $uibModalInstance, $state, APP_CONFIG, patientHistoryProxy, SjukfallFilterViewState,
        patientHistoryViewState, patient, UserModel, TableService, UserProxy) {
      'use strict';

      //Create initial default details tab (cannot be closed)
      patientHistoryViewState.reset();
      patientHistoryViewState.addTab('', 'Sjukfall', true, true);

      //expose tabs model to view
      $scope.tabs = patientHistoryViewState.getTabs();

      var allColumns = TableService.getAllPatientTableColumns();

      $scope.$watch(function() {
        return UserModel.get().preferences[TableService.patientTableKey];
      }, function() {
        $scope.tableColumns = TableService.getSelectedColumns(allColumns, TableService.patientTableKey, true);
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
        var existingTab = patientHistoryViewState.getTabById(intyg.intygsId);
        if (existingTab) {
          patientHistoryViewState.selectTab(existingTab);
        } else {
          patientHistoryViewState.addTab(intyg.intygsId, intyg.start, false, false, $scope.accessToken);
        }

      };

      $scope.onSelectSjukfall = function(timelineItem) {
        patientHistoryViewState.selectTimelineItem(timelineItem);
      };

      $scope.close = function() {
        wcLogout();
        $uibModalInstance.close();
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
