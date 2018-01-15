/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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
angular
    .module('rehabstodApp')
    .config(function($stateProvider, $urlRouterProvider, $locationProvider, $uibTooltipProvider, $httpProvider,
    http403ResponseInterceptorProvider, stConfig) {
    'use strict';

        // Default route is "Landing page"
        $urlRouterProvider.otherwise('/app/index');

        // Use /#/ syntax. True = regular / syntax
        $locationProvider.html5Mode(false);

        // Tooltip config
        $uibTooltipProvider.setTriggers({
            'show': 'hide'
        });

        // Configure 403 interceptor provider
        http403ResponseInterceptorProvider.setRedirectUrl('/');
        $httpProvider.interceptors.push('http403ResponseInterceptor');

        // Configure restErrorResponseInterceptorProvider interceptor provider
        $httpProvider.interceptors.push('restErrorResponseInterceptor');

        // Add replaceAll function to all strings.
        String.prototype.replaceAll = function(f, r) { // jshint ignore:line
            return this.split(f).join(r);
        };

        stConfig.sort.skipNatural = true;
        stConfig.sort.delay = 100;
    });
