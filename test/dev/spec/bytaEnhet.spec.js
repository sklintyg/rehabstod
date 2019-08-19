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

var navigationHelper = rhsTestTools.helpers.navigation;
var specHelper = rhsTestTools.helpers.spec;
var startPage = rhsTestTools.pages.startPage;
var aboutPage = rhsTestTools.pages.aboutPage;
var sjukfallPage = rhsTestTools.pages.sjukfallPage;

describe('Logga in som Kerstin Johansson', function() {

  beforeEach(function() {
    specHelper.login('TSTNMT2321000156-105W_TSTNMT2321000156-105P', 'TSTNMT2321000156-105P');
  });

  it('Öppna och stäng byt enhetsdialogen', function() {
    startPage.clickBytVardenhet();

    expect(startPage.closeBytVardenhet().isPresent()).toBeTruthy();

    startPage.clickCloseBytVardenhet();

    expect(startPage.closeBytVardenhet().isPresent()).toBeFalsy();
  });

  it('Byt enhet på startsidan', function() {
    bytEnhet();
  });

  it('Byt enhet på sjukfallssidan', function() {
    startPage.clickFullUnit();
    expect(sjukfallPage.isAt()).toBeTruthy();

    bytEnhet();

    expect(sjukfallPage.isAt()).toBeTruthy();
  });

  it('Byt enhet på sjukfallssidan filtertåterställs', function() {
    startPage.clickFullUnit();
    expect(sjukfallPage.isAt()).toBeTruthy();

    var freeTextValue = 'Hej';

    sjukfallPage.freeTextInput().sendKeys(freeTextValue);

    expect(sjukfallPage.freeTextInput().getAttribute('value')).toEqual(freeTextValue);

    bytEnhet();

    expect(sjukfallPage.isAt()).toBeTruthy();
    expect(sjukfallPage.freeTextInput().getAttribute('value')).toEqual('');
  });

  function bytEnhet() {
    expect(startPage.navbarVardgivare().getText()).toBe('Rehabstöd Vårdgivare 1');
    expect(startPage.navbarVardenhet().getText()).toBe('Rehabstöd Enhet 2');

    startPage.clickBytVardenhet();
    element(by.id('rhs-vardenhet-selector-select-active-unit-TSTNMT2321000156-105Q-link')).click();

    expect(startPage.navbarVardgivare().getText()).toBe('Rehabstöd Vårdgivare 1');
    expect(startPage.navbarVardenhet().getText()).toBe('Rehabstöd Enhet 3');
  }

  /* afterEach(function() {
       console.log('logout');
       specHelper.logout();
   })*/
});
