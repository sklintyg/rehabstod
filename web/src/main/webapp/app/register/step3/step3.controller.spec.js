describe('Controller: Step3Ctrl', function() {
    'use strict';

    var state;

    // load the controller's module
    beforeEach(module('htmlTemplates'));
    beforeEach(module('privatlakareApp', function($provide) {
        $provide.value('APP_CONFIG', {});
    }));

    beforeEach(inject(function(_RegisterModel_, _TermsProxy_) {
        var testModel = {
            'befattning':'201013',
            'verksamhetensNamn':'Kliniken','agarForm':'Privat',
            'vardform':'03',
            'verksamhetstyp':'12',
            'arbetsplatskod':'0123456',
            'telefonnummer':'070123456','epost':'a@a.a',
            'adress':'Gatan','postnummer':'12345','postort':'Linköping','kommun':'Linköping','lan':'Östergötland',
            'legitimeradYrkesgrupp': 'Läkare', 'specialitet': 'Allmän medicin', 'forskrivarkod': '0123456'
        };
        spyOn(_RegisterModel_, 'init').and.callFake(function() {
            return testModel;
        });
        var testTerms = {
        };
        spyOn(_TermsProxy_, 'getTerms').and.callFake(function() {
            return {
               then: function(callback) {
                   callback(testTerms);
               }
            };
        });
    }));

    var scope;

    describe('when user is registered', function() {
        beforeEach(inject(function($controller, $rootScope, _$state_, _UserModel_) {
            scope = $rootScope.$new();
            scope.registerForm = {};
            state = _$state_;

            spyOn(_UserModel_, 'isRegistered').and.callFake(function() { return true; });

            $controller('Step3Ctrl', {
                $scope: scope
            });
        }));

        it('should go to state app.register.complete if user is registered', function() {
            scope.$apply();
            expect(state.is('app.register.complete')).toBe(true);
        });
    });

    describe('when user is not registered and step2 is not valid', function() {
        beforeEach(inject(function($controller, $rootScope, _$state_, _UserModel_, _RegisterModel_) {
            scope = $rootScope.$new();
            scope.registerForm = {};
            state = _$state_;

            spyOn(_UserModel_, 'isRegistered').and.callFake(function() { return false; });
            spyOn(_RegisterModel_, 'validForStep3').and.callFake(function() { return false; });

            $controller('Step3Ctrl', {
                $scope: scope
            });
        }));

        it('should go to state app.register.step2', function() {
            scope.$apply();
            expect(state.is('app.register.step2')).toBe(true);
        });
    });

    describe('when user is not registered', function() {
        var RegisterProxy;
        var Step3ViewState;

        beforeEach(inject(function($controller, $rootScope, _$state_, _UserModel_, _RegisterModel_,
            _RegisterNavigationService_, _RegisterProxy_, _Step3ViewState_) {
            scope = $rootScope.$new();
            scope.registerForm = {
                $valid: true
            };
            state = _$state_;
            RegisterProxy = _RegisterProxy_;
            Step3ViewState = _Step3ViewState_;

            spyOn(_UserModel_, 'isRegistered').and.callFake(function() { return false; });
            spyOn(_RegisterModel_, 'validForStep3').and.callFake(function() { return true; });

            $controller('Step3Ctrl', {
                $scope: scope
            });
        }));

        it('should stay on state app.register.step3', function() {
            scope.$apply();
            expect(state.is('app.register.complete')).toBe(false);
        });

        it('should go to state app.register.complete if server responds with AUTHORIZED', function() {
            spyOn(RegisterProxy, 'registerPrivatlakare').and.callFake(function() {
                return {
                    then: function(onSuccess) {
                        onSuccess({
                            status: 'AUTHORIZED'
                        });
                    }
                };
            });
            scope.createAccount();
            scope.$apply();
            expect(state.is('app.register.complete')).toBe(true);
            expect(Step3ViewState.errorMessage.register).toBe(null);
        });

        it('should go to state app.register.waiting if server responds with NOT_AUTHORIZED', function() {
            spyOn(RegisterProxy, 'registerPrivatlakare').and.callFake(function() {
                return {
                    then: function(onSuccess) {
                        onSuccess({
                            status: 'NOT_AUTHORIZED'
                        });
                    }
                };
            });
            scope.createAccount();
            scope.$apply();
            expect(state.is('app.register.waiting')).toBe(true);
            expect(Step3ViewState.errorMessage.register).toBe(null);
        });

        it('should go to state app.register.waiting if server responds with WAITING_FOR_HOSP', function() {
            spyOn(RegisterProxy, 'registerPrivatlakare').and.callFake(function() {
                return {
                    then: function(onSuccess) {
                        onSuccess({
                            status: 'WAITING_FOR_HOSP'
                        });
                    }
                };
            });
            scope.createAccount();
            scope.$apply();
            expect(state.is('app.register.waiting')).toBe(true);
            expect(Step3ViewState.errorMessage.register).toBe(null);
        });

        it('should show error message if createAccount fails', function() {
            spyOn(RegisterProxy, 'registerPrivatlakare').and.callFake(function() {
                return {
                    then: function(onSuccess, onError) {
                        onError({
                            errorData:{
                                errorCode: 0,
                                message: ''
                            }
                        });
                    }
                };
            });
            scope.createAccount();
            scope.$apply();
            expect(Step3ViewState.errorMessage.register).not.toBe(null);
        });
    });
});