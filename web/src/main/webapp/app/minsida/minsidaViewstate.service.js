angular.module('privatlakareApp').service('MinsidaViewState',
    function(messageService) {
        'use strict';

        this.reset = function() {

            this.errorMessage = {
                load: null,
                noPermission: false,
                save: null
            };

            this.infoMessage = {
                save: null
            };

            this.loading = {
                save: false
            };

            // UndoModel holds backup of loaded model in case user wants to undo changes
            this.undoModel = {};

            return this;
        };

        this.setUndoModel = function(model) {
            this.undoModel = angular.copy(model);
        };

        this.resetModelFromUndoModel = function(RegisterModel, form) {
            RegisterModel.set(this.undoModel);
            this.infoMessage.save = null;
            form.$setPristine();
            return RegisterModel.get();
        };

        this.changesRequireLogout = function() {
          return this.infoMessage.save !== null;
        };

        this.checkFieldsRequiringLogout = function(form) {
            // No need to do anything if form isn't dirty
            if(form.$dirty) {
                // These fields require logout for intyg to be updated correctly in webcert
                this.infoMessage.save = null;
                var logoutFields = ['verksamhetensnamn', 'telefonnummer', 'adress', 'postnummer'];
                angular.forEach(logoutFields, function(fieldName) {
                    var field = form[fieldName];
                    if(typeof field === 'undefined') {
                        throw 'field ' + fieldName + ' does not exist';
                    } else if(field && field.$dirty) {
                        this.infoMessage.save = messageService.getProperty('label.summary.logoutinfo');
                    }
                }, this);
            }
        };

        this.reset();
    }
);
