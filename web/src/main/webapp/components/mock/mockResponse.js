angular.module('rehabstodApp').value('mockResponse', {
    'hospOK': {
        'hospInformation': {
            'personalPrescriptionCode': '1234567',
            'specialityNames': ['specialityName1', 'specialityName2'],
            'hsaTitles': ['hsaTitle1', 'hsaTitle2']
        }
    },
    'hospModel': {
        'personalPrescriptionCode': '1234567',
        'specialityNames': ['specialityName1', 'specialityName2'],
        'hsaTitles': ['hsaTitle1', 'hsaTitle2']
    },
    'hospFail': {
        'hospInformation': null
    },
    'hospFailModel': {},
    'omradeOK': {
        'omradeList': [
            {'postnummer': '13155', 'postort': 'NACKA', 'kommun': 'STOCKHOLM', 'lan': 'STOCKHOLM'},
            {'postnummer': '13155', 'postort': 'NACKA', 'kommun': 'NACKA', 'lan': 'STOCKHOLM'}
        ]
    },
    'omradeModel': [
        {'postnummer': '13155', 'postort': 'NACKA', 'kommun': 'STOCKHOLM', 'lan': 'STOCKHOLM'},
        {'postnummer': '13155', 'postort': 'NACKA', 'kommun': 'NACKA', 'lan': 'STOCKHOLM'}
    ],
    'userOK': {
        user: {
            'namn': 'Oskar Johansson',
            'status': 'NOT_STARTED',
            'personalIdentityNumber': '191212121212',
            'authenticationScheme': 'urn:inera:rehabstod:eleg:fake',
            'fakeSchemeId': 'urn:inera:rehabstod:eleg:fake',
            'loggedIn': false,
            'nameFromPuService': false,
            'nameUpdated': false
        }
    },

    'userModel': {
        'hsaId': 'IFV1239877878-1049',
        'namn': 'Jan Nilsson',
        'titel': '',
        'authenticationScheme': 'urn:inera:rehabstod:siths:fake',
        'vardgivare': [
            {
                '@class': 'se.inera.intyg.common.integration.hsa.model.Vardgivare',
                'id': 'IFV1239877878-1041',
                'namn': 'WebCert-Vårdgivare1',
                'vardenheter': [
                    {
                        '@class': 'se.inera.intyg.common.integration.hsa.model.Vardenhet',
                        'id': 'IFV1239877878-1042',
                        'namn': 'WebCert-Enhet1',
                        'epost': 'enhet1@webcert.invalid.se',
                        'postadress': 'Storgatan 1',
                        'postnummer': '12345',
                        'postort': 'Småmåla',
                        'telefonnummer': '0101234567890',
                        'arbetsplatskod': '1234567890',
                        'start': null,
                        'end': null,
                        'mottagningar': []
                    }
                ]
            }],
        'befattningar': [],
        'valdVardenhet': {
            '@class': 'se.inera.intyg.common.integration.hsa.model.Vardenhet',
            'id': 'IFV1239877878-1042',
            'namn': 'WebCert-Enhet1',
            'epost': 'enhet1@webcert.invalid.se',
            'postadress': 'Storgatan 1',
            'postnummer': '12345',
            'postort': 'Småmåla',
            'telefonnummer': '0101234567890',
            'arbetsplatskod': '1234567890',
            'start': null,
            'end': null,
            'mottagningar': []
        }, 'valdVardgivare': {
            '@class': 'se.inera.intyg.common.integration.hsa.model.Vardgivare',
            'id': 'IFV1239877878-1041',
            'namn': 'WebCert-Vårdgivare1',
            'vardenheter': [
                {
                    '@class': 'se.inera.intyg.common.integration.hsa.model.Vardenhet',
                    'id': 'IFV1239877878-1042',
                    'namn': 'WebCert-Enhet1',
                    'epost': 'enhet1@webcert.invalid.se',
                    'postadress': 'Storgatan 1',
                    'postnummer': '12345',
                    'postort': 'Småmåla',
                    'telefonnummer': '0101234567890',
                    'arbetsplatskod': '1234567890',
                    'start': null,
                    'end': null,
                    'mottagningar': []
                }]
        },
        'roles': {
            'LAKARE': {
                'name': 'LAKARE',
                'desc': 'Läkare',
                'privileges': []
            }
        },
        'totaltAntalVardenheter': 1
    },
    'termsOK': {'terms': {'text': 'Användaravtal placeholder', 'version': 0}},
    'termsModel': {'text': 'Användaravtal placeholder', 'version': 0}
});