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

describe('DateUtilsService', function() {
    'use strict';

    var DateUtilsService;

    beforeEach(angular.mock.module('rehabstodApp'), function(/*$provide*/){

    });

    beforeEach(angular.mock.inject(['dateUtilsService',
        function(_DateUtilsService) {
            DateUtilsService = _DateUtilsService;
        }
    ]));

    describe('#isDate', function() {

        it ('check that isDate on 2015-01-05 is true', function () {
            var dateString = '2015-01-05';
            expect(DateUtilsService.isDate(dateString)).toBeTruthy();
        });

        it ('check that isDate on 2015-01-6666 is false', function () {
            var dateString = '2015-01-666';
            expect(DateUtilsService.isDate(dateString)).toBeFalsy();
        });

    });

    describe('#isYear', function() {

        it ('check that isYear on "2015" true', function () {
            var dateString = '2015';
            expect(DateUtilsService.isYear(dateString)).toBeTruthy();
        });

        it ('check that isYear on undefined is false', function () {
            var dateString;
            expect(DateUtilsService.isYear(dateString)).toBeFalsy();
        });
        it ('check that isYear on "aaa" is false', function () {
            var dateString = 'aaa';
            expect(DateUtilsService.isYear(dateString)).toBeFalsy();
        });

    });

    describe('#toMoment', function() {

        it ('check that a date can be converted to a moment date', function () {
            var date = new Date(2015, 1, 5, 11, 33, 30, 0);
            expect(DateUtilsService.toMoment(date)).not.toBe(null);
        });

        it ('check that we can use moment to formate a date', function () {
            var date = new Date(2015, 0, 5, 11, 33, 30, 0);
            var momentDate = moment(date);
            var expected = '2015 01 05';
            var result = momentDate.format('YYYY MM DD');
            expect(expected).toBe(result);
        });

    });

    describe('#convertDateToISOString', function() {

        it ('can convert a date to iso', function () {
            var date = new Date(2015, 0, 5, 11, 33, 30, 0);
            var result = DateUtilsService.convertDateToISOString(date);
            var expected = '2015-01-05';
            expect(expected).toBe(result);
        });

        it ('can convert a date with format YY M D', function () {
            var date = new Date(2015, 0, 5, 11, 33, 30, 0);
            var result = DateUtilsService.convertDateToISOString(date, 'YY M D');
            var expected = '15 1 5';
            expect(expected).toBe(result);
        });

    });

    describe('#convertDateStrict', function() {

        it ('can convert a date strictly', function () {
            var date = new Date(2015, 0, 5, 11, 33, 30, 0);
            var expected = DateUtilsService.toMoment(new Date(2015, 0, 5, 0, 0, 0, 0));
            var result = DateUtilsService.convertDateStrict(date);
            // only yyyy, mm and dd, should be able to test that secs is 0
            expect(result.isSame(expected));
        });

    });

    describe('#pushValidDate', function() {

        it ('can filter out invalid dates', function () {
            var date = '222222222';
            var list = [];
            DateUtilsService.pushValidDate(list, date);
            // only yyyy, mm and dd, should be able to test that secs is 0

            expect(list.length).toBe(0);
        });

        it ('can push a valid dates', function () {
            var date = '2015-01-05';
            var list = [];
            DateUtilsService.pushValidDate(list, date);

            DateUtilsService.pushValidDate(list, date);
            // only yyyy, mm and dd, should be able to test that secs is 0

            expect(list.length).toBe(2);
        });
    });

    describe('#areDatesWithinMonthRange', function() {

        it ('can check that dates are within month range', function () {
            var startDate = new Date(2014, 11, 5, 11, 33, 30, 0);
            var startMoment = DateUtilsService.toMoment(startDate);

            var endDate = new Date(2015, 4, 4, 11, 33, 30, 0);
            var endMoment = DateUtilsService.toMoment(endDate);

            expect(DateUtilsService.areDatesWithinMonthRange(startMoment, endMoment)).toBeTruthy();
        });
        it ('can check that dates are within month range - edge case', function () {
            var startDate = new Date(2014, 10, 5, 11, 33, 30, 0);
            var startMoment = DateUtilsService.toMoment(startDate);

            var endDate = new Date(2015, 4, 4, 11, 33, 30, 0);
            var endMoment = DateUtilsService.toMoment(endDate);

            expect(DateUtilsService.areDatesWithinMonthRange(startMoment, endMoment)).toBeTruthy();
        });

        it ('can check that dates are not within month range', function () {
            var startDate = new Date(2015, 0, 5, 11, 33, 30, 0);
            var startMoment = DateUtilsService.toMoment(startDate);

            var endDate = new Date(2015, 8, 4, 11, 33, 30, 0);
            var endMoment = DateUtilsService.toMoment(endDate);

            expect(DateUtilsService.areDatesWithinMonthRange(startMoment, endMoment)).toBeFalsy();
        });
        it ('can check that dates are not within month range - edge case', function () {
            var startDate = new Date(2014, 10, 5, 11, 33, 30, 0);
            var startMoment = DateUtilsService.toMoment(startDate);

            var endDate = new Date(2015, 4, 5, 11, 33, 30, 0);
            var endMoment = DateUtilsService.toMoment(endDate);

            expect(DateUtilsService.areDatesWithinMonthRange(startMoment, endMoment)).toBeFalsy();
        });
        it ('can check that invalid dates are considered within range', function () {

            var startMomentInvalid = null;
            var startDate = new Date(2014, 10, 5, 11, 33, 30, 0);
            var startMoment = DateUtilsService.toMoment(startDate);

            var endMomentInvalid = null;
            var endDate = new Date(2015, 4, 5, 11, 33, 30, 0);
            var endMoment = DateUtilsService.toMoment(endDate);

            expect(DateUtilsService.areDatesWithinMonthRange(startMoment, endMomentInvalid)).toBeTruthy();
            expect(DateUtilsService.areDatesWithinMonthRange(startMomentInvalid, endMoment)).toBeTruthy();
            expect(DateUtilsService.areDatesWithinMonthRange(startMomentInvalid, endMomentInvalid)).toBeTruthy();
        });

    });

    describe('#isDateOutOfRange', function() {

        it ('can check that a date is out of range', function () {
            var startMoment = moment();
            // bump the date by 7 months
            startMoment.add(7, 'months');

            expect(DateUtilsService.isDateOutOfRange(startMoment)).toBeTruthy();
        });

        it ('can check that a date is within 6 months ', function () {
            var startMoment = moment();
            // bump the date by 7 months
            startMoment.add(5, 'months');

            expect(DateUtilsService.isDateOutOfRange(startMoment)).toBeFalsy();
        });

    });

    describe('#isDateOutOfRange', function() {

        it ('can check that a date is out of range', function () {
            var startMoment = moment();
            // bump the date by 7 months
            startMoment.add(7, 'months');

            expect(DateUtilsService.isDateOutOfRange(startMoment)).toBeTruthy();
        });

        it ('can check that a date is longer than 6 months and more than one week', function () {
            var startMoment = moment();
            // bump the date by 7 months
            startMoment.add(7, 'months');

            expect(DateUtilsService.olderThanAWeek(startMoment) || DateUtilsService.isDateOutOfRange(startMoment)).toBeTruthy();
        });


        it ('can check that a date is less than a week and less than 6 months from now', function () {
            var startMoment = moment();
            startMoment.add(3, 'days');

            expect(DateUtilsService.olderThanAWeek(startMoment) || DateUtilsService.isDateOutOfRange(startMoment)).toBeFalsy();
        });

    });

    describe('#daysBetween', function() {

        it ('can check that we can get days between two dates', function () {
            var startDate = moment('2015-01-05');
            var startMoment = DateUtilsService.toMoment(startDate);

            var endDate = moment('2015-05-05');
            var endMoment = DateUtilsService.toMoment(endDate);
            var days = DateUtilsService.daysBetween(startMoment, endMoment);
            expect(days).toBe(120);
        });

        it ('can check that we get false if we provide no dates', function () {
            var days = DateUtilsService.daysBetween(null, null);
            expect(days).toBeFalsy();
        });


        it ('can check that we can get days between two dates, webcert-1721', function () {
            var startDate = moment('2015-04-21');
            var startMoment = DateUtilsService.toMoment(startDate);

            var endDate = moment('2015-05-22');
            var endMoment = DateUtilsService.toMoment(endDate);
            var days = DateUtilsService.daysBetween(startMoment, endMoment);
            expect(days).toBe(31);
        });

    });

    describe('#parseDayCodes', function() {

        it ('can check that we can extract days correctly for valid input', function () {

            expect(DateUtilsService.parseDayCodes('d1')).toEqual(1);
            expect(DateUtilsService.parseDayCodes('d180')).toEqual(180);
            expect(DateUtilsService.parseDayCodes('d09')).toEqual(9);
            expect(DateUtilsService.parseDayCodes('d005')).toEqual(5);
            expect(DateUtilsService.parseDayCodes('005d')).toEqual(5);
            expect(DateUtilsService.parseDayCodes('D40')).toEqual(40);
            expect(DateUtilsService.parseDayCodes('40D')).toEqual(40);

            expect(DateUtilsService.parseDayCodes('v1')).toEqual(7);
            expect(DateUtilsService.parseDayCodes('v3')).toEqual(21);
            expect(DateUtilsService.parseDayCodes('v04')).toEqual(28);
            expect(DateUtilsService.parseDayCodes('v009')).toEqual(63);
            expect(DateUtilsService.parseDayCodes('009v')).toEqual(63);
            expect(DateUtilsService.parseDayCodes('V20')).toEqual(140);
            expect(DateUtilsService.parseDayCodes('20V')).toEqual(140);

        });

        it ('can check that we will not extract days for invalid input', function () {

            expect(DateUtilsService.parseDayCodes(undefined)).toBe(null);
            expect(DateUtilsService.parseDayCodes(null)).toBe(null);
            expect(DateUtilsService.parseDayCodes('')).toBe(null);
            expect(DateUtilsService.parseDayCodes('180')).toBe(null);
            expect(DateUtilsService.parseDayCodes('v1800')).toBe(null);
            expect(DateUtilsService.parseDayCodes('d1800')).toBe(null);
            expect(DateUtilsService.parseDayCodes('d0')).toBe(null);
            expect(DateUtilsService.parseDayCodes('d00')).toBe(null);
            expect(DateUtilsService.parseDayCodes('d000')).toBe(null);
            expect(DateUtilsService.parseDayCodes('000d')).toBe(null);
            expect(DateUtilsService.parseDayCodes('d000d')).toBe(null);
            expect(DateUtilsService.parseDayCodes('v0')).toBe(null);
            expect(DateUtilsService.parseDayCodes('v00')).toBe(null);
            expect(DateUtilsService.parseDayCodes('v000')).toBe(null);
            expect(DateUtilsService.parseDayCodes('000v')).toBe(null);
            expect(DateUtilsService.parseDayCodes('v000v')).toBe(null);

        });
    });

    describe('#parseMonthCode', function() {

        it ('can check that we can extract months correctly for valid input', function () {
            expect(DateUtilsService.parseMonthCode('m1')).toEqual(1);
            expect(DateUtilsService.parseMonthCode('m24')).toEqual(24);
            expect(DateUtilsService.parseMonthCode('m03')).toEqual(3);
            expect(DateUtilsService.parseMonthCode('03m')).toEqual(3);
            expect(DateUtilsService.parseMonthCode('M12')).toEqual(12);
            expect(DateUtilsService.parseMonthCode('12M')).toEqual(12);
        });

        it ('can check that we will not extract months for invalid input', function () {
            expect(DateUtilsService.parseMonthCode(undefined)).toBe(null);
            expect(DateUtilsService.parseMonthCode(null)).toBe(null);
            expect(DateUtilsService.parseMonthCode('')).toBe(null);
            expect(DateUtilsService.parseMonthCode('180')).toBe(null);
            expect(DateUtilsService.parseMonthCode('v18')).toBe(null);
            expect(DateUtilsService.parseMonthCode('d180')).toBe(null);
            expect(DateUtilsService.parseMonthCode('m0')).toBe(null);
            expect(DateUtilsService.parseMonthCode('m00')).toBe(null);
            expect(DateUtilsService.parseMonthCode('00m')).toBe(null);
            expect(DateUtilsService.parseMonthCode('m00m')).toBe(null);
        });

    });

    describe('Loose dateparser', function() {

        var parser;

        beforeEach(function() {
            var form = { $setValidity:function(){}, $setViewValue:function(){}, $render:function(){} };
            DateUtilsService.addLooseDateParser(form);
            parser =  form.$parsers[0];
        });

        it('converts YYYY-MMDD dates', function() {
            expect(parser('2016-0102')).toBe('2016-01-02');
        });

        it('converts YYYYMM-DD dates', function() {
            expect(parser('201601-02')).toBe('2016-01-02');
        });

        it('converts YYYYMMDD dates', function() {
            expect(parser('20160102')).toBe('2016-01-02');
        });

        it('can keep dashes', function() {
            expect(parser('----')).toBe('----');
            expect(parser('2016-')).toBe('2016-');
            expect(parser('201601-')).toBe('201601-');
            expect(parser('sdfs--')).toBe('sdfs--');
        });

    });
});
