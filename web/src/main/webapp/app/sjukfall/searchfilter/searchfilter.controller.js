angular.module('rehabstodApp')
    .controller('SearchFilterCtrl', function($scope, SjukfallFilterViewState, SjukfallModel, DiagnosGruppModel) {
        'use strict';

        $scope.showSearchFilter = true;
        $scope.model = SjukfallModel;

        $scope.$watch('model.get()', function(value) {
            $scope.lakare = unigeValues(value, 'lakare');
        }, true);

        var diagnosJsonTemp = [
            {id: 'A00-B99', name: 'Vissa infektionssjukdomar och parasitsjukdomar'},
            {id: 'C00-D48', name: 'Tumörer- disabled'},
            {id: 'D50-D89', name: 'Sjukdomar i blod och blodbildande organ samt vissa rubbningar i immunsystemet'},
            {id: 'E00-E90', name: 'Endokrina sjukdomar, nutritionsrubbningar och ämnesomsättningssjukdomar'}
        ];
        DiagnosGruppModel.set(diagnosJsonTemp);

        $scope.diagnosGruppModel =  DiagnosGruppModel;
        $scope.sjukskrivningslangd = [1, 366];

        $scope.filter = SjukfallFilterViewState;


        $scope.$watch('sjukskrivningslangd', function(val) {
            $scope.filter.sjukskrivningslangd.low = val[0];
            $scope.filter.sjukskrivningslangd.high = val[1] < 366 ? val[1] : null;

        }, true);


        function unigeValues(array, key) {
            var values = array.map(function(obj) {
                return obj[key];
            });
            values = values.filter(function(v, i) {
                return values.indexOf(v) === i;
            });

            return values;
        }

    });


