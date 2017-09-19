/*
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

describe('Filter: RhsSearchFilter', function() {
    'use strict';

    var testJsonData = [
        {
            'patient': {
                'id': '19360721-2222',
                'namn': 'Berit Holgersson',
                'kon': 'F',
                'alder': 79
            },
            'diagnos': {
                'intygsVarde': 'M16.0',
                'kapitel': 'M00-M99',
                'kod': 'M160'
            },
            'start': '2016-02-01',
            'slut': '2016-03-01',
            'dagar': 10,
            'intyg': 4,
            'grader': [100, 50],
            'aktivGrad': 100,
            'lakare': {
                'namn': 'Per Hansson'
            }
        },
        {
            'patient': {
                'id': '19560721-7068',
                'namn': 'Nils Persson',
                'kon': 'M',
                'alder': 59
            },
            'diagnos': {
                'intygsVarde': 'J22.0',
                'kapitel': 'J00-J99',
                'kod': 'J220'
            },
            'start': '2016-08-01',
            'slut': '2016-08-8',
            'dagar': 180,
            'intyg': 3,
            'grader': [25, 50],
            'aktivGrad': 25,
            'lakare': {
                'namn': 'Jan Nilsson'
            }
        },
        {
            'patient': {
                'id': '19560721-7068',
                'namn': 'Nils Persson',
                'kon': 'M',
                'alder': 59
            },
            'diagnos': {
                'intygsVarde': 'J22.0',
                'kapitel': 'J00-J99',
                'kod': 'J220'
            },
            'start': '2016-08-01',
            'slut': '2016-08-8',
            'dagar': 400,
            'intyg': 3,
            'grader': [25, 50],
            'aktivGrad': 25,
            'lakare': {
                'namn': undefined
            }
        }];


    // load the controller's module
    beforeEach(module('rehabstodApp'));

    it('has a rhsSuffix filter', inject(function($filter) {
        expect($filter('rhsSearchfilter')).not.toBeNull();
    }));

    it('should return all with empty filter ', inject(function(rhsSearchfilterFilter) {
        var filterParam = {
            customSearch: {
                diagnosKapitel: [],
                lakare: [],
                sjukskrivningslangd: [1, null],
                alder: [0, null],
                freeText: ''
            }

        };

        expect(rhsSearchfilterFilter(testJsonData, filterParam)).toEqual(testJsonData);

    }));

    it('should filter on diagnoskapitel when set', inject(function(rhsSearchfilterFilter) {
        var filterParam = {
            customSearch: {
                diagnosKapitel: ['M00-M99'],
                lakare: [],
                sjukskrivningslangd: [1, null],
                alder: [0, null],
                freeText: ''
            }
        };
        expect(rhsSearchfilterFilter(testJsonData, filterParam)).toEqual([testJsonData[0]]);
        filterParam.customSearch.diagnosKapitel.push('J00-J99');
        expect(rhsSearchfilterFilter(testJsonData, filterParam)).toEqual(testJsonData);
    }));

    it('should filter on lakare when set', inject(function(rhsSearchfilterFilter) {
        var filterParam = {
            customSearch: {
                diagnosKapitel: [],
                lakare: ['Dummy'],
                sjukskrivningslangd: [1, null],
                alder: [0, null],
                freeText: ''
            }
        };
        expect(rhsSearchfilterFilter(testJsonData, filterParam)).toEqual([]);

        filterParam.customSearch.lakare.push('Per Hansson');
        expect(rhsSearchfilterFilter(testJsonData, filterParam)).toEqual([testJsonData[0]]);
        filterParam.customSearch.lakare.push('Jan Nilsson');
        expect(rhsSearchfilterFilter(testJsonData, filterParam)).toEqual([testJsonData[0], testJsonData[1]]);
    }));

    it('should filter on sjukskrivingslängd when set', inject(function(rhsSearchfilterFilter) {
        var filterParam = {
            customSearch: {
                diagnosKapitel: [],
                lakare: [],
                sjukskrivningslangd: [1, null],
                alder: [0, null],
                freeText: ''
            }
        };
        expect(rhsSearchfilterFilter(testJsonData, filterParam)).toEqual(testJsonData);

        filterParam.customSearch.sjukskrivningslangd = [1, 15];
        expect(rhsSearchfilterFilter(testJsonData, filterParam)).toEqual([testJsonData[0]]);
        filterParam.customSearch.sjukskrivningslangd = [100, 200];
        expect(rhsSearchfilterFilter(testJsonData, filterParam)).toEqual([testJsonData[1]]);
        filterParam.customSearch.sjukskrivningslangd = [200, null];
        expect(rhsSearchfilterFilter(testJsonData, filterParam)).toEqual([testJsonData[2]]);

    }));

    it('should filter on wildcard when set', inject(function(rhsSearchfilterFilter, SjukfallModel) {
        var filterParam = {
            customSearch: {
                diagnosKapitel: [],
                lakare: [],
                sjukskrivningslangd: [1, null],
                alder: [0, null],
                freeText: 'son'
            }
        };
        SjukfallModel.set(testJsonData);
        var expected = SjukfallModel.get();

        expect(rhsSearchfilterFilter(testJsonData, filterParam)).toEqual(expected);

        filterParam.customSearch.freeText = '2222';
        expect(rhsSearchfilterFilter(testJsonData, filterParam)).toEqual([expected[0]]);

        filterParam.customSearch.freeText = 'XXXX';
        expect(rhsSearchfilterFilter(testJsonData, filterParam)).toEqual([]);

        filterParam.customSearch.freeText = 'Jan';
        expect(rhsSearchfilterFilter(testJsonData, filterParam)).toEqual([expected[1]]);
    }));

    it('should filter on alder when set', inject(function(rhsSearchfilterFilter) {
        var filterParam = {
            customSearch: {
                diagnosKapitel: [],
                lakare: [],
                sjukskrivningslangd: [1, null],
                alder: [0, null],
                freeText: ''
            }
        };
        expect(rhsSearchfilterFilter(testJsonData, filterParam)).toEqual(testJsonData);

        filterParam.customSearch.alder = [0, 70];
        expect(rhsSearchfilterFilter(testJsonData, filterParam)).toEqual([testJsonData[1], testJsonData[2]]);
        filterParam.customSearch.alder = [70, 100];
        expect(rhsSearchfilterFilter(testJsonData, filterParam)).toEqual([testJsonData[0]]);

        filterParam.customSearch.alder = [0, 0];
        expect(rhsSearchfilterFilter(testJsonData, filterParam)).toEqual([]);
    }));


});
