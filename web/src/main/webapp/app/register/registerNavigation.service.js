angular.module('privatlakareApp').service('RegisterNavigationService',
    function(ObjectHelper) {
        'use strict';

        // general step navigation
        this.getStepFromState = function(state) {
            if(!ObjectHelper.isDefined(state.data)) {
                return null;
            }

            return state.data.step;
        };

        this.navigationAllowed = function(toState, fromState, formValid) {
            // Prevent user from navigating forwards if clicking the fishbone nav
            var toStep = this.getStepFromState(toState);
            var fromStep = this.getStepFromState(fromState);

            if(toStep === null) { // step number is N/A. allow navigation because it is outside the register flow.
                return true;
            } else {
                return !(toStep > fromStep + 1 || (toStep === fromStep + 1 && !formValid));
            }
        };
    }
);
