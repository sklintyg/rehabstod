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

angular.module('rehabstodApp').factory('SjukfallSummaryProxy',
    function($http, $log, $q,
        ObjectHelper, networkConfig) {
      'use strict';

      /*
       * Get statistic for selected Vardenhet
       */
      function _get() {

        var promise = $q.defer();

        var restPath = '/api/sjukfall/summary';
        //No error keys defined, we handle errors here insted of in the interceptor
        var config = {
          timeout: networkConfig.defaultTimeout
        };
        $http.get(restPath, config).then(function(response) {
          if (!ObjectHelper.isDefined(response.data)) {
            promise.reject({errorCode: response.data, message: 'invalid data'});
          } else {
            promise.resolve(response.data);
          }
        }, function(response) {
          $log.error('error ' + response.status);
          // Let calling code handle the error of no data response
          if (response.data === null) {
            promise.reject({errorCode: response.data, message: 'no response'});
          } else {
            promise.reject(response.data);
          }
        });

        return promise.promise;
      }

      // Return public API for the service
      return {
        get: _get
      };
    });
