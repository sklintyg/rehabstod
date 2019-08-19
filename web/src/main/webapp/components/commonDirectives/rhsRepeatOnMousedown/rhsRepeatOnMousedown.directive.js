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

angular.module('rehabstodApp').directive('rhsRepeatOnMousedown',
    function($parse, $interval) {
      'use strict';

      //Delay (ms) between repeat action calls
      var TICK_INTERVAL = 100;

      //Delay before start repeating
      var BEFORE_START_REPEAT_DELAY = 500;

      return {

        restrict: 'A',

        link: function(scope, elem, attrs) {

          //Get reference to repeat action callback
          var action = $parse(attrs.rhsRepeatOnMousedown);

          //Start with the initial delay interval
          var activeTickInterval = BEFORE_START_REPEAT_DELAY;

          var intervalPromise = null;

          function execute() {
            //Execute Repeat action
            action(scope);
            //Should we switch the repeat delay interval (going into active repeat mode)?
            if (activeTickInterval === BEFORE_START_REPEAT_DELAY) {
              //Switch to repeat mode interval
              activeTickInterval = TICK_INTERVAL;
              $interval.cancel(intervalPromise);
              intervalPromise = $interval(execute, activeTickInterval);
            }
          }

          //Start listening to mousedown - which in turn will start the actual behaviour of this directive
          function bindStartAction() {
            elem.on('mousedown', function(e) {
              e.preventDefault();
              activeTickInterval = BEFORE_START_REPEAT_DELAY;
              intervalPromise = $interval(execute, activeTickInterval);
              bindEndAction();
            });
          }

          function bindEndAction() {
            //Set up when to end repeat action
            elem.on('mouseup', endAction);
            elem.on('mouseleave', endAction);
          }

          function endAction() {
            $interval.cancel(intervalPromise);
            unbindEndAction();
          }

          function unbindEndAction() {
            elem.off('mouseup', endAction);
            elem.off('mouseleave', endAction);
          }

          scope.$on('$destroy', function() {
            //Clean up any pending interval promise
            $interval.cancel(intervalPromise);
          });

          //Finally, start listening
          bindStartAction();

        }
      };
    });
