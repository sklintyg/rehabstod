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

        function _exportResult(type, personnummer, sort) {

            var filterState = SjukfallFilterViewState.get();

            var lakare = [];
            var diagnosGrupper = [];

            angular.forEach(filterState.lakareModel.getSelected(), function(item) {
                lakare.push(item.id);
            });

            angular.forEach(filterState.diagnosKapitelModel.getSelected(), function(item) {
                diagnosGrupper.push(item.id);
            });


            var query = {
                sortering: sort,
                maxIntygsGlapp: filterState.glapp,
                fritext: filterState.fritext,
                langdIntervall: {
                    min: filterState.sjukskrivningslangdModel[0],
                    max: filterState.sjukskrivningslangdModel[1]
                },
                lakare: lakare,
                diagnosGrupper: diagnosGrupper,
                personnummer: personnummer
            };

            return SjukfallProxy.exportResult(type, query);
        }

        function _isLoading() {
            return loading;
        }

        return {
            loadSjukfall: _loadSjukfall,
            exportResult: _exportResult,
            isLoading: _isLoading
        };
    }]);