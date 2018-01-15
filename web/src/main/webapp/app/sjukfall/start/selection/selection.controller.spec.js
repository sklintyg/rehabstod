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
describe('Controller: SjukfallStartSelectionCtrl', function() {
    'use strict';

    // load the controller's module
    beforeEach(module('rehabstodApp'));

    var scope, state, userModel, userProxy, sjukfallService;
    var user = {};


    // Initialize the controller and a mock scope
    beforeEach(inject(function($controller, $rootScope, _$state_, _UserModel_, _UserProxy_) {
        scope = $rootScope.$new();
        state = _$state_;
        userModel = _UserModel_;
        userProxy = _UserProxy_;
        sjukfallService = {loadSjukfall: function(){}};

        $controller('SjukfallStartSelectionCtrl', {
            $scope: scope,
            UserModel: userModel,
            UserProxy: userProxy,
            SjukfallService: sjukfallService
        });
    }));


    it('should setUrval on user and then change state to sjukfall', function() {
        user = {urval: 'ALL'};
        spyOn(state, 'go');
        spyOn(userModel, 'set');

        scope.onSelectUrval(user.urval);

        expect(state.go).toHaveBeenCalledWith('app.sjukfall.result');

    });


});
