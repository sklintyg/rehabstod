describe('Proxy: RegisterProxy', function() {
    'use strict';

    // Load the module and mock away everything that is not necessary.
    beforeEach(angular.mock.module('htmlTemplates'));
    beforeEach(angular.mock.module('privatlakareApp', function($provide) {
        $provide.value('RegisterModel', jasmine.createSpyObj('RegisterModel', ['get', 'convertToViewModel', 'convertToDTO']));
    }));

    var RegisterProxy, $rootScope, $httpBackend;
    
    var correctRegisterModel = {'befattning':{'id':'201013','label':'Företagsläkare'},'verksamhetensNamn':'arar','agarForm':'Privat',
        'vardform':{'id':'03','label':'Hemsjukvård'},'verksamhetstyp':{'id':'12','label':'Laboratorieverksamhet'},'arbetsplatskod':'aetaet',
        'telefonnummer':'325325','epost':'a@a.a','epost2':'a@a.a','adress':'gesgs','postnummer':'35463',
        'postort':'Linköping','kommun':'Linköping','lan':'Östergötland',
        'legitimeradYrkesgrupp':'Läkare','specialitet':'Allmän medicin','forskrivarkod':'0123456'};
/*
    var correctDTO = { 'registration':{
        'befattning':'202010',
        'verksamhetensNamn':'ewrsfdbg',
        'agarForm':'Privat',
        'vardform':'01',
        'verksamhetstyp':'15',
        'arbetsplatskod':'ewrtryret',
        'telefonnummer':'3265272',
        'epost':'a@a.a','epost2':'a@a.a','adress':'eutaelithjl','postnummer':'125313','postort':'Linköping','kommun':'Linköping','lan':'Östergötland',
        'legitimeradYrkesgrupp':'Läkare','specialitet':'Allmän medicin','forskrivarkod':'0123456'
    }};
    //var incorrectRegisterModel = angular.copy(correctRegisterModel);
*/
    var testGetLakareResponse = {'registration':
        {'befattning':'201012','verksamhetensNamn':'dfggd','agarForm':'Privat','vardform':'03','verksamhetstyp':'15','arbetsplatskod':'dfgd',
        'telefonnummer':'a@a.a','epost':'a@a.a',
        'adress':'fsdf','postnummer':'325235','postort':'Linköping','kommun':'Linköping','lan':'Östergötland'}
    };
/*
    var testGetLakareModel = {
        'registration': {
            'befattning':'201012','verksamhetensNamn':'dfggd','agarForm':'Privat','vardform':'03','verksamhetstyp':'15','arbetsplatskod':'dfgd',
            'telefonnummer':'a@a.a','epost':'a@a.a',
            'adress':'fsdf','postnummer':'325235','postort':'Linköping','kommun':'Linköping','lan':'Östergötland'
        }
    };
*/
    // Initialize the controller and a mock scope
    beforeEach(inject(function(_$rootScope_, _$httpBackend_, _RegisterProxy_) {
        $httpBackend = _$httpBackend_;
        RegisterProxy = _RegisterProxy_;
        $rootScope = _$rootScope_;
    }));

    describe('getPrivatlakare', function() {
        it('should get the data for the logged in privatlakare', function() {
            var onSuccess = jasmine.createSpy('onSuccess');
            var onError = jasmine.createSpy('onError');
            $httpBackend.expectGET('/api/registration').respond(testGetLakareResponse);

            RegisterProxy.getPrivatlakare().then(onSuccess, onError);
            $httpBackend.flush();
            // promises are resolved/dispatched only on next $digest cycle
            $rootScope.$apply();

            expect(onSuccess).toHaveBeenCalled();
            expect(onError).not.toHaveBeenCalled();
        });
    });

    describe('savePrivatlakare', function() {
        it('should save changes to a privatlakare successfully when called with a correct RegisterModel', function() {
            var onSuccess = jasmine.createSpy('onSuccess');
            var onError = jasmine.createSpy('onError');
            $httpBackend.expectPOST('/api/registration/save').respond('200', { status: 'OK'} );

            RegisterProxy.savePrivatlakare(correctRegisterModel).then(onSuccess, onError);
            $httpBackend.flush();
            // promises are resolved/dispatched only on next $digest cycle
            $rootScope.$apply();

            expect(onSuccess).toHaveBeenCalled();
            expect(onError).not.toHaveBeenCalled();
        });
    });

    describe('registerPrivatlakare', function() {
        it('should register a privatlakare successfully when called with a correct RegisterModel', function() {
            var onSuccess = jasmine.createSpy('onSuccess');
            var onError = jasmine.createSpy('onError');
            $httpBackend.expectPOST('/api/registration/create').respond('200', { status: 'AUTHORIZED'} );

            RegisterProxy.registerPrivatlakare(correctRegisterModel).then(onSuccess, onError);
            $httpBackend.flush();
            // promises are resolved/dispatched only on next $digest cycle
            $rootScope.$apply();

            expect(onSuccess).toHaveBeenCalled();
            expect(onError).not.toHaveBeenCalled();
        });
    });
});
