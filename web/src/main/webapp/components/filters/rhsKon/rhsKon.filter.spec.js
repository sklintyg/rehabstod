describe('Filter: RhsKonFilter', function () {
    'use strict';

    // load the controller's module
    beforeEach(module('rehabstodApp'));

    it('has a rhsKon filter', inject(function($filter) {
        expect($filter('rhsKon')).not.toBeNull();
    }));

    it('should return male', inject(function (rhsKonFilter, messageService) {
        var returnValue = messageService.getProperty('label.gender.male');

        expect(rhsKonFilter('m')).toEqual(returnValue);
        expect(rhsKonFilter('M')).toEqual(returnValue);
    }));

    it('should return female', inject(function (rhsKonFilter, messageService) {
        var returnValue = messageService.getProperty('label.gender.female');

        expect(rhsKonFilter('f')).toEqual(returnValue);
        expect(rhsKonFilter('F')).toEqual(returnValue);
    }));

    it('should return -', inject(function (rhsKonFilter, messageService) {
        var returnValue = messageService.getProperty('label.gender.undefined');

        expect(rhsKonFilter(null)).toEqual(returnValue);
        expect(rhsKonFilter(undefined)).toEqual(returnValue);
        expect(rhsKonFilter()).toEqual(returnValue);
        expect(rhsKonFilter('x')).toEqual(returnValue);
    }));

});
