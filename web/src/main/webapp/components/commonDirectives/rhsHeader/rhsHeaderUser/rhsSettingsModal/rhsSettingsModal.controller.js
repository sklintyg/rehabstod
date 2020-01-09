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

angular.module('rehabstodApp').controller('RhsSettingsModalCtrl',
    function($scope, $uibModalInstance, UserProxy, UserModel, SjukfallService, _) {
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


      function addSetting(settingData, property, typeConfig) {
        $scope.settings.push({
          id: 'setting-' + property.toLowerCase(),
          property: property,
          title: 'settings.modal.' + property + '.title',
          description: 'settings.modal.' + property + '.description',
          help: typeConfig.showHelp ? 'settings.modal.' + property + '.help' : null,
          typeConfig: typeConfig,
          value: settingData[property] ? settingData[property] : typeConfig.defaultValue
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

        var perferences = UserModel.get().preferences;
        var settingData = convertArrayToObject($scope.settings);

        var settingsToSave = _.assign(perferences, settingData);

        $scope.saving = true;
        UserProxy.saveSettings(settingsToSave).then(function(preferences) {
          $scope.saving = false;
          UserModel.get().preferences = preferences;
          $uibModalInstance.close($scope.settingsModel);
          if (UserModel.isPdlConsentGiven()) {
            SjukfallService.loadSjukfall(true, true);
          }
        }, function() {
          //Handle errors
          $scope.saving = false;
        });

      };

      /**
       * Run
       */
      var oldSettingData = UserModel.get().preferences;
      addSetting(oldSettingData, 'pdlConsentGiven',
          {
            type: 'BOOL_READONLY',
            defaultValue: false,
            showHelp: false
          });
      addSetting(oldSettingData, 'maxAntalDagarMellanIntyg',
          {
            type: 'INT_RANGE',
            showHelp: true,
            min: 0,
            max: 90,
            defaultValue: 5
          });
      if (UserModel.get().totaltAntalVardenheter > 1) {
        addSetting(oldSettingData, 'standardenhet',
            {
              type: 'UNIT_SELECT',
              showHelp: true,
              vardgivare: UserModel.get().vardgivare,
              defaultValue: null
            });
      }
      addSetting(oldSettingData, 'maxAntalDagarSedanSjukfallAvslut',
          {
            type: 'INT_RANGE',
            showHelp: true,
            min: 0,
            max: 14,
            defaultValue: 0
          });
    }
);
