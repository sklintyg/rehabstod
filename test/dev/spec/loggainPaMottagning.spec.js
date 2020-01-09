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

/*globals browser */
/*globals pages */
/*globals describe,it,helpers */
'use strict';

var specHelper = rhsTestTools.helpers.spec;
var startPage = rhsTestTools.pages.startPage;
var selectUnitPage = rhsTestTools.pages.selectUnitPage;

describe('Flöde som rehabkoordinator - loggar in på mottagning', function() {

  // Logga in utan att välja enhet
  beforeEach(function() {
    browser.ignoreSynchronization = false;
    specHelper.loginToSelectionPage('eva_centrum-vast');  // Eva Holgersson - utan att välja enhet
  });
  describe('Välj mottagning och verifiera att unit kontexten innehåller mottagingnsnamnet', function() {

    // Gör urval
    beforeEach(function() {
      selectUnitPage.expandUnit('centrum-vast');
      selectUnitPage.selectUnit('akuten');
      expect(startPage.isAt()).toBeTruthy();
    });

    it('skall ha med mottagning i unit kontext', function() {
      expect(startPage.navbarVardgivare().getText()).toBe('Landstinget Västmanland');
      expect(startPage.navbarVardenhet().getText()).toBe('Vårdcentrum i Väst - Akuten');
    });

  });
});
