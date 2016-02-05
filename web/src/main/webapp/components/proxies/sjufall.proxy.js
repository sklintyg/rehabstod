angular.module('rehabstodApp').factory('SjukfallProxy',
    function($http, $log, $q,
        ObjectHelper, networkConfig) {
        'use strict';

        var timeout = networkConfig.registerTimeout;

        /*
         * Get statistic for selected Vardenhet
         */
        function _get(query) {

            var promise = $q.defer();

            var restPath = '/api/sjukfall';
            $http.post(restPath, query, {timeout: timeout}).success(function(data) {
                $log.debug('sjukfall - got data:');
                $log.debug(data);
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
