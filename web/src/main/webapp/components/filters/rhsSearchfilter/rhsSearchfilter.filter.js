angular.module('rehabstodApp').filter('rhsSearchfilter', [function() {
    'use strict';


    var standardComparator = function standardComparator(obj, text) {
        text = ('' + text).toLowerCase();
        return ('' + obj).toLowerCase().indexOf(text) > -1;
    };

    return function(array, expression) {
        function customComparator(actual, filterParams) {

            //DiagnosKapitel
            if (!matchAny(actual.diagnos.kapitel, filterParams.diagnosKapitel)) {
                return false;
            }

            //Lakare
            if (!matchAny(actual.lakare.namn, filterParams.lakare)) {
                return false;
            }

            //Sjukskrivningslangd
            if (!range(actual.dagar, filterParams.sjukskrivningslangd[0], filterParams.sjukskrivningslangd[1])) {
                return false;
            }

            if (filterParams.freeText.length > 0 && !passWildCardSearch(actual, filterParams.freeText)) {
                return false;
            }


            return true;
        }

        function passWildCardSearch(actual, wildCard) {
            return standardComparator(actual.quickSearchString, wildCard);
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
            if (angular.isUndefined(matchAny) || matchAny.length === 0) {
                return true;
            }

            if (angular.isUndefined(actual)) {
                return false;
            }

            for (var i = 0; i < matchAny.length; i++) {
                if (actual.toLowerCase() === matchAny[i].toLowerCase()) {
                    return true;
                }
            }

            return false;
        }

        function processItems(array, filterParam) {
            var filteredArray = [];
            angular.forEach(array, function(item) {
                if (customComparator(item, filterParam.customSearch)) {
                    filteredArray.push(item);
                }

            });

            return filteredArray;
        }


        return processItems(array, expression);
    };

}]);