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

angular.module('rehabstodApp').factory('patientHistoryProxy',
    function($http, $log, $q,
        ObjectHelper, networkConfig) {
        'use strict';

        /*
         * Get history for the specified patient
         */
        function _get(patient) {


            var promise = $q.defer();

            $log.debug('Requesting patient history for patient ' + patient.id);
            //TODO: call real api endpoint
            var restPath = 'patienthistorymock.json';
            //var restPath = '/api/sjukfall/history';

            //No error keys defined, we handle errors here insted of in the interceptor
            var config =  {
                timeout: networkConfig.defaultTimeout
            };
            $http.get(restPath, config).success(function(data) {
                if(!ObjectHelper.isDefined(data)) {
                    promise.reject({ errorCode: data, message: 'invalid data'});
                } else {
                    promise.resolve(data);
                }
            }).error(function(data, status) {
                $log.error('error ' + status);
                // Let calling code handle the error of no data response
                if(data === null) {
                    promise.reject({errorCode: data, message: 'no response'});
                } else {
                    promise.reject(data);
                }
            });

            return promise.promise;
        }

        // Return public API for the service
        return {
            get: _get
        };
    });