angular.module('rehabstodApp').service('searchfilterViewState',
    function() {
        'use strict';


        this.reset = function() {

            this.model = {
                sjukskrivningslangd: {
                    high: null,
                    low: null
                }
            };

            return this;
        };

        this.reset();
    }
);