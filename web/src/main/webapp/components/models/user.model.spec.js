describe('Model: UserModel', function() {
    'use strict';

    // Load the module and mock away everything that is not necessary.
    beforeEach(angular.mock.module('rehabstodApp', function(/*$provide*/) {
        //$provide.value('APP_CONFIG', {});
    }));

    var UserModel;
    var testJsonData = {
        hsaId: 'IFV1239877878-1049',
        namn: 'Jan Nilsson',
        titel: '',
        urval: null,
        authenticationScheme: 'urn:inera:rehabstod:siths:fake',
        vardgivare: [{
            '@class': 'se.inera.intyg.common.integration.hsa.model.Vardgivare',
            id: 'IFV1239877878-1041',
            namn: 'WebCert-Vårdgivare1',
            vardenheter: [{
                '@class': 'se.inera.intyg.common.integration.hsa.model.Vardenhet',
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
        }],
        'befattningar': [],
        'valdVardenhet': {
            '@class': 'se.inera.intyg.common.integration.hsa.model.Vardenhet',
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
            '@class': 'se.inera.intyg.common.integration.hsa.model.Vardgivare',
            id: 'IFV1239877878-1041',
            namn: 'WebCert-Vårdgivare1',
            vardenheter: [{
                '@class': 'se.inera.intyg.common.integration.hsa.model.Vardenhet',
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
        'totaltAntalVardenheter': 1
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

});
