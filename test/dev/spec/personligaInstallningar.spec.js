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

var specHelper = rhsTestTools.helpers.spec;
var startPage = rhsTestTools.pages.startPage;
var welcomePage = rhsTestTools.pages.welcomePage;
var settings = rhsTestTools.pages.settings;
var restUtil = rhsTestTools.utils.restUtil;

describe('Ändra inställningar som Kerstin Johansson', function() {

  function resetDefaultUnit() {
    welcomePage.get();
    specHelper.waitForAngularTestability();
    specHelper.setCookieConsentBannerState('true');
    welcomePage.login('TSTNMT2321000156-105W_TSTNMT2321000156-105P');
    specHelper.waitForAngularTestability();

    restUtil.setPreferences({'standardenhet': null});
  }

  beforeEach(function() {
    resetDefaultUnit();
  });
  afterEach(function() {
    resetDefaultUnit();
  });

  describe('Hantering av standarenhet vid inloggning', function() {

    it('Välj en standardenhet i Personliga inställningar', function() {

      //On initial login - a manual unit selection must be made
      specHelper.login('TSTNMT2321000156-105W_TSTNMT2321000156-105P', 'TSTNMT2321000156-105P');
      startPage.openSettings();
      expect(settings.isOpen()).toBeTruthy();
      expect(settings.getCurrentStandardUnitName().getText()).toEqual('Ingen standardenhet');

      //Save a default login unit selection
      settings.setCurrentStandardUnit('TSTNMT2321000156-105Q');
      expect(settings.getCurrentStandardUnitName().getText()).toEqual('Rehabstöd Enhet 3');
      settings.clickSave();
      expect(settings.isOpen()).toBeFalsy();

      specHelper.logout();

      //When logging in again, we should get directly to the start page on the selected unit
      specHelper.login('TSTNMT2321000156-105W_TSTNMT2321000156-105P');
      expect(startPage.isAt()).toBe(true);
      expect(startPage.navbarVardenhet().getText()).toBe('Rehabstöd Enhet 3');

    });

  });
});
