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

angular.module('rehabstodApp').factory('SjukfallSummaryModel',
    function() {
        'use strict';

        var data = {};

        _reset();

        function _reset() {
            data.total = null;
            data.genders = null;
            data.groups = [];
            data.diagnoseGroupData = [];
            data.genderData = [];
            data.hasError = false;
            return data;
        }

        function _set(newData) {
            data.hasError = false;
            //reformat for charting purposes
            data.total = newData.total;
            data.genders = newData.genders;
            data.groups = newData.groups;

            data.totalData = [{
                name: '',
                y: newData.total
            }];
            data.diagnoseGroupData = [];
            angular.forEach(data.groups, function(group) {
                data.diagnoseGroupData.push(
                    {
                        id: group.grupp.id,
                        name: group.grupp.name,
                        y: group.count
                    });
            });

            data.genderData = [];
            angular.forEach(data.genders, function(genderStat) {
                data.genderData.push(
                    {
                        id: genderStat.gender,
                        name: genderStat.gender,
                        y: genderStat.count
                    });
            });
        }

        function _get() {
            return data;
        }

        return {
            reset: _reset,
            set: _set,
            get: _get,

            setError: function() {
                _reset();
                data.hasError = true;
            }
        };
    }
);