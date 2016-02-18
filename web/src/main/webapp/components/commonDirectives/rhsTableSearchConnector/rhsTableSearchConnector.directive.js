angular.module('rehabstodApp').directive('rhsTableSearchConnector',
    ['$timeout', 'SjukfallFilterViewState',
        function($timeout, SjukfallFilterViewState) {
            'use strict';

            return {
                restrict: 'E',
                require: '^stTable',
                link: function($scope, element, attr, table) {

                    var onFilterstateUpdated = function(newFilterParameters) {
                        $timeout(function() {
                            table.search(newFilterParameters, 'customSearch');
                        });

                    };
                    //Watch for changes in current filter state
                    $scope.filterViewState = SjukfallFilterViewState;
                    $scope.$watch('filterViewState.getCurrentFilterState()', onFilterstateUpdated, true);


                }
            };
        }]);
