/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
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

/* globals logger, browser, JSON,rhsTestTools */

'use strict';
var welcomePage = rhsTestTools.pages.welcomePage;

module.exports = function() {

  this.Given(/^att jag är inloggad som Läkare$/, function(callback) {
    var userObj = {
      fornamn: 'Jan',
      efternamn: 'Nilsson',
      hsaId: 'IFV1239877878-1049',
      enhetId: 'IFV1239877878-1042',
      lakare: true
    };
    logInAsUserRole(userObj, 'Läkare', callback);
  });
};

function logInAsUserRole(userObj, roleName, callback, newOrigin, newUserRole) {
  console.log('Loggar in som ' + userObj.fornamn + ' ' + userObj.efternamn + '..');

  global.user = JSON.parse(JSON.stringify(userObj));

  browser.ignoreSynchronization = true;
  welcomePage.get();
  welcomePage.loginByJSON(JSON.stringify(userObj));

  browser.ignoreSynchronization = false;
  browser.sleep(3000);
  expect(element(by.css('.headerbox-user-profile')).getText()).to.eventually.contain(
      roleName + ' - ' + userObj.fornamn + ' ' + userObj.efternamn).then(function(value) {
    logger.info('OK - hittade : ' + value);
  }, function(reason) {
    callback('FEL - err: ' + reason);
  }).then(callback);

}
