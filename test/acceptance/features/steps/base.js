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
/*globals logger,protractor*/
'use strict';

module.exports = function() {

  this.Given(/^syns allt innehåll$/, function(callback) {
    element(by.css('.navbar-header')).isPresent().then(function() {
      logger.info('OK - Innehållet syns! : ');
    }, function(reason) {
      callback('FEL - Innehållet syns ej : ' + reason);
    }).then(callback);
  });

  this.Given(/^jag byter till flik "([^"]*)"$/, function(arg1) {
    logger.info('Jag byter flik till: ' + arg1);
    return element.all(by.css('.nav.navbar-nav li')).then(function(items) {
      if (arg1 === 'Om Rehabstöd') {
        logger.info('Byter flik till: Om Rehabstöd');
        return items[1].click();
      } else if (arg1 === 'Pågående sjukfall') {
        logger.info('Byter flik till: Pågående sjukfall');
        return items[0].click();
      }
    });
  });

  this.Given(/^elementen "([^"]*)" syns$/, function(arg1) {
    if (arg1 === 'Om Rehabstöd') {
      logger.info('Kontrollera att sidan innehåller : Om Rehabstöd (' + arg1 + ')');
      return expect(element(by.cssContainingText('.container.ng-scope', arg1)).getText()).to.eventually.contain(arg1).then(function() {
        logger.info('OK - sidan innehåller rätt text');
      }, function(reason) {
        throw ('FEL - fel Kontrollera att sidan innehåller: ' + reason);
      });
    } else if (arg1 === 'Pågående sjukfall') {
      logger.info('Kontrollera att sidan innehåller : Pågående sjukfall (' + arg1 + ')');
      return expect(element(by.id('startPage')).getText()).to.eventually.contain(arg1).then(function() {
        logger.info('OK - sidan innehåller rätt text');
      }, function(reason) {
        throw ('FEL - fel Kontrollera att sidan innehåller: ' + reason);
      });
    }
  });

  this.Given(/^loggas jag ut$/, function() {
    return element(by.id('logoutLink')).sendKeys(protractor.Key.SPACE);
  });

  this.Given(/^jag väljer "([^"]*)"$/, function(arg1) {

    if (arg1 === 'Visa mina sjukfall') {
      return element(by.id('rhs-start-page-mine-btn')).sendKeys(protractor.Key.SPACE);
    } else if (arg1 === 'Visa alla sjukfall') {
      return element(by.id('rhs-startPage-allBtn')).sendKeys(protractor.Key.SPACE);
    }
  });

};
