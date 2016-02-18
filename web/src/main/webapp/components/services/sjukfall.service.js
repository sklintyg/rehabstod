angular.module('rehabstodApp').factory('SjukfallService', [
    '$log','SjukfallProxy', 'SjukfallModel', 'SjukfallFilterViewState',
    function($log, SjukfallProxy, SjukfallModel, SjukfallFilterViewState) {
        'use strict';

        function _loadSjukfall(force) {

            var empty = SjukfallModel.get().length === 0;

            if (force || empty) {
                SjukfallFilterViewState.reset();
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
        }

        return {
            loadSjukfall: _loadSjukfall
        };
    }]);