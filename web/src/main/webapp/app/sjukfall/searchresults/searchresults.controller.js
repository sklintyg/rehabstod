angular.module('rehabstodApp')
    .controller('SearchResultsCtrl', function($scope, searchfilterViewState) {
        'use strict';


        $scope.filter = searchfilterViewState.filter;

        $scope.model = searchfilterViewState;

        $scope.itemsByPage = 100;

        $scope.displayedCollection = [].concat($scope.model.sjukfall);

        $scope.currentPage = 1;

        $scope.$watch('displayedCollection', updateRowNumber);

        $scope.pageChangedFn = function(newPage) {
            $scope.currentPage = newPage;

            updateRowNumber();
        };

        function updateRowNumber() {
            var number = ($scope.currentPage - 1) * $scope.itemsByPage + 1;
            angular.forEach($scope.displayedCollection, function(value) {
                value.number = number++;
            });
        }
    });
