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
/*globals describe,it,rhsTestTools */
'use strict';

var specHelper = rhsTestTools.helpers.spec;
var navigationHelper = rhsTestTools.helpers.navigation;
var startPage = rhsTestTools.pages.startPage;
var sjukfallPage = rhsTestTools.pages.sjukfallPage;

describe('Flöde som läkare', function() {

  // Logga in
  beforeEach(function() {
    specHelper.login();
  });

  it('Bara se knappen mina sjukfall', function() {
    expect(startPage.fullUnit.isPresent()).toBeFalsy();
    expect(startPage.myUnit.isPresent()).toBeTruthy();
  });

  describe('gör urval', function() {

    beforeEach(function() {
      startPage.clickMyUnit();
      expect(sjukfallPage.isAt()).toBeTruthy();
      expect(sjukfallPage.lakareFilter.isPresent()).toBeFalsy();
    });

    it('Ladda om sidan och var kvar på sjukfallssidan', function() {
      browser.refresh();

      expect(sjukfallPage.isAt()).toBeTruthy();
    });
  });

  describe('inget urval', function() {
    it('Gå direkt till sjukfall', function() {
      sjukfallPage.get();
      specHelper.waitForAngularTestability();
      expect(sjukfallPage.isAt()).toBeTruthy();
    });

  });

  describe('gör urval och går tillbaka till start', function() {

    // Gör urval
    beforeEach(function() {
      startPage.clickMyUnit();
      expect(sjukfallPage.isAt()).toBeTruthy();

      navigationHelper.goToStart();
    });

    it('Gå tillbaka till start och sedan försöka gå in igen utan att göra ett urval', function() {
      // Gå till sjukfall
      sjukfallPage.get();
      specHelper.waitForAngularTestability();
      expect(sjukfallPage.isAt()).toBeTruthy();
    });

  });

  // Logga ut
  /*afterEach(function() {
      specHelper.logout();
  });*/

});