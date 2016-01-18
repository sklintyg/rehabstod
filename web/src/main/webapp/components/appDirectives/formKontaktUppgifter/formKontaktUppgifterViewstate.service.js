angular.module('privatlakareApp').service('FormKontaktUppgifterViewState',
    function() {
        'use strict';

        this.reset = function() {

            this.errorMessage = {
                pasteEpost: false,
                pasteEpost2: false
            };

            return this;
        };

        this.reset();
    }
);
