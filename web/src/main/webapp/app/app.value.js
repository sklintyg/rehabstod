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

angular.module('rehabstodApp').value('networkConfig', {
    defaultTimeout: 30000
});

/*
 * Base config for all pice chars rendered in the app. Can be overridden and extended.
 */
angular.module('rehabstodApp').value('pieChartBaseConfig', {

    colors: ['#E11964',
        '#032C53',
        '#FFBA3E',
        '#799745',
        '#3CA3FF',
        '#C37EB2',
        '#2A5152',
        '#FB7F4D',
        '#5CC2BC',
        '#704F38'],
    chart: {
        type: 'pie'
    },
    exporting: {
        enabled: false
    },
    credits: {
        enabled: false
    },
    tooltip: {
        headerFormat: ''
    },
    plotOptions: {
        pie: {
            allowPointSelect: false,
            cursor: 'default',
            showInLegend: true,
            dataLabels: {
                enabled: false
            },
            size: 100,
            center: ['50%', '50%']

        },
        series: {
            cursor: 'default',
            point: {
                events: {
                    legendItemClick: function(event) {
                        'use strict';
                        event.preventDefault();
                        return false;
                    }
                }
            },
            states: {
                hover: {
                    halo: {
                        size: 5
                    }
                }
            }
        }
    },


    series: [{
        data: []
    }]

});