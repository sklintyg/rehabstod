angular.module('rehabstodApp')
    .controller('SjukfallResultPageCtrl',
        function($scope, $state, $log, SjukfallModel, SjukfallService) {
            'use strict';

            $log.debug('SjukfallResultPageCtrl init');

            SjukfallModel.reset();
            SjukfallService.loadSjukfall().then(function(successData) {
                SjukfallModel.set(successData);
            }, function(errorData) {
                $log.debug('Failed to get sjukfall.');
                $log.debug(errorData);

                SjukfallModel.reset();
            });


        });