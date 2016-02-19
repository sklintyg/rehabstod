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

/* globals pages*/
/* globals logg, browser, JSON */

'use strict';

// var webcertBasePage = pages.webcertBase;
// var webcertBase = pages.webcertBase;

module.exports = function() {

    this.Given(/^att jag är inloggad som en användare$/, function(callback) {
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
    // Fattigmans-kloning av användar-hashen.
    global.user = JSON.parse(JSON.stringify(userObj));
    global.user.role = newUserRole || roleName;
    global.user.origin = newOrigin || 'NORMAL';
    browser.ignoreSynchronization = true;
    pages.welcomePage.get();
    pages.welcomePage.loginByJSON(JSON.stringify(userObj));

    browser.ignoreSynchronization = false;
    browser.sleep(2000);
    // webcertBasePage.header.getText()
    // expect(element(by.id('wcHeader')).getText()).to.eventually.contain(roleName + ' - ' + userObj.fornamn + ' ' + userObj.efternamn)
    // expect(webcertBase.header.getText()).to.eventually.contain(roleName + ' - ' + userObj.fornamn+ ' ' + userObj.efternamn)
    // .and.notify(callback);
}

function checkUserRole() {
    return performUserCheck('role');
}

function checkUserOrigin() {
    return performUserCheck('origin');
}

function performUserCheck(userconfig) {
    browser.ignoreSynchronization = true;
    if (userconfig === 'role') {
        browser.get('testability/user/role/');
    } else if (userconfig === 'origin') {
        browser.get('testability/user/origin/');
    }
    var attribute = element(by.css('pre')).getText();
    browser.navigate().back();
    browser.sleep(1000);
    browser.ignoreSynchronization = false;
    return attribute;
}