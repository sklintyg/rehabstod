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

/**
 * Response intercepter catching all responses coming back through the $http
 * service. On 500 status responses it will check if the config contains message keys to display.
 *
 * This provider will not actually display the error, but rather emit an event on the rootscope that
 * another component can react to and thus handle rest errors in a centralized fashion.
 *
 */
angular.module('rehabstodApp').provider('restErrorResponseInterceptor',
    function() {
      'use strict';

      /**
       * Mandatory provider $get function. here we can inject the dependencies the
       * actual implementation needs.
       */
      this.$get = ['$q', '$rootScope', function($q, $rootScope) {

        function responseError(rejection) {
          if (rejection.status === 500 || rejection.status === -1) {
            if (rejection.config.errorMessageConfig) {
              $rootScope.$emit('rehab.rest.exception', rejection.config.errorMessageConfig);
            }
          }

          return $q.reject(rejection);
        }

        return {
          'responseError': responseError
        };
      }];
    });
