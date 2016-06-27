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

/* global window */

(function() {
    'use strict';

    $.get('/api/user').then(function() {

    }, function(error) {
        if (error.status === 403) {
            window.location = 'login.html';
        }
    });


    // --- end test hooks

    // Globally configure jquery not to cache ajax requests.
    // Our other angular $http service driven requests have their own solution (using an interceptor)

    $.ajaxSetup({cache: false});

    var rehabApp = angular.module('rehabstodApp',
        ['ui.bootstrap', 'ui.router', 'ngCookies', 'ngSanitize', 'ngAnimate', 'ngMockE2E', 'ngStorage',
            'ui.bootstrap-slider',
            'smart-table',
            'infinite-scroll',
            'taiPlaceholder']);

    var app = angular.module('showcase',
        ['ui.bootstrap', 'ui.router', 'ngCookies', 'ngSanitize', 'ngAnimate', 'ngMockE2E', 'rehabstodApp', 'rhsIndexApp']);

    app.value('networkConfig', {
        defaultTimeout: 30000 // test: 1000
    });

    app.value('APP_CONFIG', {
        diagnosKapitelList: [{
            id: 'Id1',
            name: 'Val 1'
        },{
            id: 'Id2',
            name: 'Val 2'
        },{
            id: 'Id3',
            name: 'Val 3'
        }]
    });

    app.config(['$httpProvider', '$logProvider',
        function($httpProvider, $logProvider) {

            // Add cache buster interceptor
            $httpProvider.interceptors.push('http403ResponseInterceptor');

            // Enable debug logging
            $logProvider.debugEnabled(true);
        }]);

    // Inject language resources
    app.run(['$rootScope', '$httpBackend', 'UserModel', 'SjukfallSummaryModel',
        function($rootScope, $httpBackend, UserModel, SjukfallSummaryModel) {
            $rootScope.lang = 'sv';
            $rootScope.DEFAULT_LANG = 'sv';

            //Kanske vi kan (i resp controller) sätta upp 'when' mockning så att direktiven kan köra som i en sandbox
            // (Se exempel i arendehantering.controller.js)?
            // Detta kanske gör det möjligt att kunna laborera med ett direktivs alla funktioner som även kräver backendkommunikation.
            $httpBackend.whenGET(/^\/api\/*/).respond(200);
            $httpBackend.whenPOST(/^\/api\/*/).respond(200);
            $httpBackend.whenPUT(/^\/api\/*/).respond(200);

            $httpBackend.whenGET(/^\/moduleapi\/*/).respond(200);
            $httpBackend.whenPOST(/^\/moduleapi\/*/).respond(200);
           // $httpBackend.whenPUT(/^\/moduleapi\/*/).respond(200);

            //Ev. templates skall få hämtas på riktigt
            $httpBackend.whenGET(/^.+\.html/).passThrough();

            UserModel.set({
                namn: 'Test User',
                valdVardgivare: {namn: 'Vårdgivare'},
                valdVardenhet: {namn: 'Vårdenhet'}
            });

            SjukfallSummaryModel.set({
                total:117,
                genders:[
                    {gender:'F', count:56},
                    {gender:'M', count:61}
                ],
                groups:[
                    {grupp:{id:'A00-E90,G00-L99,N00-N99',name: 'Somatiska sjukdomar'},count:73},
                    {grupp:{id:'M00-M99',name: 'Muskuloskeletala sjukdomar'},count:23},
                    {grupp:{id:'',kapitelList:[],name:'Utan giltig diagnoskod'},count:21}
                ]
            });
        }]);

    // Inject language resources
    rehabApp.run(['$rootScope', 'messageService',
        function($rootScope, messageService) {

            messageService.addResources(rhsMessages);// jshint ignore:line

            $rootScope.lang = 'sv';
            $rootScope.DEFAULT_LANG = 'sv';

        }]);

}());
