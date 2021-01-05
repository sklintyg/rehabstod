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
var sjukfallPage = rhsTestTools.pages.sjukfallPage;

describe('Hantera tabellen', function() {

  beforeAll(function() {
    browser.ignoreSynchronization = false;
    specHelper.login();
    startPage.clickMyUnit();
    expect(sjukfallPage.isAt()).toBe(true);
  });

  it('Sortera på personnummer', function() {
    sjukfallPage.tableHeader.element(by.css('th.column-patient-id')).click();

    var columnPatientValueFirst = sjukfallPage.tableBody.all(by.css('.column-patient-id')).first().getText();
    var columnNumberValueFirst = sjukfallPage.tableBody.all(by.css('.column-number')).first().getText();

    expect(columnPatientValueFirst).toBeDefined();
    expect(columnNumberValueFirst).toBe('1');

    browser.executeScript('window.scrollTo(0,0);').then(function() {
      sjukfallPage.tableHeader.element(by.css('th.column-patient-id')).click();

      var afterSort = sjukfallPage.tableBody.all(by.css('.column-patient-id')).first().getText();
      //After sorting twice, the first row should have changed..
      expect(afterSort).not.toBe(columnPatientValueFirst);

    });

    browser.executeScript('window.scrollTo(0,0);').then(function() {
      sjukfallPage.tableHeader.element(by.css('th.column-patient-id')).click();

      var afterSort = sjukfallPage.tableBody.all(by.css('.column-patient-id')).first().getText();
      //After sorting again, the first row should be the same as after initial sort
      expect(afterSort).toBe(columnPatientValueFirst);

    });
  });

  it('anpassa tabellen', function() {
    sjukfallPage.anpassaTableLink.click();
    sjukfallPage.toggleColumn('days');
    sjukfallPage.anpassaTableSaveBtn.click();

    expect(sjukfallPage.langdFromInput().isEnabled()).toBeFalsy();
    expect(sjukfallPage.anpassaTableTooltip.isPresent()).toBeTruthy();
    expect(sjukfallPage.getSjukfallRowColumn(0, 'days').isPresent()).toBeFalsy();
  });

  it('återställ tabellen', function() {
    sjukfallPage.anpassaTableLink.click();
    sjukfallPage.toggleColumn('days');
    sjukfallPage.anpassaTableSaveBtn.click();

    expect(sjukfallPage.langdFromInput().isEnabled()).toBeTruthy();
    expect(sjukfallPage.anpassaTableTooltip.isPresent()).toBeFalsy(); // tar bort checken tills en återställknapp finns
    expect(sjukfallPage.getSjukfallRowColumn(0, 'days').isPresent()).toBeTruthy();
  });

});
