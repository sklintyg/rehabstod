angular.module('rehabstodApp')
    .controller('SearchFilterCtrl', function($scope, SjukfallFilterViewState, SjukfallModel) {
        'use strict';

        $scope.showSearchFilter = true;
        $scope.model = SjukfallModel;

        $scope.$watch('model.get()', function(value) {
            $scope.lakare = unigeValues(value, 'lakare');
            $scope.diagnos = unigeValues(value, 'diagnos');
        }, true);


        $scope.sjukskrivningslangd = [1, 366];

        $scope.filter = SjukfallFilterViewState;

        $scope.$watch('sjukskrivningslangd', function(val) {
            $scope.filter.sjukskrivningslangd.low = val[0];
            $scope.filter.sjukskrivningslangd.high = val[1] < 366 ? val[1] : null;

        }, true);


        function unigeValues(array, key) {
            var values = array.map(function(obj) { return obj[key]; });
            values = values.filter(function(v,i) { return values.indexOf(v) === i; });

            return values;
        }

    });


