describe('Controller: NavbarCtrl', function () {
    'use strict';

    // load the controller's module
    beforeEach(module('rehabstodApp'));

    var scope;
    var state;

    // Initialize the controller and a mock scope
    beforeEach(inject(function (_$httpBackend_, $controller, $rootScope, _$state_) {
        scope = $rootScope.$new();
        state = _$state_;



        $controller('NavbarCtrl', {
            $scope: scope,
            $state: state
        });
    }));

    it('Test isActive if null', function() {

        expect(scope.isActive(null)).toBeFalsy();
    });

    it('Test isActive if active', function() {

        spyOn(state, 'includes').and.callFake(function(stateName) {
           return (stateName === 'should.be.active');
        });

        expect(scope.isActive('should.be.active')).toBeTruthy();
        expect(state.includes).toHaveBeenCalledWith('should.be.active');

        expect(scope.isActive('notactive')).toBeFalsy();
        expect(state.includes).toHaveBeenCalledWith('notactive');
    });
});
