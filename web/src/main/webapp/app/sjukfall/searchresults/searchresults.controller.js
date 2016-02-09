angular.module('rehabstodApp')
    .controller('SearchResultsCtrl', function($scope, SjukfallFilterViewState, SjukfallModel) {
        'use strict';


        $scope.filter = SjukfallFilterViewState;
        $scope.model = SjukfallModel;
        $scope.displayedCollection = [].concat($scope.model.get());


        $scope.$watchCollection('displayedCollection', updateRowNumber);

        function updateRowNumber() {
            var number = 1;
            angular.forEach($scope.displayedCollection, function(value) {
                value.number = number++;
            });
        }
    });
