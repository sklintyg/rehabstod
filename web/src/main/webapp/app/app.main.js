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
  'smart-table'
]);

deferredBootstrapper.bootstrap({
    element: document.body,
    module: 'rehabstodApp',
    resolve: {
        APP_CONFIG: ['$http', function ($http) {
            'use strict';
            return $http.get('/api/config');
        }],
        USER_DATA: ['$http', function ($http) {
            'use strict';
            return $http.get('/api/user');
        }]
    }
});

app.value('networkConfig', {
    defaultTimeout: 1000, // prod: 30000
    regionTimeout: 1000, // prod: 30000
    registerTimeout: 1000, // prod: 30000
    hospTimeout: 1000 // prod: 30000
});

app.config(function ($stateProvider, $urlRouterProvider, $locationProvider, $tooltipProvider, $httpProvider,
    http403ResponseInterceptorProvider) {
    'use strict';

    // Default route
    $urlRouterProvider.otherwise('/');

    // Use /#/ syntax. True = regular / syntax
    $locationProvider.html5Mode(false);

    // Tooltip config
    $tooltipProvider.setTriggers({
        'show': 'hide'
    });

    // Configure 403 interceptor provider
    http403ResponseInterceptorProvider.setRedirectUrl('/');
    $httpProvider.interceptors.push('http403ResponseInterceptor');

    // Add replaceAll function to all strings.
    String.prototype.replaceAll = function(f,r) { // jshint ignore:line
        return this.split(f).join(r);
    };

});

// Inject language resources
app.run(
    function($log, $rootScope, $state, $window,
        messageService, UserProxy, UserModel, USER_DATA, AppNavViewstate) {
        'use strict';

        // Always scroll to top
        $rootScope.$on('$stateChangeSuccess',function(){
            $('html, body').animate({ scrollTop: 0 }, 200);
        });

        $rootScope.lang = 'sv';
        $rootScope.DEFAULT_LANG = 'sv';

        // Get logged in user
        UserModel.init();
        UserModel.set(USER_DATA);

        /* jshint -W117 */
        messageService.addResources(ppMessages);// jshint ignore:line

        $rootScope.$on('$stateChangeStart',
            function(event, toState, toParams, fromState/*, fromParams*/) {
                $log.debug('$stateChangeStart: ' + fromState.name + ' to ' + toState.name);

                if (toState.data && angular.isFunction(toState.data.rule)) {
                    var result = toState.data.rule(fromState, toState, AppNavViewstate, UserModel);
                    if (result && result.to) {
                        event.preventDefault();
                        $log.debug('$stateChangeStart to ' + toState.to + ' was overridden by a rule. new destination : ' + result.to);
                        $state.go(result.to, result.params, result.options);
                    }
                }
            });

        $rootScope.$on('$stateNotFound',
            function(/*event, unfoundState, fromState, fromParams*/){
            });

        $rootScope.$on('$stateChangeSuccess',
            function(/*event, toState, toParams, fromState, fromParams*/){
            });

        $rootScope.$on('$stateChangeError',
            function(event, toState/*, toParams, fromState, fromParams, error*/){
                $log.log('$stateChangeError');
                $log.log(toState);
            });
    });
