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

var startPage = rhsTestTools.pages.startPage;
var sjukfallPage = rhsTestTools.pages.sjukfallPage;

describe('Hantera tabellen', function() {


    beforeEach(function() {
        browser.ignoreSynchronization = false;
        specHelper.login();
        startPage.clickMyUnit();
        expect(sjukfallPage.isAt()).toBe(true);
    });

    it('Sortera på personnummer', function() {
        sjukfallPage.tableHeader.element(by.css('.column-patient-id')).click();

        var columnPatientValue = sjukfallPage.tableBody.all(by.css('.column-patient-id')).first().getText();
        var columnNumberValue = sjukfallPage.tableBody.all(by.css('.column-number')).first().getText();

        expect(columnPatientValue).toBe('19000207-9812 (116 år)');
        expect(columnNumberValue).toBe('1');

        sjukfallPage.tableHeader.element(by.css('.column-patient-id')).click();

        columnPatientValue = sjukfallPage.tableBody.all(by.css('.column-patient-id')).first().getText();
        columnNumberValue = sjukfallPage.tableBody.all(by.css('.column-number')).first().getText();

        expect(columnPatientValue).toBe('20050415-2398 (10 år)');
        expect(columnNumberValue).toBe('1');
    });


    /*afterEach(function() {
        specHelper.logout();
    })*/

});
