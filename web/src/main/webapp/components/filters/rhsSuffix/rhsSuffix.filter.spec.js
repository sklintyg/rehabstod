describe('Filter: RhsSuffixFilter', function () {
    'use strict';

    // load the controller's module
    beforeEach(module('rehabstodApp'));

    it('has a rhsSuffix filter', inject(function($filter) {
        expect($filter('rhsSuffix')).not.toBeNull();
    }));

    it('should return "..." if no value ', inject(function (rhsSuffixFilter) {
        var noValue = '...';

        expect(rhsSuffixFilter(null, 'st', noValue)).toEqual(noValue);
        expect(rhsSuffixFilter(null, null, noValue)).toEqual(noValue);
        expect(rhsSuffixFilter('', null, noValue)).toEqual(noValue);
        expect(rhsSuffixFilter(false, null, noValue)).toEqual(noValue);
        expect(rhsSuffixFilter(null, null, null)).toBeNull();
    }));

    it('should return value with suffix', inject(function (rhsSuffixFilter) {

        expect(rhsSuffixFilter(123, null, null)).toEqual(123);
        expect(rhsSuffixFilter(123, 'st', null)).toEqual('123 st');
        expect(rhsSuffixFilter(123, 'st', '...')).toEqual('123 st');
        expect(rhsSuffixFilter(0, 'st', '...')).toEqual('0 st');
    }));

});
