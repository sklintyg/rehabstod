describe('Filter: rhsUniqueFilter', function() {
    'use strict';

    // load the controller's module
    beforeEach(module('rehabstodApp'));

    it('has a rhsUniqueFilter filter', inject(function($filter) {
        expect($filter('rhsUnique')).not.toBeNull();
    }));


    it('should handle plain array using true as propertyExpression', inject(function(rhsUniqueFilter) {

            var dupArray = ['1', 2, 'A', 2, 'A', 'B', 'A'];
            expect(rhsUniqueFilter(dupArray, true)).toEqual(['1', 2, 'A', 'B']);
        })
    );

    it('should handle nested object properties ', inject(function(rhsUniqueFilter) {

            var dupArray = [
                {
                    root: {
                        propA: {
                            propA1: 'a1'
                        },
                        propB: 'b1'
                    }
                },
                {
                    root: {
                        propA: {
                            propA1: 'a1'
                        },
                        propB: 'b2'
                    }
                },
                {
                    root: {
                        propA: {
                            propA1: 'a1'
                        },
                        propB: 'b3'
                    }
                }

            ];
            expect(rhsUniqueFilter(dupArray, 'root.propA.propA1').length).toEqual(1);
            expect(rhsUniqueFilter(dupArray, 'root.propB')).toEqual(['b1', 'b2', 'b3']);
        })
    );


});