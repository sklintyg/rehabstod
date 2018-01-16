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

describe('Proxy: UserProxy', function() {
    'use strict';

    // Load the module and mock away everything that is not necessary.
    beforeEach(angular.mock.module('htmlTemplates'));
    beforeEach(angular.mock.module('rehabstodApp', function(/*$provide*/) {

    }));

    var UserProxy, mockResponse, $rootScope, $httpBackend;
    
    // Initialize the controller and a mock scope

    beforeEach(inject(function(_$rootScope_, _$httpBackend_, _UserProxy_, _mockResponse_) {
        $httpBackend = _$httpBackend_;
        UserProxy = _UserProxy_;
        $rootScope = _$rootScope_;
        mockResponse = _mockResponse_;
    }));


    describe('UserProxy', function() {
        it('should return user when change selected unit', function() {

            var onSuccess = jasmine.createSpy('onSuccess');
            var onError = jasmine.createSpy('onError');

            $httpBackend.expectPOST('/api/user/andraenhet').respond(mockResponse.userModel);

            UserProxy.changeSelectedUnit('123').then(onSuccess, onError);
            $httpBackend.flush();
            // promises are resolved/dispatched only on next $digest cycle
            $rootScope.$apply();

            expect(onSuccess).toHaveBeenCalledWith(mockResponse.userModel);
            expect(onError).not.toHaveBeenCalled();
        });
    });

});
