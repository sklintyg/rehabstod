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

angular.module('rehabstodApp').factory('lakarutlatandenProxy',
    function($http, $log, $q, ObjectHelper, networkConfig) {
      'use strict';

      /*
       * Get history for the specified patient
       */
      function _getLakarutlatandenForPatient(patient, vardenhet) {

        var promise = $q.defer();

        var query = {
          personId: patient.id,
          unit: vardenhet
        };

        var restPath = '/api/certificate/lu/person';

        var config = {
          timeout: networkConfig.defaultTimeout
        };

        $log.debug('Requesting lakarutlatanden for patient');

        $http.post(restPath, query, config).then(function(response) {
          if (!ObjectHelper.isDefined(response.data)) {
            promise.reject({
              errorCode: response.data,
              message: 'invalid data'
            });
          } else {
            promise.resolve(response.data);
          }
        }, function(response) {
          $log.error('error ' + response.status);
          // Let calling code handle the error of no data response
          if (response === null) {
            promise.reject({
              errorCode: response,
              message: 'no response'
            });
          } else {
            promise.reject(response.data);
          }
        });

        return promise.promise;
      }


      /*
       * Get lakarutlatanden for unit
       */
      function _getLakarutlatandenForUnit(query) {

        var promise = $q.defer();

        var restPath = '/api/certificate/lu/unit';

        var config = {
          timeout: networkConfig.defaultTimeout
        };

        $log.debug('Requesting lakarutlatanden for unit');

        $http.post(restPath, query, config).then(function(response) {
          if (!ObjectHelper.isDefined(response.data)) {
            promise.reject({
              errorCode: response.data,
              message: 'invalid data'
            });
          } else {
            promise.resolve(response.data);
          }
        }, function(response) {
          $log.error('error ' + response.status);
          // Let calling code handle the error of no data response
          if (response === null) {
            promise.reject({
              errorCode: response,
              message: 'no response'
            });
          } else {
            promise.reject(response.data);
          }
        });

        return promise.promise;
      }

      /*
       * Get signing doctors for unit
       */
      function _getDoctorsForUnit() {

        var promise = $q.defer();

        var restPath = '/api/certificate/lu/doctors';

        var config = {
          timeout: networkConfig.defaultTimeout
        };

        $log.debug('Requesting signing doctors for unit');

        $http.get(restPath, config).then(function(response) {
          if (!ObjectHelper.isDefined(response.data)) {
            promise.reject({
              errorCode: response.data,
              message: 'invalid data'
            });
          } else {
            promise.resolve(response.data);
          }
        }, function(response) {
          $log.error('error ' + response.status);
          // Let calling code handle the error of no data response
          if (response === null) {
            promise.reject({
              errorCode: response,
              message: 'no response'
            });
          } else {
            promise.reject(response.data);
          }
        });

        return promise.promise;
      }

      // Return public API for the service
      return {
        getLakarutlatandenForPatient: _getLakarutlatandenForPatient,
        getLakarutlatandenForUnit: _getLakarutlatandenForUnit,
        getDoctorsForUnit: _getDoctorsForUnit
      };
    });
