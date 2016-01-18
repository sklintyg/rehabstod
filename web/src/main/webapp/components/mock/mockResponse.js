angular.module('privatlakareApp').value('mockResponse', {
    'hospOK': {
        'hospInformation': {
            'personalPrescriptionCode' : '1234567',
            'specialityNames': ['specialityName1', 'specialityName2'],
            'hsaTitles': ['hsaTitle1','hsaTitle2']
        }
    },
    'hospModel': {
        'personalPrescriptionCode' : '1234567',
        'specialityNames': ['specialityName1', 'specialityName2'],
        'hsaTitles': ['hsaTitle1','hsaTitle2']
    },
    'hospFail' : {
        'hospInformation': null
    },
    'hospFailModel' : {
    },
    'omradeOK': {
        'omradeList':[
            {'postnummer':'13155','postort':'NACKA','kommun':'STOCKHOLM','lan':'STOCKHOLM'},
            {'postnummer':'13155','postort':'NACKA','kommun':'NACKA','lan':'STOCKHOLM'}
        ]
    },
    'omradeModel': [
        {'postnummer':'13155','postort':'NACKA','kommun':'STOCKHOLM','lan':'STOCKHOLM'},
        {'postnummer':'13155','postort':'NACKA','kommun':'NACKA','lan':'STOCKHOLM'}
    ],
    'userOK': { user: {
            'namn':'Oskar Johansson',
            'status':'NOT_STARTED',
            'personalIdentityNumber': '191212121212',
            'authenticationScheme': 'urn:inera:rehabstod:eleg:fake',
            'fakeSchemeId': 'urn:inera:rehabstod:eleg:fake',
            'loggedIn': false,
            'nameFromPuService': false,
            'nameUpdated': false
        }
    },
    'userModel': {
        'namn':'Oskar Johansson',
        'status':'NOT_STARTED',
        'personalIdentityNumber': '191212121212',
        'authenticationScheme': 'urn:inera:rehabstod:eleg:fake',
        'fakeSchemeId': 'urn:inera:rehabstod:eleg:fake',
        'loggedIn': false,
        'nameFromPuService': false,
        'nameUpdated': false
    },
    'termsOK': {'terms':{'text':'Användaravtal placeholder','version':0}},
    'termsModel': {'text':'Användaravtal placeholder','version':0}
});