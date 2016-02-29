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
/*globals logger*/
'use strict';

var specHelper = rhsTestTools.helpers.spec;
var _wp = rhsTestTools.pages.welcomePage;
var rehabstodBase = rhsTestTools.pages.rehabstodBase;

module.exports = function() {

    this.Given(/^synns all innehåll$/, function(callback) {
        element(by.css('.navbar-header')).isPresent().then(function() {
            logger.info('OK - Innehållet synns! : ');
        }, function(reason) {
            callback('FEL - Innehållet synns ej : ' + reason);
        }).then(callback);
    });



    this.Given(/^jag byter till flik "([^"]*)"$/, function(arg1, callback) {
        logger.info('Jag byter flik till: ' + arg1);
        // element(by.cssContainingText('.ng-binding.ng-scope', arg1)).sendKeys(protractor.Key.SPACE).then(callback);
        // element(by.cssContainingText('.nav.navbar-nav', arg1)).click().then(callback);
        // element(by.cssContainingText('.ng-binding.ng-scope', arg1)).click().then(callback);
        element.all(by.css('.nav.navbar-nav li')).then(function(items) {
            if (arg1 === 'Om Rehabstöd') {
                logger.info('Byter flik till: Om Rehabstöd');
                items[1].click();
            } else if (arg1 === 'Pågående sjukfall') {
                logger.info('Byter flik till: Pågående sjukfall');
                items[0].click();
            }
        }).then(callback);
    });

    this.Given(/^elementen "([^"]*)" synns$/, function(arg1, callback) {
        if (arg1 === 'Om Rehabstöd') {
            logger.info('Kontrollera att sidan innehåller : Om Rehabstöd (' + arg1 + ')');
            expect(element(by.cssContainingText('.container.ng-scope', arg1)).getText()).to.eventually.contain(arg1).then(function() {
                logger.info('OK - sidan innehåller rätt text');
            }, function(reason) {
                callback('FEL - fel Kontrollera att sidan innehåller: ' + reason);
            }).then(callback);
        } else if (arg1 === 'Pågående sjukfall') {
            logger.info('Kontrollera att sidan innehåller : Pågående sjukfall (' + arg1 + ')');
            expect(element(by.id('startPage')).getText()).to.eventually.contain(arg1).then(function() {
                logger.info('OK - sidan innehåller rätt text');
            }, function(reason) {
                callback('FEL - fel Kontrollera att sidan innehåller: ' + reason);
            }).then(callback);
        }


    });

    this.Given(/^loggas jag ut$/, function(callback) {
        expect(element(by.id('logoutLink')).isPresent()).to.become(true).then(function() {
            element(by.id('logoutLink')).sendKeys(protractor.Key.SPACE);
            logger.info('OK - Loggar ut');
        }, function(reason) {
            callback('FEL - fel Logga ut: ' + reason);
        }).then(callback);
    });

    this.Given(/^jag väljer "([^"]*)"$/, function(arg1, callback) {

        if (arg1 === 'Visa mina sjukfall') {
            element(by.id('rhs-startPage-myBtn')).sendKeys(protractor.Key.SPACE).then(callback);
        } else if (arg1 === 'Visa alla sjukfall') {
            element(by.id('rhs-startPage-allBtn')).sendKeys(protractor.Key.SPACE).then(callback);
        }
    });

};