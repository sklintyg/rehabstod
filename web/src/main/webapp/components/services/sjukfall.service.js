angular.module('rehabstodApp').factory('SjukfallService', [
    '$log','SjukfallProxy', 'SjukfallModel', 'SjukfallFilterViewState',
    function($log, SjukfallProxy, SjukfallModel, SjukfallFilterViewState) {
        'use strict';

        var loading = false;

        function _loadSjukfall(force) {

            if (loading) {
                return;
            }

            var empty = SjukfallModel.get().length === 0;

            if (force || empty) {
                loading = true;
                SjukfallFilterViewState.reset();
                SjukfallModel.reset();

                var query = {
                    maxIntygsGlapp: 0
                };

                return SjukfallProxy.get(query).then(function(successData) {
                    SjukfallModel.set(successData);
                    loading = false;
                }, function(errorData) {
                    $log.debug('Failed to get sjukfall.');
                    $log.debug(errorData);

                    SjukfallModel.reset();
                    loading = false;
                });
            }
        }

        return {
            loadSjukfall: _loadSjukfall
        };
    }]);