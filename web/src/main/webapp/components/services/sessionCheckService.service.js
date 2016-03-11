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

angular.module('rehabstodApp').factory('sessionCheckService',
    ['$http', '$log', '$interval', '$window', function($http, $log, $interval, $window) {
        'use strict';

        var pollPromise;
        var extendSessionPromise;

        //one every minute
        var msPollingInterval = 60 * 1000;

        //(Max) how often should session prolong request be sent
        var msMinExtendSessionRequestInterval = 60 * 1000;

        /*
         * stop regular polling
         */
        function _stopPolling() {
            if (pollPromise) {
                $interval.cancel(pollPromise);
            }
        }

        /*
         * get session status from server
         */
        function _getSessionInfo() {
            $log.debug('_getSessionInfo requesting session info =>');
            $http.get('/api/session-auth-check/ping').success(function(data) {
                $log.debug('<= _getSessionInfo success');
                if (data) {
                    $log.debug('session status  = ' + JSON.stringify(data));
                    if (data.authenticated === false) {
                        $log.debug('No longer authenticated - redirecting to loggedout');
                        _stopPolling();
                        $window.location.href = '/error.jsp?reason=inactivity-timeout';
                    }
                } else {
                    $log.debug('_getSessionInfo returned unexpected data:' + data);
                }

                //Schedule new polling
                _stopPolling();
                pollPromise = $interval(_getSessionInfo, msPollingInterval);
            }).error(function(data, status) {
                $log.error('_getSessionInfo error ' + status);

                _stopPolling();
                //Schedule a new check
                pollPromise = $interval(_getSessionInfo, msPollingInterval);
            });
        }

        /*
         * start regular polling of stats from server
         */
        function _startPolling() {
            $log.debug('sessionCheckService -> Start polling');
            _getSessionInfo();
        }


        /*
         * Extending session by making a request to server
         */
        function _executeExtendSessionRequest() {
            $log.debug('_executeExtendSessionRequest sending request now =>');
            $http.get('/api/session-auth-check/extend').success(function(data) {
                $log.debug('<= _executeExtendSessionRequest success:' + data);
            }).error(function(data, status) {
                $log.error('<= _executeExtendSessionRequest failed: ' + status);
            }).finally(function() { // jshint ignore:line
                //clear interval no matter the outcome of the request
                if (extendSessionPromise) {
                    $interval.cancel(extendSessionPromise);
                    extendSessionPromise = undefined;
                }
            });
        }


        function _registerUserAction() {
            if (!extendSessionPromise) {
                extendSessionPromise = $interval(_executeExtendSessionRequest, msMinExtendSessionRequestInterval);
                $log.debug('Extend session request scheduled.');
            }
        }


        // Return public API for the service
        return {
            startPolling: _startPolling,
            stopPolling: _stopPolling,
            registerUserAction: _registerUserAction
        };
    }]);
