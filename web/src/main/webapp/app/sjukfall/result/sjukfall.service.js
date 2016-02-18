angular.module('rehabstodApp').factory('SjukfallService', [
    '$log','SjukfallProxy', 'SjukfallModel',
    function($log, SjukfallProxy, SjukfallModel) {
        'use strict';

        function _loadSjukfall() {
            SjukfallModel.reset();

            var query = {
                maxIntygsGlapp: 0
            };

            return SjukfallProxy.get(query).then(function(successData) {
                SjukfallModel.set(successData);
            }, function(errorData) {
                $log.debug('Failed to get sjukfall.');
                $log.debug(errorData);

                SjukfallModel.reset();
            });
        }

        return {
            loadSjukfall: _loadSjukfall
        };
    }]);