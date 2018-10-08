/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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
 * DateOpenService
 * Tracks open datepickers
 *
 * Created by benny on 03/11/16.
 */
angular.module('rehabstodApp').service('datePickerOpenService', function() {
    'use strict';
    this.openDatePicker = null;

    this.update = function(toggledDatePickerState) {

        // Close last opened datepicker
        if(toggledDatePickerState.isOpen === true){

            if(this.openDatePicker && toggledDatePickerState !== this.openDatePicker){
                this.openDatePicker.isOpen = false;
                this.openDatePicker = null;
            }

            // Set this one as the last opened datepicker
            if(this.openDatePicker === null){
                this.openDatePicker = toggledDatePickerState;
            }

        } else {

            if(toggledDatePickerState.isOpen === false && toggledDatePickerState === this.openDatePicker){
                this.openDatePicker = null;
            }

        }

    };
});
