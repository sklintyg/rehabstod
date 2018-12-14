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
var startPage = rhsTestTools.pages.startPage;
var sjukfallPage = rhsTestTools.pages.sjukfallPage;

fdescribe('Flöde som läkare', function() {

    // Logga in
    beforeEach(function() {
        specHelper.login();
        startPage.clickMyUnit();
        expect(sjukfallPage.isAt()).toBeTruthy();
    });

    describe('öppna patient', function() {
        it('verifiera intygslista', function() {
            sjukfallPage.clickTableRow(0);
            expect(sjukfallPage.patientModal.isDisplayed()).toBeTruthy();
            expect(sjukfallPage.patientSjukfallTable.isDisplayed()).toBeTruthy();
            expect(sjukfallPage.getPatientSjukfallRow(0, 0).isDisplayed()).toBeTruthy();
        });
    });

});
