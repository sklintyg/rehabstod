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

angular.module('rehabstodApp').controller('RhsSettingsModalCtrl',
    function($scope, $uibModalInstance, UserProxy, UserModel, SjukfallService) {
        'use strict';

        /**
         * Private functions
         */
        function convertArrayToObject(array) {
            var newObject = {};

            _.each(array, function(value) { // jshint ignore:line
                newObject[value.property] = value.value;
            });

            return newObject;
        }

        function addSetting(settingData, property, defaultValue, min, max) {
            $scope.settings.push({
                id: 'setting-' + property.toLowerCase(),
                property: property,
                title: 'settings.modal.' + property + '.title',
                description: 'settings.modal.' + property + '.description',
                help: 'settings.modal.' + property + '.help',
                min: min,
                max: max,
                value: settingData[property] ? settingData[property] : defaultValue
            });
        }

        /**
         * Exposed scope properties
         */
        $scope.settings = [];

        /**
         * Exposed scope interaction functions
         */
        $scope.cancel = function() {
            $uibModalInstance.dismiss();
        };

        $scope.save = function() {

            var settingData = convertArrayToObject($scope.settings);

            $scope.saving = true;
            UserProxy.saveSettings(settingData).then(function(preferences) {
                $scope.saving = false;
                UserModel.get().preferences = preferences;
                $uibModalInstance.close($scope.settingsModel);
                SjukfallService.loadSjukfall(true, true);
            }, function() {
                //Handle errors
                $scope.saving = false;
            });

        };

        /**
         * Run
         */
        var oldSettingData = UserModel.get().preferences;
        addSetting(oldSettingData, 'maxAntalDagarMellanIntyg', 5, 0, 90);
        addSetting(oldSettingData, 'maxAntalDagarSedanSjukfallAvslut', 0, 0, 14);
    }
);
