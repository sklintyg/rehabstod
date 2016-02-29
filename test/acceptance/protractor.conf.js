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

/*globals browser,global,exports,process*/
'use strict';

exports.config = {

    allScriptsTimeout: 3000,
    // seleniumAddress: 'http://127.0.0.1:4444/wd/hub',
    framework: 'custom',

    // path relative to the current config file
    frameworkPath: require.resolve('protractor-cucumber-framework'),
    specs: [
        'features/*.feature'
    ],
    capabilities: {
        browserName: 'firefox',
        version: '',
        platform: 'ANY'
    },
    lmaxSessions: 1,
    cucumberOpts: {
        format: ['json:./acceptance/report/acc_results.json', 'pretty'],
        require: ['features/steps/**/*.js', 'features/support/**/*.js']
    },
    onPrepare: function() {

        browser.ignoreSynchronization = false;

        global.chai = require('chai');

        global.chaiAsPromised = require('chai-as-promised');
        global.chai.use(global.chaiAsPromised);

        global.expect = global.chai.expect;
        global.should = global.chai.should();

        global.rhsTestTools = require('rehabstod-testtools');

        browser.baseUrl = process.env.REHABSTOD_URL;
        console.log('process.env.REHABSTOD_URL: ' + process.env.REHABSTOD_URL);

        //Set window size
        browser.manage().window().setSize(1600, 1000);
        global.user = {};
        global.logger = new(winston.Logger)({
            transports: [
                new(winston.transports.Console)({
                    colorize: true,
                    timestamp: formatLocalDate,
                    formatter: function(options) {
                        // Return string will be passed to logger.
                        return options.timestamp() + ' ' + options.level.toUpperCase() + ' ' + (undefined !== options.message ? options.message : '') +
                            (options.meta && Object.keys(options.meta).length ? '\n\t' + JSON.stringify(options.meta) : '');
                    }
                })
            ]
        });

        process.env.NODE_TLS_REJECT_UNAUTHORIZED = '0';
    },
    formatLocalDate: function formatLocalDate() {
        var now = new Date(),
            tzo = -now.getTimezoneOffset(),
            dif = tzo >= 0 ? '+' : '-',
            pad = function(num) {
                var norm = Math.abs(Math.floor(num));
                return (norm < 10 ? '0' : '') + norm;
            };
        return now.getFullYear() + '-' + pad(now.getMonth() + 1) + '-' + pad(now.getDate()) + 'T' + pad(now.getHours()) + ':' + pad(now.getMinutes()) + ':' + pad(now.getSeconds()) + dif + pad(tzo / 60) + ':' + pad(tzo % 60);
    }
};