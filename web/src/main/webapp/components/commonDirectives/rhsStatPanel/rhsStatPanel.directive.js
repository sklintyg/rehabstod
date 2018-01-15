/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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
angular.module('rehabstodApp').directive('rhsStatPanel',
        function(SjukfallSummaryModel, UserModel, $rootScope, pieChartBaseConfig,
            messageService, chartColors) {
            'use strict';

            return {
                restrict: 'E',
                scope: {
                    vardenhet: '='
                },
                controller: function($scope) {

                    /**
                     * Private functions
                     */
                    $scope.model = SjukfallSummaryModel.get();

                    $scope.totalPaEnhetStatConfig = angular.merge(angular.copy(pieChartBaseConfig), {

                        title: null,
                        colors: [chartColors.base],
                        tooltip: {
                            pointFormat: '{point.y} st'
                        },
                        legend: {
                            labelFormat: '{y} st',
                            symbolWidth: 0,
                            symbolPadding: 0,
                            symbolRadius: 0,
                            symbolHeight: 0,
                            align: 'center',
                            verticalAlign: 'middle',
                            itemStyle: {
                                'color': '#FFFFFF',
                                'fontSize': '1.6em',
                                'fontWeight': 'bold',
                                'cursor': 'default'
                            },
                            itemHoverStyle: {
                                color: '#FFFFFF'
                            },
                            floating: true
                        },
                        plotOptions: {
                            pie: {
                                borderColor: null,
                                borderWidth: 0,
                                allowPointSelect: false
                            }
                        }
                    });

                    var _getGender = function(code, toLower) {
                        var gender = '';
                        if (code === 'F') {
                            gender = messageService.getProperty('label.gender.female.plural');
                        } else {
                            gender = messageService.getProperty('label.gender.male.plural');
                        }
                        return toLower ? gender.toLocaleLowerCase() : gender;
                    };

                    //Gender stat config
                    $scope.genderStatConfig = angular.merge(angular.copy(pieChartBaseConfig), {

                        title: {
                            text: null
                        },
                        colors: [chartColors.female, chartColors.male],
                        tooltip: {
                            pointFormatter: function() {
                                return this.percentage.toFixed(1) + '% <b>(' + this.y +
                                    ' st)</b> av patienterna <br/>' +
                                    'i sjukfallen är ' + _getGender(this.name, true) + '.';
                            }

                        },
                        legend: {
                            labelFormatter: function() {
                                return _getGender(this.name, false) + ' <b>(' + this.y + ' st)</b>';
                            },
                            enabled: false
                        }
                    });

                    //Diagnose group stat config ------------------------------------------------
                    $scope.diagnoseStatConfig = angular.merge(angular.copy(pieChartBaseConfig), {

                        title: {
                            text: null
                        },
                        colors: getDiagnoseColors($scope.model.diagnoseGroupData),
                        tooltip: {
                            pointFormat: '{point.percentage:.1f}% <b>({point.y} st)</b> av ' +
                            (UserModel.get().isLakare ? 'mina' : 'alla') + ' pågående sjukfall<br/>' +
                            'tillhör {point.name}.'
                        },
                        legend: {
                            labelFormatter: function() {
                                var truncateAfter = 30;
                                var name = (this.name.length > truncateAfter) ?
                                this.name.substr(0, truncateAfter - 1) + '&hellip;' : this.name;
                                return this.id + (this.id === '' ? '' : ' ') + name + '&nbsp;<b>' + this.y +
                                    '</b>&nbsp;st';
                            },
                            enabled: false

                        }
                    });

                    function getDiagnoseColors(diagnosis) {
                        var colors = [];

                        angular.forEach(diagnosis, function(diagnos) {
                            colors.push(chartColors.diagnosis[diagnos.id]);
                        });

                        return colors;
                    }

                    //Sjukskrivningsgrad group stat config ------------------------------------------------
                    $scope.sickLeaveDegreeStatConfig = angular.merge(angular.copy(pieChartBaseConfig), {

                        title: {
                            text: null
                        },
                        colors: getSickLeaveDegreeColors($scope.model.sickLeaveDegreeGroupData),
                        tooltip: {
                            pointFormat: '{point.percentage:.1f}% <b>({point.y} st)</b> av ' +
                            (UserModel.get().isLakare ? 'mina' : 'alla') + ' pågående<br/> ' +
                            'sjukfall har en aktuell<br/>sjukskrivningsgrad på {point.name}.'
                        },
                        legend: {
                            labelFormatter: function() {
                                var truncateAfter = 30;
                                var name = (this.name.length > truncateAfter) ?
                                    this.name.substr(0, truncateAfter - 1) + '&hellip;' : this.name;
                                return name + '&nbsp;<b>' + this.y +
                                    '</b>&nbsp;st';
                            },
                            enabled: false

                        }
                    });

                    function getSickLeaveDegreeColors(sickLeaveDegrees) {
                        var colors = [];

                        angular.forEach(sickLeaveDegrees, function(sickLeaveDegree) {
                            colors.push(chartColors.sickLeaveDegrees[sickLeaveDegree.id]);
                        });

                        return colors;
                    }


                },
                templateUrl: '/components/commonDirectives/rhsStatPanel/rhsStatPanel.directive.html'
            };
        });
