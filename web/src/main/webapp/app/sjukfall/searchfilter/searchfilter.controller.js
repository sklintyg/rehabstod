angular.module('rehabstodApp')
    .controller('SearchFilterCtrl', function($scope, searchfilterViewState) {
        'use strict';

        $scope.showSearchFilter = true;

        $scope.sjukskrivningslangd = [1, 366];

        $scope.model = searchfilterViewState.model;

        $scope.$watch('sjukskrivningslangd', function(val) {
            $scope.model.sjukskrivningslangd.low = val[0];
            $scope.model.sjukskrivningslangd.high = val[1] < 366 ? val[1] : null;

        }, true);

    });


