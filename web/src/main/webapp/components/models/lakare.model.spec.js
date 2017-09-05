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

describe('Model: LakareModel', function() {
    'use strict';

    // Load the module and mock away everything that is not necessary.
    beforeEach(module('rehabstodApp'));

    var LakareModel;
    var testJsonData = ['KALLE','OLLE','NISSE'];

    // Initialize the controller and a mock scope
    beforeEach(inject(function(_LakareModel_) {
        LakareModel = _LakareModel_;
    }));

    describe('set', function() {
        it('should set internal array correctly', function() {
            LakareModel.set(testJsonData);
            expect(LakareModel.get().length).toEqual(testJsonData.length);
            expect(LakareModel.get()[0].id).toEqual(testJsonData[0]);
            expect(LakareModel.get()[0].displayValue).toEqual(testJsonData[0]);
            expect(LakareModel.get()[0].selected).toBeFalsy();
            expect(LakareModel.get()[0].disabled).toBeFalsy();
        });
    });

    describe('getSelected', function() {
        it('should return selected items correctly', function() {
            LakareModel.set(testJsonData);
            expect(LakareModel.getSelected().length).toEqual(0);

            LakareModel.get()[0].selected = true;
            expect(LakareModel.getSelected().length).toEqual(1);
            expect(LakareModel.getSelected()[0].id).toEqual(testJsonData[0]);
            expect(LakareModel.getSelected()[0].displayValue).toEqual(testJsonData[0]);

        });
    });

    describe('reset', function() {
        it('should reset correctly', function() {
            LakareModel.set(testJsonData);

            expect(LakareModel.getSelected().length).toEqual(0);
            LakareModel.get()[0].selected = true;

            expect(LakareModel.getSelected().length).toEqual(1);
            LakareModel.reset();
            expect(LakareModel.getSelected().length).toEqual(0);

        });
    });

});
