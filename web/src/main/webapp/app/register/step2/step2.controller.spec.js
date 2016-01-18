describe('Controller: Step2Ctrl', function() {
    'use strict';

    var state;

    // load the controller's module
    beforeEach(module('htmlTemplates'));
    beforeEach(module('privatlakareApp', function($provide) {
        $provide.value('APP_CONFIG', {});
    }));

    var scope;

    describe('when user is registered', function() {
        beforeEach(inject(function($controller, $rootScope, _$state_, _UserModel_) {
            scope = $rootScope.$new();
            scope.registerForm = {};
            state = _$state_;

            spyOn(_UserModel_, 'isRegistered').and.callFake(function() { return true; });

            $controller('Step2Ctrl', {
                $scope: scope
            });
        }));

        it('should go to state app.register.complete if user is registered', function() {
            scope.$apply();
            expect(state.is('app.register.complete')).toBe(true);
        });
    });

    describe('when user is not registered and step1 is not valid', function() {
        beforeEach(inject(function($controller, $rootScope, _$state_, _UserModel_, _RegisterModel_) {
            scope = $rootScope.$new();
            scope.registerForm = {};
            state = _$state_;

            spyOn(_UserModel_, 'isRegistered').and.callFake(function() { return false; });
            spyOn(_RegisterModel_, 'validForStep2').and.callFake(function() { return false; });

            $controller('Step2Ctrl', {
                $scope: scope
            });
        }));

        it('should go to state app.register.step1', function() {
            scope.$apply();
            expect(state.is('app.register.step1')).toBe(true);
        });
    });

    describe('when user is not registered', function() {
        var RegisterNavigationService;

        beforeEach(inject(function($controller, $rootScope, _$state_, _UserModel_, _RegisterNavigationService_, _RegisterModel_) {
            scope = $rootScope.$new();
            scope.registerForm = {
                $valid: true
            };
            state = _$state_;
            RegisterNavigationService = _RegisterNavigationService_;

            spyOn(_UserModel_, 'isRegistered').and.callFake(function() { return false; });
            spyOn(_RegisterModel_, 'validForStep2').and.callFake(function() { return true; });

            $controller('Step2Ctrl', {
                $scope: scope
            });
        }));

        it('should stay on state app.register.step2 if user is registered', function() {
            scope.$apply();
            expect(state.is('app.register.complete')).toBe(false);
        });

        it('should go to step3 when form submitted and valid', function() {
            spyOn(RegisterNavigationService, 'navigationAllowed').and.callFake(function() { return true; });
            scope.submitForm();
            scope.$apply();
            expect(state.is('app.register.step3')).toBe(true);
        });

        it('should not go to step3 when form submitted and valid', function() {
            spyOn(RegisterNavigationService, 'navigationAllowed').and.callFake(function() { return false; });
            scope.submitForm();
            scope.$apply();
            expect(state.is('app.register.step3')).toBe(false);
        });
    });
});