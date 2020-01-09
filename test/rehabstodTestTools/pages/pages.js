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

/**
 * Created by BESA on 2015-11-17.
 * Holds paths to page files for easy inclusion and intellisense support in specs.
 */
'use strict';

//var intygPath = './intyg/'; // should point to intyg folder

var rehabstodBase = require('./rehabstod.base.page.js');
var welcomePage = require('./welcome.page.js');
var selectUnitPage = require('./selectunit.page.js');
var startPage = require('./start.page.js');
var sjukfallPage = require('./sjukfall.page.js');
var aboutPage = require('./about.page.js');
var errorPage = require('./error.page.js');
var pdlConsentPage = require('./pdlconsent.page');
var settingsDialog = require('./settings.dialog');

module.exports = {
  'rehabstodBase': rehabstodBase,
  'welcomePage': welcomePage,
  'selectUnitPage': selectUnitPage,
  'startPage': startPage,
  'sjukfallPage': sjukfallPage,
  'aboutPage': aboutPage,
  'errorPage': errorPage,
  'pdlConsentPage': pdlConsentPage,
  'settings': settingsDialog
};
