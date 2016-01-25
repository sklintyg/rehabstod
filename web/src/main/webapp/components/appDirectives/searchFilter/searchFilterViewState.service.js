angular.module('rehabstodApp').service('searchfilterViewState',
    function() {
        'use strict';


        this.reset = function() {

            this.sjukfall = [];

            this.filter = {
                sjukskrivningslangd: {
                    high: null,
                    low: null
                },
                lakare : null,
                diagnos: null
            };

            return this;
        };

        this.reset();
    }
);