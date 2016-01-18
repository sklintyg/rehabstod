describe('Controller: ErrorCtrl', function() {
    'use strict';

    // load the controller's module
    beforeEach(angular.mock.module('privatlakareApp', function($provide) {
        $provide.value('APP_CONFIG', { webcertUrl: 'webcertUrl' });
    }));

    var scope, sessionStorage;

    var testMessage = 'Test error message';

    describe('New error message', function() {
        beforeEach(inject(function($controller, $rootScope, _$sessionStorage_) {
            scope = $rootScope.$new();
            sessionStorage = _$sessionStorage_;
            $controller('ErrorCtrl', {
                $scope: scope,
                $stateParams:{
                    'errorMessage' : testMessage
                }
            });
        }));

        it('should store errormessage in session', function() {
            expect(scope.errorMessage).toBe(testMessage);
            expect(sessionStorage.errorMessage).toBe(testMessage);
        });
    });

    describe('Page reloaded with same error message', function() {
        beforeEach(inject(function($controller, $rootScope, _$sessionStorage_) {
            scope = $rootScope.$new();
            sessionStorage = _$sessionStorage_;
            sessionStorage.errorMessage = testMessage;
            $controller('ErrorCtrl', {
                $scope: scope
            });
        }));

        it('should load errormessage from sessionstorage', function() {
            expect(scope.errorMessage).toBe(testMessage);
        });
    });
});
