angular.module('rehabstodApp').service('HospViewState',
    function() {
        'use strict';

        this.reset = function() {

            this.socialstyrelsenUppgifter = [];

            this.errorMessage = {
                hosp: null
            };

            this.loading = {
                hosp: false
            };

            return this;
        };

        this.reset();
    }
);
