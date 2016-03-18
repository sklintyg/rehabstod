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

angular.module('rehabstodApp').directive('rhsStatPanel',
    ['SjukfallSummaryModel', 'SjukfallSummaryProxy', 'UserModel', '$rootScope', 'pieChartBaseConfig', 'messageService',
        function(SjukfallSummaryModel, SjukfallSummaryProxy, UserModel, $rootScope, pieChartBaseConfig,
            messageService) {
            'use strict';

            return {
                restrict: 'E',
                replace: true,
                scope: {
                    vardenhet: '='
                },
                controller: function($scope) {

                    /**
                     * Private functions
                     */
                    $scope.model = SjukfallSummaryModel.get();

                    $scope.totalPaEnhetStatConfig = angular.merge(angular.copy(pieChartBaseConfig), {

                        title: {
                            text: null
                        },
                        colors: ['#57843B'],
                        tooltip: {
                            pointFormat: '{point.y} st'
                        },
                        legend: {
                            labelFormat: '{y} st',
                            symbolWidth: 0,
                            align: 'center',
                            verticalAlign: 'middle',
                            itemStyle: {
                                'color': '#FFFFFF',
                                'fontSize': '1.6em',
                                'fontWeight': 'bold'
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
                        colors: ['#EA8034', '#138391'],
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
                        tooltip: {
                            pointFormat: '{point.percentage:.1f}% <b>({point.y} st)</b> av alla<br/>' +
                            'pågående sjukfall tillhör diagnoskapitlet<br/>{point.id} {point.name}.'
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


                },
                templateUrl: 'components/commonDirectives/rhsStatPanel/rhsStatPanel.directive.html'
            };
        }]);
