describe('Controller: SearchResultsCtrl', function () {
    'use strict';

    // load the controller's module
    beforeEach(module('rehabstodApp'));

    var scope;
    var searchfilterViewState;

    // Initialize the controller and a mock scope
    beforeEach(inject(function ($controller, $rootScope, _searchfilterViewState_) {
        scope = $rootScope.$new();

        searchfilterViewState = _searchfilterViewState_;

        $controller('SearchResultsCtrl', {
            $scope: scope,
            searchfilterViewState: _searchfilterViewState_
        });
    }));


    it('should add row number to model', function($timeout) {
        expect(scope.displayedCollection).toEqual([]);

        searchfilterViewState.sjukfall = [{name: 'first'}, {name: 'next'}];

        var expectArray = [{name: 'first', number: 1}, {name: 'next', number: 2}];

        $timeout(function() {
            expect(scope.displayedCollection).toEqual(expectArray);
        });
    });

    it('should update row number when table page changes', function($timeout) {
        expect(scope.displayedCollection).toEqual([]);

        searchfilterViewState.sjukfall = [{name: 'first'}, {name: 'next'}];

        scope.itemsByPage = 1;

        var expectArray = [{name: 'first', number: 1}];

        $timeout(function() {
            expect(scope.displayedCollection).toEqual(expectArray);


            scope.pageChangedFn(2);

            expectArray = [{name: 'next', number: 2}];
            expect(scope.displayedCollection).toEqual(expectArray);
        });

    });
});
