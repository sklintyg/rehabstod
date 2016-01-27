angular.module('rehabstodApp')
    .controller('SjukfallPageCtrl', function($scope, $log, UserModel, searchfilterViewState, SjukfallService) {
        'use strict';
        $log.debug('SjukfallPageCtrl init');
        $scope.user = UserModel.get();


        SjukfallService.loadSjukfall().then(function(successData) {
            searchfilterViewState.sjukfall = successData;
        }, function(errorData) {
            $log.debug('Failed to get sjukfall.');
            $log.debug(errorData);

            searchfilterViewState.model = [];
        });


    });
