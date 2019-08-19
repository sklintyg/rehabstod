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

describe('DatePickerOpenService', function() {
  'use strict';

  var pickerState;
  var DatePickerOpenService;

  beforeEach(angular.mock.module('rehabstodApp'), function(/*$provide*/) {
  });

  beforeEach(angular.mock.inject(['datePickerOpenService',
    function(_DatePickerOpenService_) {
      DatePickerOpenService = _DatePickerOpenService_;

      pickerState = {
        isOpen: false
      };
    }
  ]));

  describe('update', function() {

    it('can open new datepicker with no previous', function() {

      pickerState.isOpen = true;
      DatePickerOpenService.openDatePicker = null;

      DatePickerOpenService.update(pickerState);

      expect(DatePickerOpenService.openDatePicker).not.toBeNull();
      expect(DatePickerOpenService.openDatePicker.isOpen).toBeTruthy();
      expect(pickerState.isOpen).toBeTruthy();
    });

    it('can open new datepicker with previous', function() {

      pickerState.isOpen = true;
      var previousPickerState = {isOpen: true};
      DatePickerOpenService.openDatePicker = previousPickerState;

      DatePickerOpenService.update(pickerState);

      expect(DatePickerOpenService.openDatePicker).not.toBeNull();
      expect(previousPickerState.isOpen).toBeFalsy();
      expect(DatePickerOpenService.openDatePicker.isOpen).toBeTruthy();
      expect(pickerState.isOpen).toBeTruthy();
    });

    it('can close with same datepicker as previous', function() {

      pickerState.isOpen = false;
      DatePickerOpenService.openDatePicker = pickerState;

      DatePickerOpenService.update(pickerState);

      expect(DatePickerOpenService.openDatePicker).toBeNull();
      expect(pickerState.isOpen).toBeFalsy();
    });

    it('can open with same datepicker as previous', function() {

      pickerState.isOpen = true;
      DatePickerOpenService.openDatePicker = pickerState;

      DatePickerOpenService.update(pickerState);

      expect(DatePickerOpenService.openDatePicker).toBe(pickerState);
      expect(pickerState.isOpen).toBeTruthy();
    });

    it('can close datepicker without previous', function() {

      pickerState.isOpen = false;
      DatePickerOpenService.openDatePicker = null;

      DatePickerOpenService.update(pickerState);

      expect(DatePickerOpenService.openDatePicker).toBeNull();
      expect(pickerState.isOpen).toBeFalsy();
    });
  });
});
