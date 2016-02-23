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
                    maxIntygsGlapp: SjukfallFilterViewState.get().glapp
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

        function _exportResult(type) {

            var filterState = SjukfallFilterViewState.get();

            var query = {
                maxIntygsGlapp: filterState.glapp,
                fritext: filterState.fritext,
                langdIntervall: {
                    min: filterState.sjukskrivningslangdModel[0],
                    max: filterState.sjukskrivningslangdModel[1]
                },
                lakare: filterState.lakareModel.getSelected(),
                diagnosGrupper: filterState.diagnosKapitelModel.getSelected(),
                personnummer: []
            };

            return SjukfallProxy.exportResult(type, query);
        }

        return {
            loadSjukfall: _loadSjukfall,
            exportResult: _exportResult
        };
    }]);