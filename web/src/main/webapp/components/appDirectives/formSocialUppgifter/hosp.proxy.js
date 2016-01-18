angular.module('privatlakareApp').factory('HospProxy',
        function($http, $log, $q,
            networkConfig) {
            'use strict';

            /*
             * Get hosp info about the logged in privatlakare
             */
            function _getHospInformation() {

                var promise = $q.defer();

                /*
                *
                * In case we need a global timeout config in the future it ca nbe added as a httpinterceptor
                *
                *  angular.module('yourapp')
                 .factory('timeoutHttpIntercept', function ($rootScope, $q) {
                 return {
                 'request': function(config) {
                 config.timeout = 10000;
                 return config;
                 }
                 };
                 });
                 And then in .config inject $httpProvider and do this:

                 $httpProvider.interceptors.push('timeoutHttpIntercept');
                *
                * */

                var restPath = '/api/registration/hospInformation';
                $http.get(restPath, {timeout: networkConfig.hospTimeout}).success(function(data) {
                    $log.debug('registration/hospInformation - got data:');
                    $log.debug(data);
                    promise.resolve(data.hospInformation);
                }).error(function(data, status) {
                    $log.error('error ' + status);
                    // Let calling code handle the error of no data response
                    promise.reject(data);
                });

                return promise.promise;
            }

            // Return public API for the service
            return {
                getHospInformation: _getHospInformation
            };
        });
