angular.module('rehabstodApp')
    .controller('SjukfallResultPageCtrl',
        function(SjukfallService) {
            'use strict';

            SjukfallService.loadSjukfall();
        });