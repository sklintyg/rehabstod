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

/*globals browser */
/*globals pages */
/*globals describe,it,helpers */
'use strict';

var errorPage = rhsTestTools.pages.errorPage;
var specHelper = rhsTestTools.helpers.spec;
var startPage = rhsTestTools.pages.startPage;
var WelcomePage = rhsTestTools.pages.welcomePage;

describe('Logga in som Hans Hosplösa ', function() {

  beforeEach(function() {
    browser.ignoreSynchronization = false;
  });

  it('Öppna och försök logga in som Hans Hosplösa', function() {
    var userJson = {
      forNamn: 'Höns',
      efterNamn: 'Hosplösa',
      hsaId: 'höns-hosplosa',
      enhetId: 'TSTNMT2321000156-105F',
      forskrivarKod: '9300005'
    };

    WelcomePage.get();
    specHelper.waitForAngularTestability();
    WelcomePage.loginByJSON(JSON.stringify(userJson));
    specHelper.waitForAngularTestability();
    expect(errorPage.isAt()).toBeTruthy();
    expect(errorPage.isTeknisktFelShowing()).toBeTruthy();
    expect(errorPage.isMedarbetaruppdragSaknasShowing()).toBeFalsy();
  });

});
