angular.module('rehabstodApp').factory('UserModel',
    function($window, $timeout, ObjectHelper) {
        'use strict';

        var data = {};

        function _reset() {
            data.name = null;
            data.role = null;
            data.authenticationScheme = null;
            data.fakeSchemeId = 'urn:inera:rehabstod:siths:fake';
            data.valdVardenhet = null;
            data.valdVardgivare = null;
            data.vardgivare = null;
            data.totaltAntalVardenheter = 0;
            data.isLakare = false;
            data.urval = null;

            data.loggedIn = false;
            return data;
        }

        function _changeLocation(newLocation) {
            $timeout(function() {
                $window.location.href = newLocation;
            });
        }

        /**
         * Roles property containt 1 property that also is the role object, since we don't know which role the
         * user has, we have do this clumsy dance to get to it.
         * @param roles
         * @returns {*}
         * @private
         */
        function _resolveRole(roles) {
            if (angular.isDefined(roles)) {
                if (roles.hasOwnProperty('LAKARE')) {
                    return roles.LAKARE;
                } else if (roles.hasOwnProperty('REHABKOORDINATOR')) {
                    return roles.REHABKOORDINATOR;
                }
            }
            return null;
        }

        return {

            reset: _reset,
            init: function() {
                return _reset();
            },

            set: function(user) {
                _reset();
                data.name = user.namn;
                data.role = _resolveRole(user.roles);
                data.authenticationScheme = user.authenticationScheme;
                data.valdVardenhet = user.valdVardenhet;
                data.valdVardgivare = user.valdVardgivare;
                data.vardgivare = user.vardgivare;
                data.totaltAntalVardenheter = user.totaltAntalVardenheter;
                data.loggedIn = true;
                data.isLakare = this.isLakare();
                data.urval = user.urval;
            },
            get: function() {
                return data;
            },

            isLakare: function() {
                return (ObjectHelper.isDefined(data.role) && data.role.name === 'LAKARE');
            },

            isUrvalSet: function() {
                return (ObjectHelper.isDefined(data.urval));
            },

            setUrval: function(newUrval) {
                data.urval = newUrval;
            },

            fakeLogin: function() {
                if (data.authenticationScheme === data.fakeSchemeId) {
                    _changeLocation('/welcome.html');
                }
            },
            logout: function() {
                if (data.authenticationScheme === data.fakeSchemeId) {
                    _changeLocation('/logout');
                } else {
                    _changeLocation('/saml/logout/');
                }
            },
            getLogoutLocation: function() {
                if (data.authenticationScheme === data.fakeSchemeId) {
                    return '/logout';
                } else {
                    return '/saml/logout/';
                }
            }
        };
    }
);