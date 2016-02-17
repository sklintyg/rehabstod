angular.module('rehabstodApp')
    .controller('SjukfallPageCtrl',
        function($scope, $state, $log, SjukfallModel, SjukfallService) {
            'use strict';

            $log.debug('SjukfallPageCtrl init');

            SjukfallModel.reset();
            SjukfallService.loadSjukfall().then(function(successData) {
                SjukfallModel.set(successData);
            }, function(errorData) {
                $log.debug('Failed to get sjukfall.');
                $log.debug(errorData);

                SjukfallModel.reset();
            });


        });