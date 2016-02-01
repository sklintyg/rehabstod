angular.module('rehabstodApp').directive('rhsStatPanel',
    ['messageService', 'UnitCertificateSummaryModel', 'UnitCertificateSummaryProxy',
        function(messageService, UnitCertificateSummaryModel, UnitCertificateSummaryProxy) {
            'use strict';

            return {
                restrict: 'E',
                scope: {
                    vardenhet: '='
                },
                controller: function($scope) {

                    $scope.model = UnitCertificateSummaryModel.get();

                    if ($scope.model.total === null) {
                        UnitCertificateSummaryProxy.get().then(function(data) {
                            UnitCertificateSummaryModel.set(data);
                        });
                    }
                },
                templateUrl: 'components/commonDirectives/rhsStatPanel/rhsStatPanel.directive.html'
            };
        }]);
