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

describe('Controller: ErrorCtrl', function() {
    'use strict';

    // load the controller's module
    beforeEach(angular.mock.module('rehabstodApp', function($provide) {
        $provide.value('APP_CONFIG', { webcertUrl: 'webcertUrl' });
    }));

    var scope, sessionStorage;

    var testMessage = 'Test error message';

    describe('New error message', function() {
        beforeEach(inject(function($controller, $rootScope, _$sessionStorage_) {
            scope = $rootScope.$new();
            sessionStorage = _$sessionStorage_;
            $controller('ErrorCtrl', {
                $scope: scope,
                $stateParams:{
                    'errorMessage' : testMessage
                }
            });
        }));

        it('should store errormessage in session', function() {
            expect(scope.errorMessage).toBe(testMessage);
            expect(sessionStorage.errorMessage).toBe(testMessage);
        });
    });

    describe('Page reloaded with same error message', function() {
        beforeEach(inject(function($controller, $rootScope, _$sessionStorage_) {
            scope = $rootScope.$new();
            sessionStorage = _$sessionStorage_;
            sessionStorage.errorMessage = testMessage;
            $controller('ErrorCtrl', {
                $scope: scope
            });
        }));

        it('should load errormessage from sessionstorage', function() {
            expect(scope.errorMessage).toBe(testMessage);
        });
    });
});
