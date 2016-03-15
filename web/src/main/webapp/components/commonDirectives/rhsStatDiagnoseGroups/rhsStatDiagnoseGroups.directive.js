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
/* globals Highcharts */
angular.module('rehabstodApp').directive('rhsStatDiagnoseGroups',
    [
        function() {
            'use strict';

            return {
                restrict: 'E',
                replace: true,
                scope: {
                    data: '=',
                    title: '@'
                },
                templateUrl: 'components/commonDirectives/rhsStatDiagnoseGroups/rhsStatDiagnoseGroups.directive.html',
                link: function(scope, element) {
                    Highcharts.setOptions(
                        {
                            lang: {
                                noData: 'Inget att visa',
                                loading: 'Hämtar data...'
                            }
                        });

                    var chart = Highcharts.chart(element[0], {
                        colors: ['#E11964', '#032C53', '#FFBA3E', '#799745', '#3CA3FF', '#C37EB2', '#2A5152',
                            '#FB7F4D', '#5CC2BC', '#704F38'],
                        chart: {
                            type: 'pie'

                        },
                        exporting: {
                            enabled: false
                        },
                        credits: {
                            enabled: false
                        },
                        title: {
                            text: scope.title
                        },
                        tooltip: {
                            headerFormat: '',
                            pointFormat: '{point.percentage:.1f}% <b>({point.y} st)</b> av alla<br/>' +
                                         'pågående sjukfall tillhör diagnosgrupp<br/>{point.name}.'
                        },
                        legend: {
                            labelFormatter: function() {
                                var truncateAfter = 30;
                                var name = (this.name.length > truncateAfter) ?
                                this.name.substr(0, truncateAfter - 1) + '&hellip;' : this.name;
                                return this.id + ' ' + name + ' ' + this.y + ' st';
                            },
                            useHTML: true
                        },
                        plotOptions: {
                            pie: {
                                allowPointSelect: true,
                                cursor: 'pointer',
                                showInLegend: true,
                                dataLabels: {
                                    enabled: false
                                }
                            }
                        },
                        series: [{
                            data: scope.data
                        }]
                    });
                    chart.showLoading();

                    scope.$watchCollection('data', function(newData) {
                        if (newData) {
                            chart.hideLoading();
                            chart.series[0].setData(newData);
                            chart.reflow();
                        }
                    });
                }
            };
        }]);
