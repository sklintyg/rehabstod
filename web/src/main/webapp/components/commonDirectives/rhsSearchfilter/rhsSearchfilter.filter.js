angular.module('rehabstodApp').filter('rhsSearchfilter', ['$filter', function($filter) {
    'use strict';

    var filterFilter = $filter('filter');
    var standardComparator = function standardComparator(obj, text) {
        text = ('' + text).toLowerCase();
        return ('' + obj).toLowerCase().indexOf(text) > -1;
    };

    return function (array, expression) {
        function customComparator(actual, expected) {

            var isLower = expected.lower;
            var isHigher = expected.higher;
            var higherLimit;
            var lowerLimit;


            if (angular.isObject(expected)) {

                if (isLower || isHigher) {
                    //number range
                    if (isLower) {
                        lowerLimit = expected.lower;

                        if (actual < lowerLimit) {
                            return false;
                        }
                    }

                    if (isHigher) {
                        higherLimit = expected.higher;
                        if (actual > higherLimit) {
                            return false;
                        }
                    }

                    return true;
                }
                //etc

                return true;

            }
            return standardComparator(actual, expected);
        }

        var output = filterFilter(array, expression, customComparator);
        return output;
    };
}]);