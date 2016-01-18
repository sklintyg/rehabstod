angular.module('privatlakareApp').factory('OmradeProxy',
        function($http, $log, $q,
               networkConfig) {
            'use strict';

            /*
             * Get region info for a postnummer
             */
            function _getOmradeList(postnummer) {
                $log.debug('getOmradeList');

                var promise = $q.defer();

                if(typeof postnummer !== 'string') {
                    $log.debug('invalid parameter. postnummer must be a string of length 5 or 6.');
                    promise.reject(null);
                } else {

                    // Clean input
                    postnummer = postnummer.replaceAll(' ', '');
                    postnummer = postnummer.trim();
                    if(postnummer.length !== 5) {
                        $log.debug('invalid parameter. postnummer too short or too long. must be length 5 after trimming.');
                        promise.reject(null);
                    } else {
                        var restPath = '/api/registration/omrade/' + postnummer;
                        $log.debug('REST call: getOmradeList - ' + restPath);
                        $http.get(restPath, { timeout: networkConfig.regionTimeout }).success(function(data) {
                            $log.debug('registration/omrade - got data:');
                            $log.debug(data);

                            if(typeof data !== 'undefined' && typeof data.omradeList !== 'undefined') {
                                promise.resolve(data.omradeList);
                            } else {
                                $log.debug('JSON response syntax error. omradeList property required. Rejected.');
                                promise.reject(null);
                            }

                        }).error(function(data, status) {
                            $log.error('error ' + status);
                            // Let calling code handle the error of no data response
                            promise.reject(data);
                        });
                    }
                }

                return promise.promise;
            }

            // Return public API for the service
            return {
                getOmradeList: _getOmradeList
            };
        });
