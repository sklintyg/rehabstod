angular.module('privatlakareApp').factory('PostnummerHelper',
    function(ObjectHelper) {
        'use strict';

        function _cleanPostnummer(postnr) {
            if(!ObjectHelper.isDefined(postnr)) {
                return;
            }

            postnr = postnr.trim();
            postnr = postnr.replaceAll(' ', '');
            return postnr;
        }

        function _isValidPostnummer(postnr) {
            var cleanPostnr = _cleanPostnummer(postnr);
            return (ObjectHelper.isDefined(cleanPostnr) && (cleanPostnr.length === 5) && !isNaN(Number(cleanPostnr)));
        }

        return {
            cleanPostnummer: _cleanPostnummer,
            isValidPostnummer: _isValidPostnummer
        };
    }
);
