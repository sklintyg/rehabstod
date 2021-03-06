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

angular.module('rehabstodApp').factory('TableService',
    function(UserModel, SjukfallFilterViewState, featureService, _, LakarutlatandeFilterViewState) {
      'use strict';

      var sjukfallTableKey = 'sjukfallTableColumns';
      var patientTableKey = 'patientTableColumns';
      var lakarutlatandenTableKey = 'lakarutlatandenTableColumns';
      var lakarutlatandeUnitTableKey = 'lakarutlatandeUnitTableColumns';

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
            defaultSort: true,
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
            id: 'qa',
            dataColumn: 'highestNbrOfQA',
            classes: 'column-qa'
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

      function _getAllPatientTableColumns(nyligenAvslutat) {
        var columns = [
          {
            id: 'number',
            classes: 'column-number'
          },
          {
            id: 'diagnose',
            classes: 'column-diagnose'
          },
          {
            id: 'startdate',
            classes: 'column-startdate'
          },
          {
            id: 'enddate',
            classes: 'column-enddate'
          },
          {
            id: 'length',
            classes: 'column-length'
          },
          {
            id: 'grade',
            classes: 'column-grade'
          },
          {
            id: 'arenden',
            classes: 'column-arenden'
          },
          {
            id: 'doctor',
            classes: 'column-doctor'
          },
          {
            id: 'occupation',
            classes: 'column-occupation'
          }
        ];

        if(!nyligenAvslutat) {
          columns.push({
            id: 'vardenhet',
            classes: '',
            filter: function(isActive) {
              return isActive;
            }
          });
          columns.push({
            id: 'vardgivare',
            classes: '',
            filter: function(isActive) {
              return isActive;
            }
          });
        }

        if (featureService.hasFeature('SRS')) {
          columns.push({
            id: 'risk',
            classes: 'column-risk'
          });
        }

        return columns;
      }

      function _getAllPatientAndAgTableColumns(nyligenAvslutat) {
        var patientTableColumns = _getAllPatientTableColumns(nyligenAvslutat);
        var agTableColumns = _getAllAgTableColumns();
        patientTableColumns.splice(1, 0, agTableColumns[1]);
        return patientTableColumns;
      }

      function _getAllAgTableColumns() {
        var columns = [
          {
            id: 'number',
            classes: 'column-number'
          },
          {
            id: 'intyg',
            classes: 'column-intyg'
          },
          {
            id: 'diagnose',
            classes: 'column-diagnose'
          },
          {
            id: 'startdate',
            classes: 'column-startdate'
          },
          {
            id: 'enddate',
            classes: 'column-enddate'
          },
          {
            id: 'length',
            classes: 'column-length'
          },
          {
            id: 'grade',
            classes: 'column-grade'
          },
          {
            id: 'doctor',
            classes: 'column-doctor'
          },
          {
            id: 'occupation',
            classes: 'column-occupation'
          },
          {
            id: 'vardenhet',
            classes: ''
          },
          {
            id: 'vardgivare',
            classes: ''
          }
        ];
        return columns;
      }

      function _getAllLakarutlatandenTableColumns() {
        var columns = [
          {
            id: 'number',
            classes: 'column-number',
            hideHelp: true,
            disableSort: true
          },
          {
            id: 'type',
            classes: 'column-type',
            dataColumn: 'certificateType'
          },
          {
            id: 'diagnos',
            classes: 'column-diagnos',
            dataColumn: 'diagnosis.intygsVarde'
          },
          {
            id: 'signeringsdatum',
            classes: 'column-signeringsdatum',
            defaultSort: true,
            dataColumn: 'signingTimeStamp'
          },
          {
            id: 'arenden',
            classes: 'column-arenden',
            dataColumn: 'highestNbrOfArenden'
          },
          {
            id: 'doctor',
            classes: 'column-doctor',
            dataColumn: 'doctor.namn'
          },
          {
            id: 'vardenhet',
            classes: '',
            dataColumn: 'careUnitName'
          },
          {
            id: 'vardgivare',
            classes: '',
            dataColumn: 'careProviderName'
          }
          ];
        return columns;
      }

      function _getSelectedColumns(allColumns, preferenceKey, onlyPreferences, isLU) {
        var selectedColumns = UserModel.get().preferences[preferenceKey];
        var allSelected = !selectedColumns;
        var columns;

        if (allSelected) {
          columns = allColumns;
        } else {
          var columnsSelected = selectedColumns ? selectedColumns.split('|') : [];
          var allColumnsMap = _.keyBy(allColumns, 'id');

          columns = columnsSelected
            .map(function(column) {
              var splitString = column.split(':');

              if (splitString[1] === '0') {
                return null;
              }

              return splitString[0];
            })
            .filter(function(column) {
              return !!allColumnsMap[column];
            })
            .map(function(column) {
              return allColumnsMap[column];
            });
        }

        if (onlyPreferences) {
          return columns;
        }

        return columns
        .filter(function(column) {
          if (column.id === 'patientId' || column.id === 'patientName'){
            if (isLU && !LakarutlatandeFilterViewState.get().showPatientId){
              return false;
            }
            else if (!SjukfallFilterViewState.get().showPatientId) {
              return false;
            }
          }

          return true;
        });
      }

      function _getSelectedSjukfallColumns(onlyPreferences) {
        return _getSelectedColumns(_getAllSjukfallTableColumns(), sjukfallTableKey, onlyPreferences);
      }

      function _getSelectedLakarutlatandenTableColumns(onlyPreferences) {
        return _getSelectedColumns(_getAllLakarutlatandenTableColumns(), lakarutlatandenTableKey, onlyPreferences);
      }

      function _getSelectedLakarutlatandeUnitColumns(onlyPreferences) {
        return _getSelectedColumns(_getAllLakarutlatandeUnitColumns(), lakarutlatandeUnitTableKey, onlyPreferences, true);
      }

      function _getAllLakarutlatandeUnitColumns() {
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
          id: 'certType',
          dataColumn: 'certificateType',
          classes: 'column-certtype'
        },
        {
          id: 'dxs',
          dataColumn: 'diagnosis.intygsVarde',
          classes: 'column-diagnos'
        },
        {
          id: 'signdate',
          classes: 'column-signeringsdatum',
          defaultSort: true,
          dataColumn: 'signingTimeStamp'
        },
        {
          id: 'qas',
          classes: 'column-arenden',
          dataColumn: 'highestNbrOfArenden'
        }
      ];

        if (UserModel.get().urval !== 'ISSUED_BY_ME') {
          columns.push({
            id: 'doctor',
            dataColumn: 'doctor.namn',
            classes: ''
          });
        }

        return columns;
      }

      function _getSelectedAgTableColumns(onlyPreferences) {
        return _getSelectedColumns(_getAllAgTableColumns(), patientTableKey, onlyPreferences);
      }

      function _getSelectedPatientTableColumns(nyligenAvslutat){
          return _getSelectedColumns(_getAllPatientTableColumns(nyligenAvslutat), patientTableKey, true);
      }

      return {
        getSelectedColumns: _getSelectedColumns,
        getSelectedSjukfallColumns: _getSelectedSjukfallColumns,
        getSelectedLakarutlatandenTableColumns: _getSelectedLakarutlatandenTableColumns,
        getSelectedAgTableColumns: _getSelectedAgTableColumns,
        getSelectedPatientTableColumns: _getSelectedPatientTableColumns,
        getAllSjukfallTableColumns: _getAllSjukfallTableColumns,
        getAllLakarutlatandenTableColumns: _getAllLakarutlatandenTableColumns,
        getAllPatientAndAgTableColumns: _getAllPatientAndAgTableColumns,
        sjukfallTableKey: sjukfallTableKey,
        patientTableKey: patientTableKey,
        lakarutlatandenTableKey: lakarutlatandenTableKey,
        lakarutlatandeUnitTableKey: lakarutlatandeUnitTableKey,
        getSelectedLakarutlatandeUnitColumns: _getSelectedLakarutlatandeUnitColumns,
        getAllLakarutlatandeUnitColumns: _getAllLakarutlatandeUnitColumns
      };
    });
