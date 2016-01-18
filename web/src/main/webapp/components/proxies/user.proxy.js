angular.module('privatlakareApp').factory('UserProxy',
        function($http, $log, $q,
               networkConfig) {
            'use strict';

            /*
             * Get user data for logged in user
             */
            function _getUser() {
                $log.debug('getUser');

                var promise = $q.defer();

                var restPath = '/api/user';
                $log.debug('REST call: getUser ' + restPath);
                $http.get(restPath, { timeout: networkConfig.defaultTimeout }).success(function(data) {
                    $log.debug(restPath + ' - got data:');
                    $log.debug(data);

                    if(typeof data.user !== 'undefined') {
                        promise.resolve(data.user);
                    } else {
                        $log.debug('JSON response syntax error. user property required. Rejected.');
                        promise.reject(null);
                    }
                }).error(function(data, status) {
                    $log.error('error ' + status);
                    // Let calling code handle the error of no data response
                    promise.reject(data);
                });

                return promise.promise;
            }

            // Return public API for the service
            return {
                getUser: _getUser
            };
        });
