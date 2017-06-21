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

/*globals browser */
/*globals pages */
/*globals describe,it,helpers */
'use strict';

var specHelper = rhsTestTools.helpers.spec;
var navigationHelper = rhsTestTools.helpers.navigation;
var startPage = rhsTestTools.pages.startPage;
var sjukfallPage = rhsTestTools.pages.sjukfallPage;
var aboutPage = rhsTestTools.pages.aboutPage;

describe('Flöde som rehabkoordinator', function() {

    // Logga in
    beforeEach(function() {
        browser.ignoreSynchronization = true;
        specHelper.login('TSTNMT2321000156-105W_TSTNMT2321000156-105P', 'TSTNMT2321000156-105P'); // Kerstin Johansson
    });

    it('Bara se knappen alla sjukfall', function() {
        expect(startPage.fullUnit.isPresent()).toBeTruthy();
        expect(startPage.myUnit.isPresent()).toBeFalsy();
    });

    it('Gå till om rehab', function() {
        navigationHelper.goToAbout();
    });

    describe('gör urval', function() {

        beforeEach(function() {
            startPage.clickFullUnit();
            expect(sjukfallPage.isAt()).toBeTruthy();
            expect(sjukfallPage.lakareFilter.isPresent()).toBeTruthy();
        });

        it('gå till om rehab och gå tillbaka till sjukfall', function() {
            // Gå till about
            navigationHelper.goToAbout();

            // Gå till sjukfall
            navigationHelper.goToSjukfall();
        });
    });

    describe('utan urval', function() {
        it('Gå direkt till sjukfall', function() {
            sjukfallPage.get();
            specHelper.waitForAngularTestability();
            expect(sjukfallPage.isAt()).toBeTruthy();
        });

        it('Gå till om och sedan till sjukfall', function() {
            // Gå till about
            navigationHelper.goToAbout();

            // Gå till sjukfall
            navigationHelper.goToSjukfall();
        });
    });

    describe('gör urval och går tillbaka till start', function() {

        // Gör urval
        beforeEach(function() {
            startPage.clickFullUnit();
            expect(sjukfallPage.isAt()).toBeTruthy();

            navigationHelper.goBackToStart();
        });

        it('Gå tillbaka till start och sedan försöka gå in igen utan att göra ett urval', function() {
            // Gå till sjukfall
            sjukfallPage.get();
            specHelper.waitForAngularTestability();
            expect(sjukfallPage.isAt()).toBeTruthy();
        });

        it('Gå tillbaka till start och sedan about och tillbaka och hamna på start.', function() {
            // Gå till about
            navigationHelper.goToAbout();

            // Gå till start
            navigationHelper.goToStart();
        });
    });


    // Logga ut
    /*afterEach(function() {
        specHelper.logout();
    });*/

});