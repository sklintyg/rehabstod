angular.module('rehabstodApp')
    .controller('SearchResultsCtrl', function($scope, SjukfallFilterViewState, SjukfallModel) {
        'use strict';


        $scope.filter = SjukfallFilterViewState;
        $scope.model = SjukfallModel;
        $scope.displayedCollection = [].concat($scope.model.get());

        $scope.getToolTip = function(diagnos) {
            return '<b>' + diagnos.kod + '</b><br>' + diagnos.beskrivning;
        };
    });
