angular.module('rehabstodApp')
    .controller('SjukfallStartCtrl', function($scope, $state, $http,
        UserModel, SjukfallSummaryModel, SjukfallSummaryProxy) {
        'use strict';

        $scope.sjunetAvailable = false;

        function _init() {
            SjukfallSummaryModel.reset();
            SjukfallSummaryProxy.get().then(
                function(data) {
                    SjukfallSummaryModel.set(data);
                    _proceed();
                }, function() {
                    //for some reason, we failed to get sjukfallSummary
                    SjukfallSummaryModel.get().hasError = true;
                    _proceed();
                });
        }

        function _proceed() {
            if (SjukfallSummaryModel.get().total === 0) {
                //No data to show - go to show 'no data for unit' state
                $state.go('app.sjukfall.start.nodata');
            } else if (UserModel.get().isLakare) {
                //Show lakares selection view (we know we have some summaryData)
                $state.go('app.sjukfall.start.lakare');
            } else {
                //Show rehab selection view (we know we have some summaryData)
                $state.go('app.sjukfall.start.rehabkoordinator');
            }
        }

        function _testSjunetConnection() {
            $http.get('https://statistik.intygstjanster.sjunet.org/api/ping', function() {
                    // Success
                    $scope.sjunetAvailable = true;
                },
                function() {
                    // Failure
                    $scope.sjunetAvailable = false;
                });
        }

        _init();
        _testSjunetConnection();

    });
