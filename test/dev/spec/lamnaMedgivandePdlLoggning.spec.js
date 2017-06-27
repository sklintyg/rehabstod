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
var pdlConsentPage = rhsTestTools.pages.pdlConsentPage;
var restUtil = rhsTestTools.utils.restUtil;

describe('Flöde som läkare utan lämnat medgivande', function() {

    var sessionCookie;

    beforeEach(function() {
        browser.ignoreSynchronization = false;
    });

    describe('Gå till sjukfall', function() {
        it('Logga in', function() {
            specHelper.login('peter-enkel_'); // Peter Enkel (1 enhet). Skall EJ ha pdlConsentGiven=true
            restUtil.login({
                'forNamn': 'Peter',
                'efterNamn': 'Enkel',
                'hsaId': 'peter-enkel'
            });
            restUtil.removeConsent('peter-enkel');
        });

        it('Verifiera att PDL-dialogen visas', function() {

            startPage.clickSjukfall();
            expect(pdlConsentPage.isAt());
        });

        it('Verifiera att klick på avbryt-knappen tar ner dialogen.', function() {
            pdlConsentPage.cancelConsentBtn().click();
            expect(pdlConsentPage.isAt()).toBe(false);
        });

        it('Verifiera att klick på Visa pågående sjukfall-knappen visar PDL-dialogen.', function() {
            startPage.clickMyUnit();
            expect(pdlConsentPage.isAt());
        });

        it('Verifiera att knappen för att lämna medgivande är oklickbar.', function() {
            expect(pdlConsentPage.giveConsentBtn().isEnabled()).toBe(false);
            expect(pdlConsentPage.consentCheckbox().isSelected()).toBe(false);
        });

        it('Verifiera att knappen för att lämna medgivande är blir klickbar när checkboxen klickas i.', function() {
            pdlConsentPage.consentCheckbox().click();
            expect(pdlConsentPage.giveConsentBtn().isEnabled()).toBe(true);
        });

        it('Verifiera att vi kommer till sjukfallssidan vid klick på den nu aktiva knappen.', function() {
            pdlConsentPage.giveConsentBtn().sendKeys(protractor.Key.SPACE);
            expect(pdlConsentPage.isAt()).toBe(false);
            expect(sjukfallPage.isAt()).toBe(true);
        });

        it('Logga ut', function() {
            restUtil.login({
                'forNamn': 'Peter',
                'efterNamn': 'Enkel',
                'hsaId': 'peter-enkel'
            });
            restUtil.removeConsent('peter-enkel');
            specHelper.logout();
        });
    });

});
