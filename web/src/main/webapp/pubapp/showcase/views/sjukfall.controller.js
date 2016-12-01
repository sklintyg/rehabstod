/*
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

angular.module('showcase').controller('showcase.SjukfallCtrl',
        [ '$scope', '$window', 'SjukfallFilterViewState', function($scope, $window, SjukfallFilterViewState) {
            'use strict';

            $scope.show = true;
            $scope.text = 'Text';
            $scope.showSingle = true;
            $scope.loading = true;
            $scope.registerForm = {};
            $scope.registerModel = {};

            $scope.filterViewState = SjukfallFilterViewState;
            $scope.sjukfallsLangder = angular.copy($scope.filterViewState.get().sjukskrivningslangdModel);

            $scope.showCookieBanner = false;
            $scope.doShowCookieBanner = function() {
                $window.localStorage.setItem('pp-cookie-consent-given', '0');
                $scope.showCookieBanner = !$scope.showCookieBanner;
            };

            $scope.user = {
                'valdVardenhet': {id:null},
                'vardgivare': [ {
                    '@class': 'se.inera.intyg.infra.integration.hsa.model.Vardgivare',
                    'id': 'TSTNMT2321000156-105M',
                    'namn': 'Rehabstöd Vårdgivare 1',
                    'vardenheter': [ {
                        '@class': 'se.inera.intyg.infra.integration.hsa.model.Vardenhet',
                        'id': 'TSTNMT2321000156-105N',
                        'namn': 'Rehabstöd Enhet 1.1',
                        'epost': 'enhet1@rehabstod.invalid.se',
                        'postadress': 'Lasarettsgatan 1',
                        'postnummer': '12345',
                        'postort': 'Testhult',
                        'telefonnummer': '0101234567890',
                        'arbetsplatskod': '1234567890',
                        'agandeForm': 'OFFENTLIG',
                        'start': null,
                        'end': null,
                        'vardgivareHsaId': null,
                        'mottagningar': [

                        ]
                    }, {
                        '@class': 'se.inera.intyg.infra.integration.hsa.model.Vardenhet',
                        'id': 'TSTNMT2321000156-105P',
                        'namn': 'Rehabstöd Enhet 1.2',
                        'epost': 'enhet1@rehabstod.invalid.se',
                        'postadress': 'Lasarettsgatan 1',
                        'postnummer': '12345',
                        'postort': 'Testhult',
                        'telefonnummer': '0101234567890',
                        'arbetsplatskod': '1234567890',
                        'agandeForm': 'OFFENTLIG',
                        'start': null,
                        'end': null,
                        'vardgivareHsaId': null,
                        'mottagningar': [

                        ]
                    } ]
                },
                    {
                        '@class': 'se.inera.intyg.infra.integration.hsa.model.Vardgivare',
                        'id': 'TSTNMT2321000156-105M22',
                        'namn': 'Rehabstöd Vårdgivare 2',
                        'vardenheter': [ {
                            '@class': 'se.inera.intyg.infra.integration.hsa.model.Vardenhet',
                            'id': 'TSTNMT2321000156-105N22',
                            'namn': 'Rehabstöd Enhet 2.1',
                            'epost': 'enhet1@rehabstod.invalid.se',
                            'postadress': 'Lasarettsgatan 1',
                            'postnummer': '12345',
                            'postort': 'Testhult',
                            'telefonnummer': '0101234567890',
                            'arbetsplatskod': '1234567890',
                            'agandeForm': 'OFFENTLIG',
                            'start': null,
                            'end': null,
                            'vardgivareHsaId': null,
                            'mottagningar':[
                                {
                                    '@class':'se.inera.intyg.infra.integration.hsa.model.Mottagning',
                                    'id':'mottagning 1',
                                    'namn':'mottagning 1',
                                    'epost':null,
                                    'postadress':'',
                                    'postnummer':null,
                                    'postort':null,
                                    'telefonnummer':'',
                                    'arbetsplatskod':null,
                                    'agandeForm':'OFFENTLIG',
                                    'start':null,
                                    'end':null,
                                    'parentHsaId':'linkoping'
                                },
                                {
                                    '@class':'se.inera.intyg.infra.integration.hsa.model.Mottagning',
                                    'id':'mottagning 2',
                                    'namn':'mottagning 2',
                                    'epost':null,
                                    'postadress':'',
                                    'postnummer':null,
                                    'postort':null,
                                    'telefonnummer':'',
                                    'arbetsplatskod':null,
                                    'agandeForm':'OFFENTLIG',
                                    'start':null,
                                    'end':null,
                                    'parentHsaId':'linkoping'
                                }
                            ]
                        }, {
                            '@class': 'se.inera.intyg.infra.integration.hsa.model.Vardenhet',
                            'id': 'TSTNMT2321000156-105P22',
                            'namn': 'Rehabstöd Enhet 2.2',
                            'epost': 'enhet1@rehabstod.invalid.se',
                            'postadress': 'Lasarettsgatan 1',
                            'postnummer': '12345',
                            'postort': 'Testhult',
                            'telefonnummer': '0101234567890',
                            'arbetsplatskod': '1234567890',
                            'agandeForm': 'OFFENTLIG',
                            'start': null,
                            'end': null,
                            'vardgivareHsaId': null,
                            'mottagningar': [

                            ]
                        } ]
                    }]
            };

            $scope.onUnitSelected = function(enhet) {
                $scope.user.valdVardenhet.id = enhet.id;

            };

        } ]);
