angular.module('rehabstodApp').factory('SjukfallFilterViewState', [
    'DiagnosKapitelModel', 'LakareModel', 'APP_CONFIG',
    function(DiagnosKapitelModel, LakareModel, APP_CONFIG) {
        'use strict';

        var state = {
            diagnosKapitelModel: DiagnosKapitelModel,
            lakareModel: LakareModel,
            sjukskrivningslangdModel: [1, 366],
            freeTextModel: ''
        };

        //Kanske initiera diagnoskapitelmodellen samtidigt som UserModel i appmain eller något?
        state.diagnosKapitelModel.set(APP_CONFIG.diagnosKapitelList);


        function _reset() {
            //TODO:återställ till som när man kom in första gången
            state.sjukskrivningslangdModel[0] = 1;
            //diagnosKapitelModel.reset();
            //lakareModel.reset();
        }

        function _getCurrentFilterState() {
            var selectedDiagnosKapitel = [];
            angular.forEach(state.diagnosKapitelModel.getSelected(), function(value) {
                this.push(value.id);
            }, selectedDiagnosKapitel);

            var selectedLakare = [];
            angular.forEach(state.lakareModel.getSelected(), function(value) {
                this.push(value.id);
            }, selectedLakare);

            return {
                diagnosKapitel: selectedDiagnosKapitel,
                lakare: selectedLakare,
                sjukskrivningslangd: [state.sjukskrivningslangdModel[0],
                    state.sjukskrivningslangdModel[1] > 365 ? null : state.sjukskrivningslangdModel[1]],
                freeText : state.freeTextModel

            };

        }

        function _getState() {
            return state;
        }

        _reset();

        return {
            reset: _reset,
            getCurrentFilterState: _getCurrentFilterState,
            get: _getState
        };
    }])
;