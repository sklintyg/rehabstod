angular.module('rehabstodApp')
    .controller('SearchFilterCtrl',
        function($scope, $filter, SjukfallFilterViewState, SjukfallModel, DiagnosKapitelModel, LakareModel, UserModel,
            APP_CONFIG) {
            'use strict';

            DiagnosKapitelModel.set(APP_CONFIG.diagnosKapitelList);
            $scope.diagnosKapitelModel = DiagnosKapitelModel;
            $scope.lakareModel = LakareModel;
            $scope.user = UserModel.get();


            $scope.showSearchFilter = true;
            $scope.model = SjukfallModel;

            $scope.$watch('model.get()', function(value) {
                $scope.lakareModel.set($filter('rhsUnique')(value, 'lakare'));
                $scope.diagnosKapitelModel.setActivDiagnosKapitelIdlist($filter('rhsUnique')(value, 'diagnos.kapitel'));

            }, true);


            $scope.sjukskrivningslangd = [1, 366];

            $scope.filter = SjukfallFilterViewState;


            $scope.$watch('sjukskrivningslangd', function(val) {
                $scope.filter.sjukskrivningslangd.low = val[0];
                $scope.filter.sjukskrivningslangd.high = val[1] < 366 ? val[1] : null;

            }, true);

        });


