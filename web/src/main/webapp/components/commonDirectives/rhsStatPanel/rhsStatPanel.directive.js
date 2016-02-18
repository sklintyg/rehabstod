angular.module('rehabstodApp').directive('rhsStatPanel',
    ['SjukfallSummaryModel', 'SjukfallSummaryProxy', '$rootScope',
        function(SjukfallSummaryModel, SjukfallSummaryProxy, $rootScope) {
            'use strict';

            return {
                restrict: 'E',
                replace: true,
                scope: {
                    vardenhet: '='
                },
                controller: function($scope) {

                    /**
                     * Private functions
                     */
                    function _loadData() {
                        SjukfallSummaryModel.reset();
                        SjukfallSummaryProxy.get().then(function(data) {
                            data.women = Math.round(data.women * 10) / 10;
                            data.men = Math.round(data.men * 10) / 10;
                            SjukfallSummaryModel.set(data);
                        });
                    }

                    var unregisterFn = $rootScope.$on('SelectedUnitChanged', function(/*event, value*/) {
                        _loadData();
                    });
                    //rootscope on event listeners aren't unregistered automatically when 'this' directives
                    //scope is destroyed, so let's take care of that.
                    $scope.$on('$destroy', unregisterFn);

                    /**
                     * Exposed scope properties
                     */
                    $scope.model = SjukfallSummaryModel.get();

                    if ($scope.model.total === null) {
                        _loadData();
                    }

                    $scope.today = new Date();
                },
                templateUrl: 'components/commonDirectives/rhsStatPanel/rhsStatPanel.directive.html'
            };
        }]);
