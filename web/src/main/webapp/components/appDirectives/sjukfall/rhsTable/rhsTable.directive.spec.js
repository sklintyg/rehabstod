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
describe('Directive: rhsTable', function () {
    'use strict';

    describe('Controller: RhsTableCtrl', function () {

        // load the controller's module
        beforeEach(module('rehabstodApp'));

        var scope;
        var SjukfallModel;
        var SjukfallFilterViewState;

        // Initialize the controller and a mock scope
        beforeEach(inject(function ($controller, $rootScope, _SjukfallModel_, _SjukfallFilterViewState_) {
            scope = $rootScope.$new();

            SjukfallModel = _SjukfallModel_;
            SjukfallFilterViewState = _SjukfallFilterViewState_;

            $controller('RhsTableCtrl', {
                $scope: scope,
                SjukfallModel: _SjukfallModel_,
                SjukfallFilterViewState: SjukfallFilterViewState
            });
        }));


        it('should return toolTip', function() {
            var diagnos = {
                kod: 123,
                beskrivning: 'Hej'
            };

            expect(scope.getToolTip(diagnos)).toEqual('<b>' + diagnos.kod + '</b><br>' + diagnos.beskrivning);
        });
    });
});


