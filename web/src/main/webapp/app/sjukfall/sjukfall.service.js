angular.module('rehabstodApp').factory('SjukfallService',
    function($log, $q) {
        'use strict';

        function _loadSjukfallTemp() {
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
                    startdatumVardenhet: '2015-10-' + dag,
                    startdatumVardgivare: '2015-09-' + dag,
                    slutdatum: '2015-12-15',
                    sjukskrivningslangd: langd,
                    antalIntyg: 1,
                    grad: '100%',
                    lakare: lakare
                };
            }

            var collection = [];
            for (var j = 0; j < 2000; j++) {
                collection.push(createRandomItem());
            }

            return collection;
        }

        function _loadSjukfall() {
            var promise = $q.defer();

            promise.resolve(_loadSjukfallTemp());

            return promise.promise;
        }

        return {
            loadSjukfall: _loadSjukfall
        };
    });