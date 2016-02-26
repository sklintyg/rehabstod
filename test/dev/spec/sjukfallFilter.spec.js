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

/*globals pages */
/*globals describe,it,helpers */
'use strict';

var specHelper = rhsTestTools.helpers.spec;

var startPage = rhsTestTools.pages.startPage;
var sjukfallPage = rhsTestTools.pages.sjukfallPage;

describe('Hantera filtret', function() {


    beforeEach(function() {
        browser.ignoreSynchronization = false;
        specHelper.login();
        startPage.clickFullUnit();
        expect(sjukfallPage.isAt()).toBe(true);
    });

    it('Hide filter', function() {
        expect(sjukfallPage.resetButton().isPresent()).toBeTruthy();
        expect(sjukfallPage.settingsButton().isPresent()).toBeTruthy();

        sjukfallPage.clickHideFilter();

        expect(sjukfallPage.resetButton().isPresent()).toBeFalsy();
        expect(sjukfallPage.settingsButton().isPresent()).toBeFalsy();
    });

    it('Reset filter', function() {
        var freeTextValue = 'Hej';

        sjukfallPage.freeTextInput().sendKeys(freeTextValue);

        expect(sjukfallPage.freeTextInput().getAttribute('value')).toEqual(freeTextValue);

        sjukfallPage.clickResetFilter();

        expect(sjukfallPage.freeTextInput().getAttribute('value')).toEqual('');
    });

    afterEach(function() {
        specHelper.logout();
    })

});
