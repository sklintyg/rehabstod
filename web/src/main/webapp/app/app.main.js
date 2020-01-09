/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
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

deferredBootstrapper.bootstrap({
  element: document.body,
  module: 'rehabstodApp',
  resolve: {
    LINKS: ['$http', function($http) {
      'use strict';
      return $http.get('/api/config/links');
    }],
    APP_CONFIG: ['$http', function($http) {
      'use strict';
      return $http.get('/api/config');
    }],
    USER_DATA: ['$http', '$q', function($http, $q) {
      'use strict';
      var promise = $q.defer();
      $http.get('/api/user').then(function(response) {
        promise.resolve(response.data);
      }, function() {
        //resolve user as empty user object, in case we accessing the index page.
        promise.resolve(undefined);
      });

      return promise.promise;

    }]
  },
  onError: function(error) {
    'use strict';
    //We don't have access to any components in our app yet, since the bootstrap resolve failed, so
    //redirect to error page with our best guess.

    var reason = 'unknown';
    //If the resolve failed with a 403 status, we're most likely not authenticated, e.g used a
    //bookmark / deep link without logging in first.
    if (error && error.status === 403) {
      reason = 'denied';
    }
    window.location.href = '/error.jsp?reason=' + reason; // jshint ignore:line
  }
});
