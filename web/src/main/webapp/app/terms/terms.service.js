angular.module('privatlakareApp').factory('TermsService',
    function($state, $log, $q, $timeout, $filter, $window, wcModalService, AppTermsModalModel, messageService,
        TermsModel, TermsProxy) {
        'use strict';

        function _printTerms(content) {
            var head = '<!DOCTYPE html><html>' +
                '<head>' +
                '<link rel="stylesheet" href="/app/app.css" media="print">' +
                '<title>'+ messageService.getProperty(content.titleId) +'</title>' +
                '</head>';

            var body = '<body onload="window.print()">';
            if(content.logoImage !== undefined && content.logoImage !== null) {
                body += '<img class="pull-left" style="padding-bottom: 20px" src="'+ content.logoImage + '" />';
            }
            body += '<p style="clear:left;padding-top:20px;padding-bottom:20px;color:#535353">' +
                '<span style="padding-right:30px">Version: ' +
                content.terms.termsModel.version + '</span><br>' +
                '<span>Datum: ' + $filter('date')(content.terms.termsModel.date, 'yyyy-MM-dd') + '</span></p>' +
                '<h1 style="color: black;font-size: 2em">' + messageService.getProperty(content.titleId) + '</h1>' +
                '<p style="clear:left;padding-bottom: 10px">' + content.terms.termsModel.text + '</p>' +
                '<p style="clear:left;color:#535353;padding-top:50px">' + content.absUrl + '</p>' +
                '</body>';
            var footer = '</html>';

            var template = head + body + footer;

            var popupWin = null;
            if (navigator.userAgent.toLowerCase().indexOf('chrome') > -1) {
                popupWin = $window.open('', '_blank',
                    'width=400,scrollbars=no,menubar=no,toolbar=no,location=no,status=no,titlebar=no');
                popupWin.window.focus();
                popupWin.document.write(template);
                $timeout(function() {
                    popupWin.close();
                }, 100);
                popupWin.onbeforeunload = function(/*event*/) {
                    popupWin.close();
                };
                popupWin.onabort = function(/*event*/) {
                    popupWin.document.close();
                    popupWin.close();
                };
            } else {
                popupWin = $window.open('', '_blank',
                    'width=800,scrollbars=yes,menubar=no,toolbar=no,location=no,status=no,titlebar=no');
                popupWin.document.open();
                popupWin.document.write(template);
            }
            popupWin.document.close();

            return true;
        }

        function _loadTerms() {

            var termsLoadDeferred = $q.defer();

            var content = {
                termsModel: TermsModel.init(),
                loading: false,
                loadedOK: false
            };

            if ($state.params.termsData) { // Medgivandevillkor loads data into state param in Step3controller.
                TermsModel.set($state.params.termsData);
                content.loadedOK = $state.params.termsData !== null;
                termsLoadDeferred.resolve(content);
            }
            else if ($state.params.terms) { // Webcertvillkor just sets terms to 'webcert' on state link to /terms and expects them to load here
                content.loading = true;
                TermsProxy.getTerms($state.params.terms).then(function(successData) {
                    content.loading = false;
                    content.loadedOK = true;
                    TermsModel.set(successData);
                    $log.info('modal content loaded. updating size.');
                    termsLoadDeferred.resolve(content);
                }, function(errorData) {
                    content.loading = false;
                    content.loadedOK = false;
                    $log.debug('Failed to get terms.');
                    $log.debug(errorData);
                    termsLoadDeferred.resolve(content);
                });
            } else { // None loaded correctly but user opened dialog. Show error.
                content.loadedOK = false;
                termsLoadDeferred.resolve(content);
            }

            return termsLoadDeferred.promise;
        }

        function _openTerms(modalModel) {
            function popState() {
                if ($state.current.url === '/terms') {
                    $state.go('^');
                }
            }

            modalModel.options.buttons = [
                {
                    name: 'close',
                    text: 'common.close',
                    id: 'dismissBtn',
                    className: 'btn-success',
                    clickFn: function($modalInstance) {
                        $modalInstance.close();
                        popState();
                    }
                },
                {
                    name: 'print',
                    text: 'common.print',
                    id: 'printBtn',
                    clickFn: function($modalInstance, content) {
                        if(content.terms.loadedOK) {
                            _printTerms(content);
                        }
                    }
                }
            ];

            modalModel.modalInstance = wcModalService.open(modalModel.options);
        }

        return {
            loadTerms: _loadTerms,
            printTerms: _printTerms,
            openTerms: _openTerms
        };
    });
