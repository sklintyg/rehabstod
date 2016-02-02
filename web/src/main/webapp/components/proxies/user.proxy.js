angular.module('rehabstodApp').factory('UserProxy',
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
            $http.get(restPath, {timeout: networkConfig.defaultTimeout}).success(function(data) {
                $log.debug(restPath + ' - success');

                if (typeof data !== 'undefined') {
                    promise.resolve(data);
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

        /*
         * Get user data for logged in user
         */
        function _changeSelectedUnit(newUnitId) {
            $log.debug('_changeSelectedUnit');

            var promise = $q.defer();

            var restPath = '/api/user/andraenhet';
            var dto = {
                id: newUnitId
            };

            $log.debug('REST call: _changeSelectedUnit ' + restPath);
            $http.post(restPath, dto, {timeout: networkConfig.defaultTimeout}).success(function(data) {
                $log.debug(restPath + ' - success');

                if (typeof data !== 'undefined') {
                    promise.resolve(data);
                } else {
                    $log.debug('JSON response syntax error. Rejected.');
                    promise.reject(null);
                }
            }).error(function(data, status) {
                $log.error('error ' + status);
                // Let calling code handle the error of no data response
                promise.reject(data);
            });

            return promise.promise;
        }

        /*
         * Get user data for logged in user
         */
        function _changeUrval(newUrval) {
            $log.debug('_changeUrval');

            var promise = $q.defer();

            var restPath = '/api/user/urval';
            var dto = {
                urval: newUrval
            };

            $log.debug('REST call: _changeUrval ' + restPath);
            $http.post(restPath, dto, {timeout: networkConfig.defaultTimeout}).success(function(data) {
                $log.debug(restPath + ' - success');

                if (typeof data !== 'undefined') {
                    promise.resolve(data);
                } else {
                    $log.debug('JSON response syntax error. Rejected.');
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
            getUser: _getUser,
            changeSelectedUnit: _changeSelectedUnit,
            changeUrval: _changeUrval
        };
    });
