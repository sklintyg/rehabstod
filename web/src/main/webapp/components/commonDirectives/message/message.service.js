/**
 * message directive for externalizing text resources.
 *
 * All resourcekeys are expected to be defined in lowercase and available in a
 * global js object named "messages"
 * Also supports dynamic key values such as key="status.{{scopedvalue}}"
 *
 * Usage: <message key="some.resource.key" [fallback="defaulttextifnokeyfound"]/>
 */
angular.module('rehabstodApp').factory('messageService',
    function($rootScope) {
        'use strict';

        var _messageResources = null;

        function _propertyExists(key, language, fallbackToDefaultLanguage) {
            var value;

            if (!language) {
                language = $rootScope.lang;
                if (!language && fallbackToDefaultLanguage) {
                    language = $rootScope.DEFAULT_LANG;
                }
            }

            if (language) {
                value = _getPropertyInLanguage(language, key, null);
                if (value === null || value === undefined) {
                    value = false;
                }
            } else {
                value = false;
            }

            return value;
        }

        function _getProperty(key, variables, defaultValue, language, fallbackToDefaultLanguage) {
            var value;

            if (!language) {
                language = $rootScope.lang;
                if (!language && fallbackToDefaultLanguage) {
                    language = $rootScope.DEFAULT_LANG;
                }
            }

            if (language) {
                value = _getPropertyInLanguage(language, key, variables);
                if (value === null || value === undefined) {
                    value = defaultValue === null || defaultValue === undefined ?
                        '[Missing "' + key + '"]' : defaultValue;
                }
            } else {
                value = '[Missing language]';
            }

            return value;
        }

        function _getPropertyInLanguage(lang, key, variables) {
            _checkResources();
            var message = _messageResources[lang][key];

            angular.forEach(variables, function(value, key) {
                var regexp = new RegExp('\\$\\{' + key + '\\}', 'g');
                message = message.replace(regexp, value);
            });

            return message;
        }

        function _addResources(resources) {
            _checkResources();
            angular.extend(_messageResources.sv, resources.sv);
            angular.extend(_messageResources.en, resources.en);
        }

        function _checkResources() {
            if (_messageResources === null) {
                _messageResources = {
                    'sv': {
                        'initial.key': 'Initial nyckel'
                    },
                    'en': {
                        'initial.key': 'Initial key'
                    }
                };
            }
        }

        return {
            propertyExists: _propertyExists,
            getProperty: _getProperty,
            addResources: _addResources
        };
    }
);
