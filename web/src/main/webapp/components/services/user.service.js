/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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

angular.module('rehabstodApp').factory('UserService',
    function(UserModel, SjukfallFilterViewState, featureService) {
      'use strict';

      function _getAllSjukfallTableColumns() {
        var columns = [
          {
            id: 'number',
            hideHelp: true,
            disableSort: true,
            classes: 'column-number'
          },
          {
            id: 'patientId',
            dataColumn: 'patient.id',
            hideHelp: true,
            classes: 'column-patient-id text-nowrap'
          },
          {
            id: 'patientAge',
            dataColumn: 'patient.alder',
            hideHelp: true,
            classes: 'column-patient-age text-nowrap'
          },
          {
            id: 'patientName',
            dataColumn: 'patient.namn',
            hideHelp: true,
            classes: 'column-patient-name text-nowrap'
          },
          {
            id: 'gender',
            dataColumn: 'patient.konShow',
            hideHelp: true,
            classes: 'column-patient-kon text-nowrap'
          },
          {
            id: 'dxs',
            dataColumn: 'diagnos.intygsVarde',
            classes: 'column-diagnos'
          },
          {
            id: 'startDate',
            dataColumn: 'start',
            classes: 'column-start'
          },
          {
            id: 'endDate',
            dataColumn: 'slut',
            classes: 'column-slut'
          },
          {
            id: 'days',
            dataColumn: 'dagar',
            classes: 'column-dagar'
          },
          {
            id: 'antal',
            dataColumn: 'intyg',
            classes: 'column-antal'
          },
          {
            id: 'degree',
            dataColumn: 'aktivGrad',
            classes: 'column-grad'
          },
          {
            id: 'kompletteringar',
            dataColumn: 'obesvaradeKompl',
            classes: 'column-obesvaradeKompl'
          }
        ];

        if (featureService.hasFeature('SRS')) {
          columns.push({
            id: 'srs',
            dataColumn: 'riskSignal.riskKategori',
            classes: ''
          });
        }

        if (UserModel.get().urval !== 'ISSUED_BY_ME') {
          columns.push({
            id: 'doctor',
            dataColumn: 'lakare.namn',
            classes: ''
          });
        }

        return columns;
      }

      function _getSelectedColumns(allColumns, preferenceKey, onlyPreferences) {
        var user = UserModel.get();
        var columns = user.preferences[preferenceKey];
        var allSelected = !columns;
        var columnsSelected = columns ? columns.split('|') : [];

        return allColumns.filter(function (column) {
          if (!allSelected && columnsSelected.indexOf(column.id) === -1) {
            return false;
          }

          if (!onlyPreferences && (column.id === 'patientId' || column.id === 'patientName') && !SjukfallFilterViewState.get().showPatientId) {
            return false;
          }

          return true;
        });
      }

      return {
        getSelectedColumns: _getSelectedColumns,
        getAllSjukfallTableColumns: _getAllSjukfallTableColumns,
        sjukfallTableKey: 'sjukfallTableColumns'
      };
    });
