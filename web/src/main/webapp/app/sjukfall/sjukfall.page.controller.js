angular.module('rehabstodApp')
    .controller('SjukfallPageCtrl',
        function($scope, $state, $log, searchfilterViewState, SjukfallService, AppNavViewstate) {
            'use strict';

            $log.debug('SjukfallPageCtrl init');


            if (!AppNavViewstate.isVisningsLageValt()) {
                $log.debug('visningslage not selected - redirecting...');
                $state.go('app.start', {}, {location: 'replace'});
            }

            SjukfallService.loadSjukfall().then(function(successData) {
                searchfilterViewState.sjukfall = successData;
            }, function(errorData) {
                $log.debug('Failed to get sjukfall.');
                $log.debug(errorData);

                searchfilterViewState.model = [];
            });


        });
