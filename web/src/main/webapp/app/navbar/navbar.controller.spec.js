describe('Controller: NavbarCtrl', function () {
    'use strict';

    // load the controller's module
    beforeEach(module('rehabstodApp'));

    var scope;
    var location;
    var state;

    // Initialize the controller and a mock scope
    beforeEach(inject(function (_$httpBackend_, $controller, $rootScope, _$location_, _$state_) {
        scope = $rootScope.$new();
        location = _$location_;
        state = _$state_;



        $controller('NavbarCtrl', {
            $scope: scope,
            $location: location,
            $state: state
        });
    }));

    it('Test isActive if null', function() {

        expect(scope.isActive(null)).toBeFalsy();
    });

    it('Test isActive if active', function() {

        spyOn(location, 'path').and.callFake(function() {
           return '/#/start';
        });

        expect(scope.isActive('/#/start')).toBeTruthy();
        expect(scope.isActive('/#/notactive')).toBeFalsy();
    });
});
