angular.module('rehabstodApp')
    .controller('SearchResultsCtrl', function($scope, SjukfallFilterViewState, SjukfallModel) {
        'use strict';


        $scope.filter = SjukfallFilterViewState;

        $scope.model = SjukfallModel;

        $scope.itemsByPage = 100;

        $scope.displayedCollection = [].concat($scope.model.get());

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
