angular.module('privatlakareApp').service('PostnummerRegionLookupViewState',
    function() {
        'use strict';

        this.reset = function() {
            this.errorMessage = {
                region: false
            };

            this.loading = {
                region: false
            };

            this.validPostnummer = false;
            this.kommunOptions = null;
            this.kommunSelectionMode = false;
            this.kommunSelected = false;

            return this;
        };

        this.reset();
    }
);
