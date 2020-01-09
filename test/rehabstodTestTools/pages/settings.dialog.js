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

/* globals browser */

/**
 * Created by marced
 */

'use strict';

var RehabstodBasePage = require('./rehabstod.base.page.js');

var SettingsDialogBase = RehabstodBasePage._extend({
  init: function init() {
    init._super.call(this);
  },

  isOpen: function isOpen() {
    return element(by.id('rhs-settings-modal')).isPresent();
  },
  getCurrentStandardUnitName: function() {
    return element(by.id('rhs-select-unit-dropdown-btn-label'));
  },
  setCurrentStandardUnit: function(hsaId) {
    element(by.id('rhs-select-unit-dropdown-btn')).click().then(function() {
      element(by.id('rhs-select-unit-dropdown-link-' + hsaId)).click()
    });
  },
  clickSave: function() {
    element(by.id('rhs-settings-modal-save-btn')).click();
  },
  clickCancel: function() {
    element(by.id('rhs-settings-modal-close-btn')).click();
  }
});

module.exports = new SettingsDialogBase();
