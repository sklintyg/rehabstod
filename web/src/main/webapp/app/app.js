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
angular.module('rehabstodApp').config(function($stateProvider) {
    'use strict';
    //This is the App's root that uses index.html as template

    $stateProvider.state('app', {
        abstract: true, // jshint ignore:line
        url: '/app',
        views: {
            'app@': {
                templateUrl: '/app/app.html',
                controller: 'AppPageCtrl'
            },
            'header@': {
                templateUrl: '/app/header/header.html',
                controller: 'HeaderController'
            },
            'navbar@': {
                templateUrl: '/app/navbar/navbar.html',
                controller: 'NavbarCtrl'
            },
            'footer@': {
                templateUrl: '/app/footer/footer.html',
                controller: 'footerCtrl'
            }
        }
    });

});
