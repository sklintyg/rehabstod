angular.module('rehabstodApp').filter('rhsUnique', [
    '$parse',
    function($parse) {
        'use strict';
        return function(items, propertyExpression) {
            if (propertyExpression === false) {
                return items;
            }
            if ((propertyExpression || angular.isUndefined(propertyExpression)) && angular.isArray(items)) {
                var newItems = [];
                //get via parse or otherwise just return the expression
                var get = angular.isString(propertyExpression) ? $parse(propertyExpression) : function(item) {
                    return item;
                };
                var extractPropertyFilteredOn = function(item) {
                    //if the property is an object, parse it, otherwise just return it.
                    return angular.isObject(item) ? get(item) : item;
                };

                angular.forEach(items, function(item) {
                    var isDuplicate = false;
                    //check if we already have collected this value...
                    for (var i = 0; i < newItems.length; i++) {
                        if (angular.equals(extractPropertyFilteredOn(newItems[i]), extractPropertyFilteredOn(item))) {
                            isDuplicate = true;
                            break;
                        }
                    }
                    if (!isDuplicate) {
                        newItems.push(extractPropertyFilteredOn(item));
                    }
                });
                return  newItems;
            }
            return items;
        };
    }
]);