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

/**
 * Response intercepter catching ALL responses coming back through the $http
 * service. On 403 status responses, the browser is redirected to the web apps
 * main starting point.
 *
 * Updated to angular 1.4.x. Use like this:
 * $httpProvider.nterceptors.push('http403ResponseInterceptor');
 *
 * The url which the interceptor redirects to on a 403 response can be
 * configured via the providers setRedirectUrl in the apps config block, e.g:
 *
 * http403ResponseInterceptorProvider.setRedirectUrl("/web/403-error.jsp");
 */
angular.module('rehabstodApp').provider('http403ResponseInterceptor',
    function() {
        'use strict';

        /**
         * Object that holds config and default values.
         */
        this.config = {
            redirectUrl: '/'
        };

        /**
         * Setter for configuring the redirectUrl
         */
        this.setRedirectUrl = function(url) {
            this.config.redirectUrl = url;
        };

        /**
         * Mandatory provider $get function. here we can inject the dependencies the
         * actual implementation needs, in this case $q (and $window for redirection)
         */
        this.$get = [ '$q', '$window', 'UserModel', function($q, $window, UserModel) {
            //Ref our config object
            var config = this.config;

            function responseError(rejection) {
                // for 403 responses - redirect browser to configured redirect url
                if (rejection.status === 403) {

                    var redirectUrl = config.redirectUrl;
                    if (UserModel.get().authenticationScheme === UserModel.get().fakeSchemeId) {
                        redirectUrl = '/welcome.html?utloggad';
                    }

                    UserModel.get().loggedIn = false;
                    $window.location.href = redirectUrl;
                }
                // signal rejection (arguably not meaningful here since we just
                // issued a redirect)
                return $q.reject(rejection);
            }

            return {
                'responseError': responseError
            };
        }];
    });
