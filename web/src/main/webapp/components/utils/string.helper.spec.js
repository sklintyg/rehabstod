describe('Helper: StringHelper', function() {
    'use strict';

    beforeEach(module('rehabstodApp'));

    var StringHelper;

    // Inject dependencies and mocks
    beforeEach(inject(function(_StringHelper_) {
        StringHelper = _StringHelper_;
    }));

    it('replaceAll', function() {
        expect(StringHelper.replaceAll('A+B','+', ' ')).toEqual('A B');
        expect(StringHelper.replaceAll('A+B+C','+', ' ')).toEqual('A B C');
        expect(StringHelper.replaceAll('AB','+', ' ')).toEqual('AB');
        expect(StringHelper.replaceAll('A B','+', ' ')).toEqual('A B');
        expect(StringHelper.replaceAll('','+', ' ')).toEqual('');
        expect(StringHelper.replaceAll('+','+', ' ')).toEqual(' ');
    });
});