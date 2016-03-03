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

var navigationHelper = rhsTestTools.helpers.navigation;
var specHelper = rhsTestTools.helpers.spec;
var startPage = rhsTestTools.pages.startPage;
var aboutPage = rhsTestTools.pages.aboutPage;
var sjukfallPage = rhsTestTools.pages.sjukfallPage;

describe('Logga in som Lena Karlsson ', function() {

    beforeEach(function() {
        browser.ignoreSynchronization = false;
        specHelper.login('IFV1239877878-104N_IFV1239877878-1045');

        console.log('beforeEach');
    });

    it('Öppna och stäng byt enhetsdialogen', function() {
        console.log('Steg 1');
        startPage.clickBytVardenhet();

        console.log('Öppnat vårdenehet');
        expect(startPage.closeBytVardenhet().isPresent()).toBeTruthy();

        console.log('expect');
        startPage.clickCloseBytVardenhet();

        console.log('close');
        expect(startPage.closeBytVardenhet().isPresent()).toBeFalsy();

        console.log('done');
    });

    it('Byt enhet på startsidan', function() {
        bytEnhet();
    });

    it('Byt enhet på om sidan', function() {
        navigationHelper.goToAbout();

        bytEnhet();

        expect(aboutPage.isAt()).toBeTruthy();
    });

    it('Byt enhet på sjukfallssidan', function() {
        startPage.clickFullUnit();
        expect(sjukfallPage.isAt()).toBeTruthy();

        bytEnhet();

        expect(sjukfallPage.isAt()).toBeTruthy();
    });


    function bytEnhet() {
        var today = new Date().toISOString().split('T')[0];

        expect(startPage.navbarLocation().getText()).toBe(today + ' - WebCert-Vårdgivare2 - WebCert-Enhet2');

        startPage.clickBytVardenhet();
        element(by.id('select-active-unit-IFV1239877878-104D-modal')).click();

        expect(startPage.navbarLocation().getText()).toBe(today + ' - WebCert-Vårdgivare2 - WebCert-Enhet3');
    }


   /* afterEach(function() {
        console.log('logout');
        specHelper.logout();
    })*/
});
