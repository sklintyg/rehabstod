angular.module('rehabstodApp').service('HospService',
    function($state,
        HospProxy, ObjectHelper) {
        'use strict';

        this.loadHosp = function(hospViewState, HospModel) {

            var service = this;
            function processHospResult(hospInfo) {
                service.updateHosp('load', hospViewState, HospModel, hospInfo);
            }

            hospViewState.loading.hosp = true;
            HospProxy.getHospInformation().then(processHospResult, processHospResult);
        };

        this.updateHosp = function(source, hospViewState, HospModel, hospInfo) {
            hospViewState.loading.hosp = false;
            if(!ObjectHelper.isDefined(hospInfo) ||
                (!ObjectHelper.isDefined(hospInfo.personalPrescriptionCode) &&
                    !ObjectHelper.isDefined(hospInfo.hsaTitles) &&
                    !ObjectHelper.isDefined(hospInfo.specialityNames))) {
                hospViewState.errorMessage.hosp = 'hosp.error.' + source;
                HospModel.reset();
            } else {
                hospViewState.errorMessage.hosp = false;
                var hospModel = HospModel.get();
                hospModel.legitimeradYrkesgrupp = ObjectHelper.returnJoinedArrayOrNull(hospInfo.hsaTitles);
                hospModel.specialitet = ObjectHelper.returnJoinedArrayOrNull(hospInfo.specialityNames);
                hospModel.forskrivarkod = ObjectHelper.valueOrNull(hospInfo.personalPrescriptionCode);
            }
            updateViewState(hospViewState, HospModel.get());
        };

        function updateViewState(hospViewState, hospModel) {
            hospViewState.socialstyrelsenUppgifter = [
                { id: 'legitimeradYrkesgrupp', name: 'Legimiterad yrkesgrupp', value: hospModel.legitimeradYrkesgrupp, locked: true },
                { id: 'specialitet', name: 'Specialitet', value: hospModel.specialitet, locked: true },
                { id: 'forskrivarkod', name: 'Förskrivarkod', value: hospModel.forskrivarkod, locked: true }
            ];
        }
    }
);
