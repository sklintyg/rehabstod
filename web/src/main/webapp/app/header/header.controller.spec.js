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

describe('Controller: HeaderController', function () {
    'use strict';

    // load the controller's module
    beforeEach(module('rehabstodApp'));

    var scope;

    // Initialize the controller and a mock scope
    beforeEach(inject(function ($controller, $rootScope) {
        scope = $rootScope.$new();

        $controller('HeaderController', {
            $scope: scope
        });
    }));


    it('show description if user is lakare', function() {
        var role = {
            name: 'LAKARE',
            desc: 'Läkare'
        };

        expect(scope.showRoleDescription(role)).toBeTruthy();
    });

    it('hide description if user is REHABKOORDINATOR', function() {
        var role = {
            name: 'REHABKOORDINATOR',
            desc: 'Rehabkoordinator'
        };

        expect(scope.showRoleDescription(role)).toBeFalsy();
    });
});


