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
describe('Model: UserModel', function() {
    'use strict';

    // Load the module and mock away everything that is not necessary.
    beforeEach(module('rehabstodApp'));

    var UserModel;
    var testJsonData = {
        hsaId: 'IFV1239877878-1049',
        namn: 'Jan Nilsson',
        titel: '',
        urval: null,
        authenticationScheme: 'urn:inera:rehabstod:siths:fake',
        vardgivare: [{
            '@class': 'se.inera.intyg.infra.integration.hsa.model.Vardgivare',
            id: 'IFV1239877878-1041',
            namn: 'WebCert-Vårdgivare1',
            vardenheter: [{
                '@class': 'se.inera.intyg.infra.integration.hsa.model.Vardenhet',
                id: 'IFV1239877878-1042',
                namn: 'WebCert-Enhet1',
                epost: 'enhet1@webcert.invalid.se',
                postadress: 'Storgatan 1',
                postnummer: '12345',
                postort: 'Småmåla',
                telefonnummer: '0101234567890',
                arbetsplatskod: '1234567890',
                start: null,
                end: null,
                mottagningar: [{
                    '@class':'se.inera.intyg.infra.integration.hsa.model.Mottagning',
                    'id':'mottagning 1',
                    'namn':'mottagning 1',
                    'epost':null,
                    'postadress':'',
                    'postnummer':null,
                    'postort':null,
                    'telefonnummer':'',
                    'arbetsplatskod':null,
                    'agandeForm':'OFFENTLIG',
                    'start':null,
                    'end':null,
                    'parentHsaId':'IFV1239877878-1042'
                }]
            }]
        }],
        'befattningar': [],
        'valdVardenhet': {
            '@class': 'se.inera.intyg.infra.integration.hsa.model.Vardenhet',
            id: 'IFV1239877878-1042',
            namn: 'WebCert-Enhet1',
            epost: 'enhet1@webcert.invalid.se',
            postadress: 'Storgatan 1',
            postnummer: '12345',
            postort: 'Småmåla',
            telefonnummer: '0101234567890',
            arbetsplatskod: '1234567890',
            start: null,
            end: null,
            mottagningar: []
        },
        valdVardgivare: {
            '@class': 'se.inera.intyg.infra.integration.hsa.model.Vardgivare',
            id: 'IFV1239877878-1041',
            namn: 'WebCert-Vårdgivare1',
            vardenheter: [{
                '@class': 'se.inera.intyg.infra.integration.hsa.model.Vardenhet',
                id: 'IFV1239877878-1042',
                namn: 'WebCert-Enhet1',
                epost: 'enhet1@webcert.invalid.se',
                postadress: 'Storgatan 1',
                postnummer: '12345',
                postort: 'Småmåla',
                telefonnummer: '0101234567890',
                arbetsplatskod: '1234567890',
                start: null,
                end: null,
                mottagningar: []
            }]
        },
        'roles': {'LAKARE': {'name': 'LAKARE', 'desc': 'Läkare', 'privileges': []}},
        'totaltAntalVardenheter': 2
    };

    // Initialize the controller and a mock scope
    beforeEach(inject(function(_UserModel_) {
        UserModel = _UserModel_;
    }));

    describe('set', function() {
        it('should set name correctly', function() {
            UserModel.set(testJsonData);
            expect(UserModel.get().name).toEqual('Jan Nilsson');
            expect(UserModel.isLakare()).toBeTruthy();

        });
    });

    describe('urval', function() {
        it('should handle get/set urval correctly', function() {
            UserModel.set(testJsonData);
            expect(UserModel.get().urval).toBeNull();
            expect(UserModel.isUrvalSet()).toBeFalsy();

            UserModel.setUrval('ALL');
            expect(UserModel.get().urval).toEqual('ALL');
            expect(UserModel.isUrvalSet()).toBeTruthy();

        });
    });

    describe('test getUnitNameById', function() {
        it('should handle getUnitNameById', function() {
            //Arrange
            UserModel.set(testJsonData);

            //Assert
            expect(UserModel.getUnitNameById('unknown')).toEqual('');
            expect(UserModel.getUnitNameById('IFV1239877878-1042')).toEqual('WebCert-Enhet1');
            expect(UserModel.getUnitNameById('mottagning 1')).toEqual('mottagning 1');


        });
    });

});
