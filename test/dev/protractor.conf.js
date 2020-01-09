/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
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

// conf.js
/*globals browser,global,exports,process*/
/**
 * Setup :
 * <rehabstod/test/> : npm install
 *
 * To run tests :
 * <rehabstod/test/> : grunt
 *
 **/
'use strict';
var HtmlScreenshotReporter = require('protractor-jasmine2-screenshot-reporter');

var screenshotReporter = new HtmlScreenshotReporter({
  dest: 'reports/',
  filename: 'index.html',
  ignoreSkippedSpecs: true,
  captureOnlyFailedSpecs: true
});

exports.config = {
  directConnect: true,
  //seleniumAddress: require('./../rehabstodTestTools/environment.js').envConfig.SELENIUM_ADDRESS,
  baseUrl: require('./../rehabstodTestTools/environment.js').envConfig.REHABSTOD_URL,

  specs: ['./spec/*.spec.js'],

  suites: {
    app: ['./spec/*.spec.js']
  },

  // Capabilities to be passed to the webdriver instance. (ignored if multiCapabilities is used)
  capabilities: {
    browserName: 'chrome', // possible values: phantomjs, firefox, chrome
    acceptInsecureCerts: true,
    chromeOptions: {
      args: ["--no-sandbox", "--headless", "--disable-gpu", "--window-size=1600x1024"]
    }
  },

  framework: 'jasmine',
  jasmineNodeOpts: {
    // If true, print colors to the terminal.
    showColors: true,
    // Default time to wait in ms before a test fails.
    defaultTimeoutInterval: 30000
  },

  // Setup the report before any tests start
  beforeLaunch: function() {
    return new Promise(function(resolve) {
      screenshotReporter.beforeLaunch(resolve);
    });
  },

  onPrepare: function() {
    // implicit and page load timeouts
    //browser.manage().timeouts().pageLoadTimeout(40000);
    //browser.manage().timeouts().implicitlyWait(25000);

    browser.ignoreSynchronization = false;

    // We need a certain size of the window to make sure everything is visible
    browser.manage().window().setSize(1600, 1024);

    global.rhsTestTools = require('rehabstod-testtools');

    global.logg = function(text) {
      console.log(text);
    };

    var reporters = require('jasmine-reporters');
    jasmine.getEnv().addReporter(
        new reporters.JUnitXmlReporter({
          savePath: 'reports/',
          filePrefix: 'junit',
          consolidateAll: true
        }));

    jasmine.getEnv().addReporter(screenshotReporter);

    var disableNgAnimate = function() {
      angular.module('disableNgAnimate', []).run(['$animate', function($animate) {
        console.log('Animations are disabled');
        $animate.enabled(false);
      }]);
    };

    var disableCssAnimate = function() {
      angular
      .module('disableCssAnimate', [])
      .run(function() {
        var style = document.createElement('style');
        style.type = 'text/css';
        style.innerHTML = '* {' +
            '-webkit-transition: none !important;' +
            '-moz-transition: none !important' +
            '-o-transition: none !important' +
            '-ms-transition: none !important' +
            'transition: none !important' +
            '}';
        document.getElementsByTagName('head')[0].appendChild(style);
      });
    };

    browser.addMockModule('disableNgAnimate', disableNgAnimate);
    browser.addMockModule('disableCssAnimate', disableCssAnimate);
  },

  // Close the report after all tests finish
  afterLaunch: function(exitCode) {
    return new Promise(function(resolve) {
      screenshotReporter.afterLaunch(resolve.bind(this, exitCode));
    });
  }
};
