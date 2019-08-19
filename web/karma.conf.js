// Karma configuration
// http://karma-runner.github.io/0.10/config/configuration-file.html

module.exports = function(config) {
  config.set({
    // base path, that will be used to resolve files and exclude
    basePath: '',

    // testing framework to use (jasmine/mocha/qunit/...)
    frameworks: ['jasmine'],

    // list of files / patterns to load in the browser
    files: [
      // bower:js
      'src/main/webapp/bower_components/jquery/dist/jquery.js',
      'src/main/webapp/bower_components/angular/angular.js',
      'src/main/webapp/bower_components/angular-animate/angular-animate.js',
      'src/main/webapp/bower_components/angular-resource/angular-resource.js',
      'src/main/webapp/bower_components/angular-cookies/angular-cookies.js',
      'src/main/webapp/bower_components/angular-sanitize/angular-sanitize.js',
      'src/main/webapp/bower_components/angular-messages/angular-messages.js',
      'src/main/webapp/bower_components/angular-i18n/angular-locale_sv-se.js',
      'src/main/webapp/bower_components/angular-bootstrap/ui-bootstrap-tpls.js',
      'src/main/webapp/bower_components/angular-ui-router/release/angular-ui-router.js',
      'src/main/webapp/bower_components/angular-deferred-bootstrap/angular-deferred-bootstrap.js',
      'src/main/webapp/bower_components/angular-smart-table/dist/smart-table.js',
      'src/main/webapp/bower_components/ngstorage/ngStorage.js',
      'src/main/webapp/bower_components/ngInfiniteScroll/build/ng-infinite-scroll.js',
      'src/main/webapp/bower_components/angular-placeholder-tai/lib/tai-placeholder.js',
      'src/main/webapp/bower_components/highcharts/highcharts.js',
      'src/main/webapp/bower_components/highcharts/modules/no-data-to-display.js',
      'src/main/webapp/bower_components/moment/moment.js',
      'src/main/webapp/bower_components/jquery-date-range-picker/src/jquery.daterangepicker.js',
      'src/main/webapp/bower_components/lodash/lodash.js',
      'src/main/webapp/bower_components/floating-scroll/dist/jquery.floatingscroll.min.js',
      // endbower
      'src/main/webapp/bower_components/angular-mocks/angular-mocks.js',
      'src/main/webapp/app/**/*.module.js',

      'src/main/webapp/app/**/*.js',
      'src/main/webapp/components/**/*.js',
      'src/main/webapp/app/**/*.html',
      'src/main/webapp/components/**/*.html'
    ],

    // list of files / patterns to exclude
    exclude: [
      'src/main/webapp/app/app.main.js'
    ],

    preprocessors: {
      'src/main/webapp/!(bower_components)/**/*.html': ['ng-html2js'],
      'src/main/webapp/!(bower_components)/**/!(*spec).js': ['coverage']
    },

    ngHtml2JsPreprocessor: {
      stripPrefix: 'src/main/webapp', // don't strip trailing slash because we're using absolute urls and need it when matching templates
      // the name of the Angular module to create
      moduleName: 'htmlTemplates'
    },

    ngJade2JsPreprocessor: {
      stripPrefix: 'src/main/webapp/'
    },

    // web server port
    port: 47651,

    // level of logging
    // possible values: LOG_DISABLE || LOG_ERROR || LOG_WARN || LOG_INFO || LOG_DEBUG
    logLevel: config.LOG_INFO,

    // enable / disable watching file and executing tests whenever any file changes
    autoWatch: false,

    // Start these browsers, currently available:
    // - Chrome
    // - ChromeCanary
    // - Firefox
    // - Opera
    // - Safari (only Mac)
    // - PhantomJS
    // - IE (only Windows)
    browsers: ['PhantomJS'],

    coverageReporter: {
      reporters: [
        // reporters not supporting the `file` property
        {type: 'html', subdir: 'report-html'},
        {type: 'lcovonly', subdir: '.', file: 'lcov.info'}
      ]
    },

    jasmine: {
      random: false
    },

    plugins: [
      'karma-phantomjs-launcher',
      'karma-coverage',
      'karma-jasmine',
      'karma-ng-html2js-preprocessor'
    ],

    // Continuous Integration mode
    // if true, it capture browsers, run tests and exit
    singleRun: false
  });
};
