angular.module('rehabstodApp')
    .controller('SearchResultsCtrl', function($scope) {
        'use strict';
        var
            nameList = ['Pierre', 'Pol', 'Jacques', 'Robert', 'Elisa'],
            familyName = ['Dupont', 'Germain', 'Delcourt', 'bjip', 'Menez'];

        function createRandomItem(number) {
            var
                firstName = nameList[Math.floor(Math.random() * 4)],
                lastName = familyName[Math.floor(Math.random() * 4)],
                langd = Math.floor(Math.random() * 100),
                dag = Math.floor(Math.random() * 30) + 1;

            if (dag < 10) {
                dag = '0' + dag;
            }

            return{
                number: number,
                personnummer: '19701010-1212',
                namn: firstName,
                kon: lastName,
                enkeltIntyg: 'Ja',
                diagnos: lastName,
                startdatum: '2015-10-' + dag,
                slutdatum: '2015-12-15',
                sjukskrivningslangd: langd + ' dagar',
                gard: '100%',
                lakare: 'Test'
            };
        }

        $scope.itemsByPage = 30;

        $scope.rowCollection = [];
        for (var j = 0; j < 2000; j++) {
            $scope.rowCollection.push(createRandomItem(j));
        }
    });
