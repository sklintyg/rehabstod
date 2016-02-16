angular.module('rehabstodApp')
    .controller('SjukfallSearchResultsCtrl', function($scope, SjukfallFilterViewState, SjukfallModel, UserModel) {
        'use strict';

        $scope.filter = SjukfallFilterViewState;
        $scope.model = SjukfallModel;
        $scope.user = UserModel.get();

        $scope.displayedCollection = [].concat($scope.model.get());

        $scope.getToolTip = function(diagnos) {
            return '<b>' + diagnos.kod + '</b><br>' + diagnos.beskrivning;
        };
        
        $scope.showMoreInTable = function() {
            $scope.limit += 50;
        };

        $scope.resetLimit = function() {
            $scope.limit = 100;
        };

        $scope.resetLimit();
    });
