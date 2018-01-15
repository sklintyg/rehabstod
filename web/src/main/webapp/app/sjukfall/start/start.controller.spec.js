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

describe('Controller: SjukfallStartCtrl', function() {
    'use strict';

    // load the controller's module
    beforeEach(module('rehabstodApp'));
    beforeEach(module('htmlTemplates'));

    var controller, scope, state, http, userModel, sjukfallSummaryModel, sjukfallSummaryProxy, appConfig, $httpBackend;
    
    // Initialize the mock scope
    beforeEach(inject(function($controller, $rootScope, _$httpBackend_, _$state_, _$http_, _UserModel_, _SjukfallSummaryModel_,
        _SjukfallSummaryProxy_) {
        controller = $controller;
        scope = $rootScope.$new();
        state = {go: function() {}};
        http = _$http_;
        userModel = _UserModel_;
        sjukfallSummaryModel = _SjukfallSummaryModel_;
        sjukfallSummaryProxy = _SjukfallSummaryProxy_;
        appConfig = {statistikTjanstBaseUrl: 'https://statistik.intygstjanster.sjunet.org'};
        $httpBackend = _$httpBackend_;
    }));


    it('With sjunet access sjunetAvailable bool should be true', function() {
        $httpBackend.expectGET('/api/sjukfall/summary').respond({});
        $httpBackend.expectGET('https://statistik.intygstjanster.sjunet.org/api/ping').respond({});

        initializeController(controller, scope, state, http, userModel, sjukfallSummaryModel, sjukfallSummaryProxy,
            appConfig);

        scope.$digest();
        $httpBackend.flush();
        expect(scope.sjunetAvailable).toBe(true);
    });

    it('Without sjunet access sjunetAvailable bool should be false', function() {
        $httpBackend.expectGET('/api/sjukfall/summary').respond({});
        $httpBackend.expectGET('https://statistik.intygstjanster.sjunet.org/api/ping').respond(400, 'ERROR');

        initializeController(controller, scope, state, http, userModel, sjukfallSummaryModel, sjukfallSummaryProxy,
            appConfig);

        scope.$digest();
        $httpBackend.flush();
        expect(scope.sjunetAvailable).toBe(false);
    });

    function initializeController(controller, scope, state, http, userModel, sjukfallSummaryModel, sjukfallSummaryProxy,
        appConfig) {
        controller('SjukfallStartCtrl', {
            $scope: scope,
            $state: state,
            $http: http,
            UserModel: userModel,
            SjukfallSummaryModel: sjukfallSummaryModel,
            SjukfallSummaryProxy: sjukfallSummaryProxy,
            APP_CONFIG: appConfig
        });
    }

});
