/* global module, require */
var baseConfig = require('./karma.conf.js');

module.exports = function(config) {
    'use strict';

    // Load base config
    baseConfig(config);

    // Override base config
    config.set({
        autoWatch: false,
        logLevel: config.LOG_ERROR,
        singleRun: true,

        browsers: [ 'PhantomJS' ],

        // coverage reporter generates the coverage
        reporters: ['progress', 'coverage'],

        coverageReporter: {
            type : 'lcovonly',
            dir : 'build/karma/coverage/lcov',
            subdir: '.'
        }
    });
};
