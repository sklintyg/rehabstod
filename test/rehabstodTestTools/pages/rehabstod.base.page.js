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

/**
 * Created by bennysce on 17-12-15.
 */
/*globals browser*/
'use strict';

var Class = require('jclass');

/**
 * Elements always shown in rehabstod are connected here. Header etc.
 */
var RehabstodBasePage = Class._extend({
    init: function() {
        this.at = null;
        this.doctor = element(by.css('.logged-in'));
        // this.header = element(by.id('wcHeader'));
        this.header = element(by.css('.headerbox-user-profile'));
        this.navBar = element(by.css('.navbar-header'));
        this.closeBytVardenhet = element(by.id('select-care-unit-close'));
    },
    isAt: function() {
        return this.at.isDisplayed();
    },
    getPage: function(page) {
        browser.get('app.html/#/' + page);
    },
    clickBytVardenhet: function() {
        element(by.id('rhs-header-other-locations')).click();
    },
    clickCloseBytVardenhet: function() {
        this.closeBytVardenhet.click();
    }
});

module.exports = RehabstodBasePage;