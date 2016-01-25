angular.module('rehabstodApp')
    .controller('SearchResultsCtrl', function($scope, searchfilterViewState) {
        'use strict';


        $scope.model = searchfilterViewState.model;

        var
            nameList = ['Pierre', 'Pol', 'Jacques', 'Robert', 'Elisa'],
            familyName = ['Dupont', 'Germain', 'Delcourt', 'bjip', 'Menez'],
            diagnosName = ['F 3.21', 'M', 'F 3.22', '-'],
            lakareName = ['Mästerkatten', 'Dupont', 'Germain', 'Delcourt', 'bjip', 'Menez'];

        function createRandomItem() {
            var
                firstName = nameList[Math.floor(Math.random() * 5)],
                lastName = familyName[Math.floor(Math.random() * 5)],
                lakare = lakareName[Math.floor(Math.random() * 6)],
                diagnos = diagnosName[Math.floor(Math.random() * 4)],
                langd = 1 + Math.floor(Math.random() * 400),
                dag = Math.floor(Math.random() * 30) + 1;

            if (dag < 10) {
                dag = '0' + dag;
            }

            return {
                number: 0,
                personnummer: '19700123-9297',
                namn: firstName + ' ' + lastName,
                enkeltIntyg: 'Ja',
                diagnos: diagnos,
                startdatum: '2015-10-' + dag,
                slutdatum: '2015-12-15',
                sjukskrivningslangd: langd,
                antalIntyg: 1,
                grad: '100%',
                lakare: lakare
            };
        }

        $scope.itemsByPage = 20;


        $scope.rowCollection = [];
        for (var j = 0; j < 2000; j++) {
            $scope.rowCollection.push(createRandomItem());
        }

        $scope.displayedCollection = [].concat($scope.rowCollection);

        $scope.$watch('displayedCollection', function(val) {
            var number = 1;
            angular.forEach(val, function(value) {
                value.number = number++;
            });
        });
    });
