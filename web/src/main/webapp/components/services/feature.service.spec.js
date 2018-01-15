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
describe('Service: FeatureService', function() {
    'use strict';

    // load the controller's module
    beforeEach(angular.mock.module('rehabstodApp'));

    var featureService;
    var UserModel;

    beforeEach(inject(function(_featureService_, _UserModel_) {
        featureService = _featureService_;
        UserModel = _UserModel_;

        UserModel.set({
            features: ['test']
        });
    }));

    it('Has feature', function() {
        expect(featureService.hasFeature('test')).toBeTruthy();
    });

    it('Missing feature', function() {
        expect(featureService.hasFeature('missing')).toBeFalsy();
    });
});
