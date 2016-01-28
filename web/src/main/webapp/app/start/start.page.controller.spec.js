describe('Controller: StartPageCtrl', function() {
    'use strict';

    // load the controller's module
    beforeEach(module('rehabstodApp'));
    beforeEach(module('htmlTemplates'));

    var scope, state, userModel;

    describe('When users role is LÄKARE', function() {
        // Initialize the controller and a mock scope
        beforeEach(inject(function($controller, $rootScope, _$state_, _UserModel_) {
            scope = $rootScope.$new();
            state = _$state_;
            userModel = _UserModel_;

            spyOn(userModel, 'isLakare').and.callFake(function() {
                return true;
            });

            spyOn(state, 'go');

            $controller('StartPageCtrl', {
                $scope: scope
            });
        }));


        it('should redirect to substate for läkare', function() {

            expect(state.go).toHaveBeenCalledWith('app.start.lakare', {}, {location: 'replace'});
        });
    });

    describe('When users role is NOT LÄKARE', function() {
        // Initialize the controller and a mock scope
        beforeEach(inject(function($controller, $rootScope, _$state_, _UserModel_) {
            scope = $rootScope.$new();
            state = _$state_;
            userModel = _UserModel_;

            spyOn(userModel, 'isLakare').and.callFake(function() {
                return false;
            });

            spyOn(state, 'go');

            $controller('StartPageCtrl', {
                $scope: scope
            });
        }));


        it('should redirect to substate for rehabkoordinator', function() {

            expect(state.go).toHaveBeenCalledWith('app.start.rehabkoordinator', {}, {location: 'replace'});
        });
    });
});