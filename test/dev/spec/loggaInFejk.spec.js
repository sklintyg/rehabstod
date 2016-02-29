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

/*globals pages */
/*globals describe,it,helpers */
'use strict';

var specHelper = rhsTestTools.helpers.spec;
var startPage = rhsTestTools.pages.startPage;

describe('Logga in', function() {

    describe('Logga in som Jan Nilsson', function() {
        it('with user', function() {
            browser.ignoreSynchronization = false;
            specHelper.login();
            specHelper.logout();
        });
    });

    describe('Logga in som Lena Karlsson', function() {

        beforeEach(function() {
            browser.ignoreSynchronization = false;
            specHelper.login('IFV1239877878-104N_IFV1239877878-1045');

        });

        it('with user open and close select care unit', function() {
            browser.ignoreSynchronization = false;
            startPage.clickBytVardenhet();

            expect(startPage.closeBytVardenhet.isPresent()).toBeTruthy();

            startPage.clickCloseBytVardenhet();
        });

        it('with user open and change care unit', function() {
            browser.ignoreSynchronization = false;

            var today = new Date().toISOString().split('T')[0];

            expect(element(by.id('location')).getText()).toBe(today + ' - WebCert-Vårdgivare2 - WebCert-Enhet2');

            startPage.clickBytVardenhet();
            element(by.id('select-active-unit-IFV1239877878-104D-modal')).click();

            expect(element(by.id('location')).getText()).toBe(today + ' - WebCert-Vårdgivare2 - WebCert-Enhet3');
        });

        afterEach(function() {
            specHelper.logout();
        })
    });

});
