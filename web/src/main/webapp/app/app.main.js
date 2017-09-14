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
    'taiPlaceholder',
    'ngStorage',
    'rehabstodcommon.dynamiclink'
]).value('THROTTLE_MILLISECONDS', 300).config(function(stConfig) {
    'use strict';

    stConfig.sort.skipNatural = true;
    stConfig.sort.delay = 100;
});

deferredBootstrapper.bootstrap({
    element: document.body,
    module: 'rehabstodApp',
    resolve: {
        LINKS: ['$http', function($http) {
            'use strict';
            return $http.get('/api/config/links');
        }],
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
        messageService, dynamicLinkService, UserProxy, UserModel, USER_DATA, LINKS, $uibModalStack) {
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
        messageService.addLinks(LINKS);

        dynamicLinkService.addLinks(LINKS);

        $rootScope.$on('$stateChangeStart',
            function(event, toState, toParams, fromState/*, fromParams*/) {
                $uibModalStack.dismissAll();

                $log.debug('$stateChangeStart: from "' + fromState.name + '" to "' + toState.name + '"');

                var user = UserModel.get();
                if (user.valdVardenhet === null && toState.name!=='appselectunit') {
                    event.preventDefault();
                    $log.debug('No vardenhet selected - redirecting to selection page!');

                    $state.go('appselectunit', {},  {location: false});
                } else if ((toState.name === 'app.sjukfall.result') && !UserModel.isPdlConsentGiven()) {
                    event.preventDefault();

                    // The if-statement is for the corner case where someone tries to access /app.html#/sjukfall directly
                    // without having a valid fromState (e.g. using bookmark or similar while already being logged in)
                    if (fromState['abstract']) {
                        $state.go('app.sjukfall.start');
                    } else {
                        $log.debug('PDL logging consent not given - redirecting to give consent page!');

                        var msgConfig = {
                            bodyTextKey: 'modal.pdlconsent.' + (UserModel.isLakare() ? 'lakare' : 'rehabkoordinator') + '.body'
                        };
                        $rootScope.$emit('show.pdl.consent', msgConfig);

                        // This is a workaround so the "Pågående sjukfall" tab doesn't stay selected if the user was redirected
                        // to the PDL dialog and then chose to Avbryt.
                        $state.reload();
                    }
                }
                else if (toState.data && angular.isFunction(toState.data.rule)) {
                    var result = toState.data.rule(fromState, toState, UserModel);
                    if (result && result.to) {
                        event.preventDefault();
                        //$log.debug(
                        //    '$stateChangeStart to ' + toState.name + ' was overridden by a rule. new destination : ' +
                        //    result.to);
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
