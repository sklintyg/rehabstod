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

var app = angular.module('rehabstodApp', [
    'ngAnimate',
    'ngCookies',
    'ngResource',
    'ngSanitize',
    'ngStorage',
    'ngMessages',
    'ui.router',
    'ui.bootstrap',
    'ui.bootstrap-slider',
    'smart-table',
    'infinite-scroll',
    'taiPlaceholder'
]).value('THROTTLE_MILLISECONDS', 300).config(function(stConfig) {
    'use strict';

    stConfig.sort.skipNatural = true;
    stConfig.sort.delay = 100;
});

deferredBootstrapper.bootstrap({
    element: document.body,
    module: 'rehabstodApp',
    resolve: {
        APP_CONFIG: ['$http', function($http) {
            'use strict';
            return $http.get('/api/config');
        }],
        USER_DATA: ['$http', function($http) {
            'use strict';
            return $http.get('/api/user');
        }]
    },
    onError: function(error) {
        'use strict';
        //We don't have access to any components in our app yet, since the bootstrap resolve failed, so
        //redirect to error page with our best guess.

        var reason = 'unknown';
        //If the resolve failed with a 403 status, we're most likely not authenticated, e.g used a
        //bookmark / deep link without logging in first.
        if (error && error.status === 403) {
            reason = 'denied';
        }
        window.location.href = '/error.jsp?reason=' + reason; // jshint ignore:line
    }
});

app.value('networkConfig', {
    defaultTimeout: 15000
});

/*
 * Base config for all pice chars rendered in the app. Can be overridden and extended.
 */
app.value('pieChartBaseConfig', {

    colors: ['#E11964',
        '#032C53',
        '#FFBA3E',
        '#799745',
        '#3CA3FF',
        '#C37EB2',
        '#2A5152',
        '#FB7F4D',
        '#5CC2BC',
        '#704F38'],
    chart: {
        type: 'pie'
    },
    exporting: {
        enabled: false
    },
    credits: {
        enabled: false
    },
    tooltip: {
        headerFormat: ''
    },
    plotOptions: {
        pie: {
            allowPointSelect: true,
            cursor: 'pointer',
            showInLegend: true,
            dataLabels: {
                enabled: false
            },
            size: 100,
            center: ['50%', '50%']

        }
    },
    series: [{
        data: []
    }]

});


app.config(function($stateProvider, $urlRouterProvider, $locationProvider, $uibTooltipProvider, $httpProvider,
    http403ResponseInterceptorProvider) {
    'use strict';

    // Default route
    $urlRouterProvider.otherwise('/');

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

});

// Inject language resources
app.run(
    function($log, $rootScope, $state, $window,
        messageService, UserProxy, UserModel, USER_DATA) {
        'use strict';

        // Always scroll to top
        $rootScope.$on('$stateChangeSuccess', function() {
            $('html, body').animate({scrollTop: 0}, 200);
        });

        $rootScope.lang = 'sv';
        $rootScope.DEFAULT_LANG = 'sv';

        // Get logged in user
        UserModel.init();
        UserModel.set(USER_DATA);

        /* jshint -W117 */
        messageService.addResources(rhsMessages);// jshint ignore:line

        $rootScope.$on('$stateChangeStart',
            function(event, toState, toParams, fromState/*, fromParams*/) {
                $log.debug('$stateChangeStart: ' + fromState.name + ' to ' + toState.name);

                if (toState.data && angular.isFunction(toState.data.rule)) {
                    var result = toState.data.rule(fromState, toState, UserModel);
                    if (result && result.to) {
                        event.preventDefault();
                        $log.debug(
                            '$stateChangeStart to ' + toState.name + ' was overridden by a rule. new destination : ' +
                            result.to);
                        $state.go(result.to, result.params, result.options);
                    }
                }
            });

        $rootScope.$on('$stateNotFound',
            function(/*event, unfoundState, fromState, fromParams*/) {
            });

        $rootScope.$on('$stateChangeSuccess',
            function(/*event, toState, toParams, fromState, fromParams*/) {
            });

        $rootScope.$on('$stateChangeError',
            function(event, toState/*, toParams, fromState, fromParams, error*/) {
                $log.log('$stateChangeError');
                $log.log(toState);
            });
    });
