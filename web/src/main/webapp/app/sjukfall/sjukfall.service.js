angular.module('rehabstodApp').factory('SjukfallService',
    function($log, $q) {
        'use strict';

        function _loadSjukfallTemp() {
            var
                nameList = ['Pierre', 'Pol', 'Jacques', 'Robert', 'Elisa'],
                familyName = ['Dupont', 'Germain', 'Delcourt', 'bjip', 'Menez'],
                diagnosName = ['F 3.21', 'M', 'F 3.22', '-'],
                lakareName = ['Mästerkatten', 'Dupont', 'Germain', 'Delcourt', 'bjip', 'Menez'],
                grader = [100, 50, 25];

            function createRandomItem() {
                var
                    firstName = nameList[Math.floor(Math.random() * 5)],
                    lastName = familyName[Math.floor(Math.random() * 5)],
                    lakare = lakareName[Math.floor(Math.random() * 6)],
                    diagnos = diagnosName[Math.floor(Math.random() * 4)],
                    langd = 1 + Math.floor(Math.random() * 400),
                    dag = Math.floor(Math.random() * 30) + 1,
                    grad = grader[Math.floor(Math.random() * 3)];

                if (dag < 10) {
                    dag = '0' + dag;
                }

                return {
                    patient :{
                        id: '19700123-9297',
                        alder: 12,
                        kon: 'f',
                        namn: firstName + ' ' + lastName
                    },
                    diagnos : {
                        original: diagnos,
                        grupp: null,
                        kod: null
                    },
                    vgstart: '2015-09-' + dag,
                    vestart: '2015-10-' + dag,
                    slut: '2015-12-15',
                    dagar: langd,
                    intyg: Math.floor(Math.random() * 5) + 1,
                    grader: grader,
                    aktivgrad: grad,
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