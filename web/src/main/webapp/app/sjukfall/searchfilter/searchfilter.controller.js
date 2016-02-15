angular.module('rehabstodApp')
    .controller('SearchFilterCtrl',
        function($scope, $filter, SjukfallFilterViewState, SjukfallModel, DiagnosKapitelModel, LakareModel, UserModel) {
            'use strict';

            $scope.filterViewState = SjukfallFilterViewState;
            $scope.user = UserModel.get();


            $scope.showSearchFilter = true;
            $scope.model = SjukfallModel;

            $scope.$watchCollection('model.get()', function(value) {
                //Update contents on those models of filtercomponents that depends on the searchresults contents, i.e
                // uniqueness of lakare diagnoskapitel.
                $scope.filterViewState.get().lakareModel.set($filter('rhsUnique')(value, 'lakare'));
                $scope.filterViewState.get().diagnosKapitelModel.setActivDiagnosKapitelIdlist(
                    $filter('rhsUnique')(value, 'diagnos.kapitel'));

            });
        });


