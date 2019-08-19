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

var errorPage = rhsTestTools.pages.errorPage;
var specHelper = rhsTestTools.helpers.spec;
var startPage = rhsTestTools.pages.startPage;
var WelcomePage = rhsTestTools.pages.welcomePage;

describe('Logga in som Pelle Uppdragslös ', function() {

  beforeEach(function() {
    browser.ignoreSynchronization = false;
  });

  it('Öppna och stäng byt enhetsdialogen', function() {
    WelcomePage.get();
    specHelper.waitForAngularTestability();
    WelcomePage.login('pelle-uppdragslos_');
    specHelper.waitForAngularTestability();
    expect(errorPage.isAt()).toBeTruthy();
    expect(errorPage.isMedarbetaruppdragSaknasShowing()).toBeTruthy();
  });

});
