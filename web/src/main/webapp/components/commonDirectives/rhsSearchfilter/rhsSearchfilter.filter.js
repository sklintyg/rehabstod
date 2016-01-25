angular.module('rehabstodApp').filter('rhsSearchfilter', ['$filter', function($filter) {
    'use strict';

    var filterFilter = $filter('filter');
    var standardComparator = function standardComparator(obj, text) {
        text = ('' + text).toLowerCase();
        return ('' + obj).toLowerCase().indexOf(text) > -1;
    };

    return function (array, expression) {
        function customComparator(actual, expected) {

            if (angular.isObject(expected)) {

                //matchAny
                if (expected.matchAny) {
                    return matchAny(actual, expected.matchAny);
                }

                if (expected.lower || expected.higher) {
                    return range(actual, expected.lower, expected.higher);
                }
                //etc

                return true;

            }
            return standardComparator(actual, expected);
        }

        function range(actual, lower, higher) {
            //number range
            if (lower) {
                if (actual < lower) {
                    return false;
                }
            }

            if (higher) {
                if (actual > higher) {
                    return false;
                }
            }

            return true;
        }

        function matchAny(actual, matchAny) {
            if (matchAny.all) {
                return true;
            }

            if (!actual) {
                return false;
            }

            for (var i = 0; i < matchAny.items.length; i++) {
                if (actual.toLowerCase() === matchAny.items[i].toLowerCase()) {
                    return true;
                }
            }

            return false;
        }

        var output = filterFilter(array, expression, customComparator);
        return output;
    };
}]);