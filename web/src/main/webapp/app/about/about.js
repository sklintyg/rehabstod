/*
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

angular.module('rehabstodApp')
    .config(function($stateProvider) {
        'use strict';
        $stateProvider
            .state('app.about', {
                url: '/about',
                views: {
                    'content@app': {templateUrl: '/app/about/about.page.html', controller: 'AboutPageCtrl'}
                },
                data: {
                    rule: function(fromState, toState) {

                        if (toState.name !== 'app.about') {
                            return;
                        }

                        var to = 'app.about.rehabstod';

                        return {
                            to: to,
                            params: {}
                        };
                    }
                }
            })
            .state('app.about.rehabstod', {
                url: '/rehabstod',
                views: {
                    'content@app.about': {
                        templateUrl: '/app/about/subpages/rehabstod.html'
                    }
                }
            })
            .state('app.about.faq', {
                url: '/faq',
                views: {
                    'content@app.about': {
                        templateUrl: '/app/about/subpages/faq.html',
                        controller: 'AboutFaqPageCtrl'
                    }
                }
            })
            .state('app.about.support', {
                url: '/support',
                views: {
                    'content@app.about': {
                        templateUrl: '/app/about/subpages/support.html'
                    }
                }
            });
    });