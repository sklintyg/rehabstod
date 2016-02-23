angular.module('rehabstodApp')
    .controller('RhsFilterCtrl',
        function($scope, $filter, $log, SjukfallFilterViewState, SjukfallModel, DiagnosKapitelModel, LakareModel, UserModel) {
            'use strict';

            $scope.filterViewState = SjukfallFilterViewState;
            $scope.user = UserModel.get();


            $scope.showSearchFilter = true;
            $scope.position = 'bottom';
            $scope.model = SjukfallModel;

            $scope.$watchCollection('model.get()', function(value) {
                //Update contents on those models of filtercomponents that depends on the searchresults contents, i.e
                // uniqueness of lakare diagnoskapitel.
                var lakare = $filter('rhsUnique')(value, 'lakare');
                if (!angular.equals($scope.filterViewState.get().lakareModel.getNames(), lakare)) {
                    $scope.filterViewState.get().lakareModel.set(lakare);
                }

                $scope.filterViewState.get().diagnosKapitelModel.setActivDiagnosKapitelIdlist(
                    $filter('rhsUnique')(value, 'diagnos.kapitel'));

            });

            $scope.onResetFilterClick = function() {
                $scope.filterViewState.reset();
                $log.debug('reset filterViewState');
            };
        }
    )
    .directive('rhsFilter',
        function() {
            'use strict';

            return {
                restrict: 'E',
                scope: {},
                controller: 'RhsFilterCtrl',
                templateUrl: 'components/appDirectives/sjukfall/rhsFilter/rhsFilter.directive.html'
            };
        });
