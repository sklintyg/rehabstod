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
/*globals Promise*/

'use strict';

module.exports = function() {

  this.Given(/^jag anger "(\d+)" i fritextfältet$/, function(fritext) {
    return element(by.id('rhs-filter-free-text-input')).sendKeys(fritext);
  });

  this.Given(/^ska det endast visas rader med '(\d+)' i texten$/, function(fritext) {

    var tabs = element.all(by.css('tr.rhs-table-row')).map(function(elm) {
      return elm.getText();
    });

    tabs.then(function(result) {
      var promiseArr = [];
      for (var i = 0; i < result.length; i++) {
        promiseArr.push(expect(result[i]).to.eventually.contain(fritext));
      }
      return Promise.all(promiseArr);

    });
  });

};
