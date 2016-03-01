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

describe('Model: DiagnosKapitelModel', function() {
    'use strict';

    // Load the module and mock away everything that is not necessary.
    beforeEach(angular.mock.module('rehabstodApp', function(/*$provide*/) {
        //$provide.value('APP_CONFIG', {});
    }));

    var DiagnosKapitelModel;
    var testJsonData = [
        {id: 'A00-B99', name: 'Vissa infektionssjukdomar och parasitsjukdomar i halsen'},
        {id: 'C00-D48', name: 'Tumörer- disabled'},
        {id: 'D50-D89', name: 'Sjukdomar i blod och blodbildande organ samt vissa rubbningar i immunsystemet'},
        {id: 'E00-E90', name: 'Endokrina sjukdomar, nutritionsrubbningar och ämnesomsättningssjukdomar'}
    ];

    // Initialize the controller and a mock scope
    beforeEach(inject(function(_DiagnosKapitelModel_) {
        DiagnosKapitelModel = _DiagnosKapitelModel_;
    }));

    describe('set', function() {
        it('should set internal array correctly', function() {
            DiagnosKapitelModel.set(testJsonData);
            expect(DiagnosKapitelModel.get().length).toEqual(testJsonData.length);
            expect(DiagnosKapitelModel.get()[0].id).toEqual(testJsonData[0].id);
            expect(DiagnosKapitelModel.get()[0].displayValue).toEqual(testJsonData[0].id + ': ' + testJsonData[0].name);
            expect(DiagnosKapitelModel.get()[0].selected).toBeFalsy();
            expect(DiagnosKapitelModel.get()[0].disabled).toBeFalsy();
        });
    });

    describe('getSelected', function() {
        it('should return selected items correctly', function() {
            DiagnosKapitelModel.set(testJsonData);
            expect(DiagnosKapitelModel.getSelected().length).toEqual(0);

            DiagnosKapitelModel.get()[0].selected = true;
            expect(DiagnosKapitelModel.getSelected().length).toEqual(1);
            expect(DiagnosKapitelModel.getSelected()[0].id).toEqual(testJsonData[0].id);
            expect(DiagnosKapitelModel.getSelected()[0].displayValue).toEqual(testJsonData[0].id + ': ' + testJsonData[0].name);

        });
    });

    describe('reset', function() {
        it('should reset correctly', function() {
            DiagnosKapitelModel.set(testJsonData);

            expect(DiagnosKapitelModel.getSelected().length).toEqual(0);
            DiagnosKapitelModel.get()[0].selected = true;

            expect(DiagnosKapitelModel.getSelected().length).toEqual(1);
            DiagnosKapitelModel.reset();
            expect(DiagnosKapitelModel.getSelected().length).toEqual(0);

        });
    });

});
