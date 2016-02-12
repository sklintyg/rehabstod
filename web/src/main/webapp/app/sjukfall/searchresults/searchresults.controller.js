angular.module('rehabstodApp')
    .controller('SearchResultsCtrl', function($scope, SjukfallFilterViewState, SjukfallModel) {
        'use strict';


        $scope.filter = SjukfallFilterViewState;
        $scope.model = SjukfallModel;
        $scope.displayedCollection = [].concat($scope.model.get());
    });
