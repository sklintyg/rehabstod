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
angular.module('rehabstodApp')
    .value('networkConfig', {
        defaultTimeout: 30000
    })
    .value('chartColors', {
        base: '#57843B',
        male: '#008391',
        female: '#EA8025',
        total: '#5D5D5D',
        diagnosis: {
            'A00-E90,G00-L99,N00-N99': '#E11964',
            'F00-F99': '#032C53',
            'M00-M99': '#FFBA3E',
            'O00-O99': '#799745',
            'P00-P96,Q00-Q99,S00-Y98': '#3CA3FF',
            'R00-R99': '#C37EB2',
            'Z00-Z99': '#2A5152',
            '': '#FB7F4D'
        },
        sickLeaveDegrees: {
            '25': '#E11964',
            '50': '#032C53',
            '75': '#FFBA3E',
            '100': '#799745'
        }
    })
    .value('moment', moment)
    .value('THROTTLE_MILLISECONDS', 300);


/*
 * Base config for all pice chars rendered in the app. Can be overridden and extended.
 */
angular.module('rehabstodApp').value('pieChartBaseConfig', {

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