angular.module('rehabstodApp')
    .controller('SearchResultsCtrl', function($scope, searchfilterViewState) {
        'use strict';


        $scope.filter = searchfilterViewState.filter;

        $scope.model = searchfilterViewState;

        $scope.itemsByPage = 100;

        $scope.displayedCollection = [].concat($scope.model.sjukfall);

        $scope.$watch('displayedCollection', function(val) {
            var number = 1;
            angular.forEach(val, function(value) {
                value.number = number++;
            });
        });
    });
