angular.module('privatlakareApp').factory('RegisterProxy',
        function($http, $log, $q,
            ObjectHelper, RegisterModel, networkConfig) {
            'use strict';

            var timeout = networkConfig.registerTimeout;

            /*
             * Get the logged in privatlakare
             */
            function _getPrivatlakare() {

                var promise = $q.defer();

                var restPath = '/api/registration';
                $http.get(restPath, {timeout: timeout}).success(function(data) {
                    $log.debug('registration - got data:');
                    $log.debug(data);
                    if(!ObjectHelper.isDefined(data)) {
                        promise.reject({ errorCode: data, message: 'invalid data'});
                    } else {
                        data = RegisterModel.convertToViewModel(data);
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

            /*
             * Register a privatlakare
             */
            function _savePrivatlakare(registerModel) {

                var promise = $q.defer();

                // Create flat dto from model to send to backend
                var dto = RegisterModel.convertToDTO(registerModel);
                $log.debug('savePrivatlakare dto:');
                $log.debug(dto);

                if(dto === null) {
                    $log.debug('Invalid dto. aborting save.');
                    promise.reject({message: 'Invalid state'});
                } else {
                    // POST
                    var restPath = '/api/registration/save';
                    $http.post(restPath, dto, {timeout: timeout}).success(function(data) {
                        $log.debug('registration/save - got data:');
                        $log.debug(data);
                        promise.resolve(data);
                    }).error(function(data, status) {
                        $log.error('error ' + status);
                        $log.debug('dto:');
                        $log.debug(dto);
                        // Let calling code handle the error of no data response
                        if(data === null) {
                            promise.reject({errorCode: data, message: 'no response'});
                        } else {
                            promise.reject(data);
                        }                    });
                }

                return promise.promise;
            }

            /*
             * Register a privatlakare
             */
            function _registerPrivatlakare(registerModel, godkantMedgivandeVersion) {

                var promise = $q.defer();

                // Create flat dto from model to send to backend
                var dto = RegisterModel.convertToDTO(registerModel, godkantMedgivandeVersion);
                $log.debug('registerPrivatlakare dto:');
                $log.debug(dto);

                // POST
                var restPath = '/api/registration/create';
                $http.post(restPath, dto, {timeout: timeout}).success(function(data) {
                    $log.debug('registration/create - got data:');
                    $log.debug(data);
                    promise.resolve(data);
                }).error(function(data, status) {
                    $log.error('error ' + status);
                    $log.debug('dto:');
                    $log.debug(dto);
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
                getPrivatlakare: _getPrivatlakare,
                savePrivatlakare: _savePrivatlakare,
                registerPrivatlakare: _registerPrivatlakare
            };
        });
