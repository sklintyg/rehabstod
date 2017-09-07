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
angular.module('rehabstodApp').directive('rhsPatientHistoryTable', [ 'UserModel', 'messageService',
    function(UserModel, messageService) {
    'use strict';

    return {
        restrict: 'E',
        scope: {
            historyItem: '=',
            index: '=',
            onSelect: '&'
        },
        templateUrl: '/components/commonDirectives/rhsPatientHistoryTable/rhsPatientHistoryTable.directive.html',
        link: function($scope) {
            $scope.user = UserModel.get();

            $scope.getToolTip = function(diagnos) {
                var desc = angular.isString(diagnos.beskrivning) ? diagnos.beskrivning :
                    messageService.getProperty('label.table.diagnosbeskrivning.okand', {'kod': diagnos.kod});
                return '<b>' + diagnos.kod + '</b><br>' + desc;
            };

            //Requirements state that only first/last of grader should be returned
            $scope.formatGrader = function(gradArr) {
                switch (gradArr.length) {
                case 0:
                    return '';
                case 1:
                    return gradArr[0] + '%';
                default:
                    return gradArr[0] + '% &#10142; ' + gradArr[gradArr.length - 1] + '%';
                }

            };
        }
    };
} ]);
