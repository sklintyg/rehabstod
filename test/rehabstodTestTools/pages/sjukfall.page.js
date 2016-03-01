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

/* globals browser */

/**
 * Created by erik
 */

'use strict';

var RehabstodBasePage = require('./rehabstod.base.page.js');

var RehabstodSjukfallPage = RehabstodBasePage._extend({
    init: function init() {
        init._super.call(this);
        this.at = element(by.id('rhs-sjukfall-page'));
        this.backBtn = element(by.id('rhs-sjukfall-page-go-back'));
        this.lakareFilter = element(by.id('rhs-filter-lakare'));
        this.tableHeader = element(by.id('rhs-table-normal-header'));
        this.tableBody = element(by.id('rhs-table-body'));
    },
    get: function () {
        this.getPage('sjukfall');
    },
    isAt: function isAt() {
        return isAt._super.call(this);
    },
    resetButton: function() {
        return element(by.id('rhs-filter-reset-btn'));
    },
    settingsButton: function() {
        return element(by.id('rhs-settings-change-btn'));
    },
    freeTextInput: function() {
        return element(by.id('rhs-filter-free-text-input'));
    },
    clickResetFilter: function() {
        this.resetButton().click();
    },
    clickHideFilter: function() {
        element(by.id('rhs-filter-hide-btn')).click();
    },
    clickGoBack: function() {
        this.backBtn.click();
    }
});

module.exports = new RehabstodSjukfallPage();
