// Generated on 2015-06-10 using generator-angular-fullstack 2.0.13
'use strict';

module.exports = function(grunt) {

    grunt.loadNpmTasks('grunt-connect-proxy');

    // Load grunt tasks automatically, when needed
    require('jit-grunt')(grunt, {
        connect: 'grunt-contrib-connect',
        useminPrepare: 'grunt-usemin',
        ngtemplates: 'grunt-angular-templates',
        protractor: 'grunt-protractor-runner',
        injector: 'grunt-injector',
        configureProxies: 'grunt-connect-proxy',
        sasslint: 'grunt-sass-lint'
    });

    var serveStatic = require('serve-static');

    // Time how long tasks take. Can help when optimizing build times
    require('time-grunt')(grunt);

    // Define the configuration for all the tasks
    grunt.initConfig({

        // Project settings
        pkg: grunt.file.readJSON('package.json'),
        config: {
            // configurable paths
            client: 'src/main/webapp',
            node_modules: 'node_modules',
            dist: 'build/webapp',
            tmp: 'build/.tmp'
        },
        connect: {
            dev: {
                options: {
                    port: 9091,
                    base: 'src/main/webapp',
                    hostname: '*',
                    middleware: function(connect, options, middlewares) {
                        return [
                            require('connect-livereload')(),
                            connect().use(
                                '/index.html',
                                serveStatic(__dirname + '/src/main/webapp/index.html') // jshint ignore:line
                            ),
                            connect().use(
                                '/welcome.html',
                                serveStatic(__dirname + '/src/main/webapp/welcome.html') // jshint ignore:line
                            ),
                            connect().use(
                                '/app',
                                serveStatic(__dirname + '/src/main/webapp/app') // jshint ignore:line
                            ),
                            connect().use(
                                '/components',
                                serveStatic(__dirname + '/src/main/webapp/components') // jshint ignore:line
                            ),
                            require('grunt-connect-proxy/lib/utils').proxyRequest
                        ];
                    }
                },
                proxies: [
                    {
                        context: '/',
                        host: 'localhost',
                        port: 8790
                    }
                ]
            }
        },
        open: {
            dev: {
                url: 'http://localhost:9091/welcome.html'
            }
        },
        watch: {
            injectJS: {
                files: [
                    '<%= config.client %>/{app,components}/**/*.js',
                    '!<%= config.client %>/{app,components}/**/*.spec.js',
                    '!<%= config.client %>/{app,components}/**/*.mock.js',
                    '!<%= config.client %>/app/app.main.js',
                    '!<%= config.client %>/app/app.main.test.js'],
                tasks: ['injector:scripts']
            },
            injectCss: {
                files: [
                    '<%= config.client %>/{app,components}/**/*.css'
                ],
                tasks: ['injector:css']
            },
            jsTest: {
                files: [
                    '<%= config.client %>/{app,components}/**/*.spec.js',
                    '<%= config.client %>/{app,components}/**/*.mock.js'
                ],
                tasks: ['newer:jshint:all', 'karma']
            },
            injectSass: {
                files: [
                    '<%= config.client %>/{app,components}/**/*.{scss,sass}'],
                tasks: ['injector:sass']
            },
            sass: {
                files: [
                    '<%= config.client %>/{app,components}/**/*.{scss,sass}'],
                tasks: ['sass', 'postcss']
            },
            gruntfile: {
                files: ['Gruntfile.js']
            },
            livereload: {
                files: [
                    '{<%= config.tmp %>,<%= config.client %>}/{app,components}/**/*.css',
                    '{<%= config.tmp %>,<%= config.client %>}/{app,components}/**/*.scss',
                    '{<%= config.tmp %>,<%= config.client %>}/{app,components}/**/*.html',
                    '{<%= config.tmp %>,<%= config.client %>}/{app,components}/**/*.js',
                    '!{<%= config.tmp %>,<%= config.client %>}{app,components}/**/*.spec.js',
                    '!{<%= config.tmp %>,<%= config.client %>}/{app,components}/**/*.mock.js',
                    '<%= config.client %>/assets/images/{,*//*}*.{png,jpg,jpeg,gif,webp,svg}'
                ],
                options: {
                    livereload: true
                }
            }
        },

        // Make sure code styles are up to par and there are no obvious mistakes
        jshint: {
            options: {
                jshintrc: '<%= config.client %>/.jshintrc',
                reporter: require('jshint-stylish'),
                force: false
            },
            all: [
                '<%= config.client %>/{app,components}/**/*.js',
                '!<%= config.client %>/{app,components}/**/*.spec.js',
                '!<%= config.client %>/{app,components}/**/*.mock.js'
            ],
            test: {
                src: [
                    '<%= config.client %>/{app,components}/**/*.spec.js',
                    '<%= config.client %>/{app,components}/**/*.mock.js'
                ]
            }
        },

        // Empties folders to start fresh
        clean: {
            dist: {
                files: [
                    {
                        dot: true,
                        src: [
                            '<%= config.dist %>/*',
                            '<%= config.tmp %>/*'
                        ]
                    }
                ]
            }
        },

        // Add vendor prefixed styles
        postcss: {
            options: {
                map: false,
                processors: [
                    require('autoprefixer')({browsers: ['last 2 versions', 'ie 11']}) // add vendor prefixes
                ]
            },
            dist: {
                src: '<%= config.client %>/app/*.css'
            }
        },

        // Automatically inject Bower components into the app
        wiredep: {
            target: {
                src: [
                    '<%= config.client %>/index.html',
                    'karma.conf.js'
                ],
                ignorePath: '<%= config.client %>/',
                exclude: [/bootstrap-sass-official/, '/bootstrap.js'],
                fileTypes: {
                    js: {
                        block: /(([\s\t]*)\/\/\s*bower:*(\S*))(\n|\r|.)*?(\/\/\s*endbower)/gi,
                        detect: {
                            js: /".*\.js"/gi
                        },
                        replace: {
                            js: '\'<%= config.client %>/{{filePath}}\','
                        }
                    }
                }
            }
        },

        // Renames files for browser caching purposes
        filerev: {
            dist: {
                files: [
                    {
                        src: [
                            '<%= config.dist %>/{,*/}*.js',
                            '<%= config.dist %>/{,*/}*.css',
                            '<%= config.dist %>/assets/images/{,*/}*.{png,jpg,jpeg,gif,webp,svg}',
                            '<%= config.dist %>/assets/fonts/*'
                        ]
                    }
                ]
            }
        },

        concat: {
            options: {
                sourceMap: true
            }
        },

        uglify: {
            options: {
                sourceMap: true,
                sourceMapIn: function(uglifySource) {
                    return uglifySource + '.map';
                },
                sourceMapIncludeSources: true
            }
        },

        // Reads HTML for usemin blocks to enable smart builds that automatically
        // concat, minify and revision files. Creates configurations in memory so
        // additional tasks can operate on them
        useminPrepare: {
            html: ['<%= config.client %>/index.html'],
            options: {
                dest: '<%= config.dist %>',
                staging: '<%= config.tmp %>'
            }
        },

        // Performs rewrites based on rev and the useminPrepare configuration
        usemin: {
            html: ['<%= config.dist %>/{,*/}*.html', '<%= config.dist %>/{,*/}*.jsp'],
            css: ['<%= config.dist %>/{,*/}*.css'],
            js: ['<%= config.dist %>/{,*/}*.js'],
            options: {
                assetsDirs: [
                    '<%= config.dist %>',
                    '<%= config.dist %>/assets/images'
                ],
                // This is so we update image references in our ng-templates
                patterns: {
                    js: [
                        [/(assets\/images\/.*?\.(?:gif|jpeg|jpg|png|webp|svg))/gm,
                            'Update the JS to reference our revved images']
                    ]
                }
            }
        },

        // Allow the use of non-minsafe AngularJS files. Automatically makes it
        // minsafe compatible so Uglify does not destroy the ng references
        ngAnnotate: {
            dist: {
                files: [
                    {
                        expand: true,
                        cwd: '<%= config.tmp %>/concat',
                        src: '*/**.js',
                        dest: '<%= config.tmp %>/concat'
                    }
                ]
            }
        },

        // Package all the html partials into a single javascript payload
        ngtemplates: {
            options: {
                // This should be the name of your apps angular module
                module: 'rehabstodApp',
                htmlmin: {
                    collapseBooleanAttributes: true,
                    collapseWhitespace: true,
                    removeAttributeQuotes: true,
                    removeEmptyAttributes: true,
                    removeRedundantAttributes: true,
                    removeScriptTypeAttributes: true,
                    removeStyleLinkTypeAttributes: true
                },
                usemin: 'app/app.main.js',
                prefix: '/'
            },
            main: {
                cwd: '<%= config.client %>',
                src: ['{app,components}/**/*.html'],
                dest: '<%= config.tmp %>/templates.js'
            }
        },

        // Copies remaining files to places other tasks can use
        copy: {
            dist: {
                files: [
                    {
                        expand: true,
                        dot: true,
                        cwd: '<%= config.client %>',
                        dest: '<%= config.dist %>',
                        src: [
                            'assets/**/*',
                            'bower_components/**/*',
                            'components/**/*',
                            'WEB-INF/**/*',
                            '*.*'
                        ]
                    },
                    {
                        expand: true,
                        cwd: '<%= config.tmp %>/images',
                        dest: '<%= config.dist %>/assets/images',
                        src: ['generated/*']
                    }
                ]
            },
            styles: {
                expand: true,
                cwd: '<%= config.client %>',
                dest: '<%= config.tmp %>/',
                src: ['{app,components}/**/*.css']
            }
        },

        // Test settings
        karma: {
            unit: {
                configFile: 'karma.conf.ci.js',
                singleRun: true
            }
        },

        protractor: {
            options: {
                configFile: 'protractor.conf.js'
            },
            chrome: {
                options: {
                    args: {
                        browser: 'chrome'
                    }
                }
            }
        },


        // Compiles Sass to CSS
        sass: {
            options: {
                includePaths: [
                    '<%= config.client %>/bower_components',
                    '<%= config.client %>/app',
                    '<%= config.client %>/components',
                    '<%= config.node_modules %>/compass-mixins/lib'
                ]
            },
            client: {
                files: {
                    '<%= config.client %>/app/app.css': '<%= config.client %>/app/app.scss'
                }
            }
        },

        sasslint: {
            options: {
                //configFile: 'config/.sass-lint.yml' //For now we use the .sass-lint.yml that is packaged with sass-lint
            },
            target: ['<%= config.client %>/{app,components}/**/*.scss']
        },

        injector: {
            options: {
                lineEnding: grunt.util.linefeed
            },
            // Inject application script files into index.html (doesn't include bower)
            scripts: {
                options: {
                    transform: function(filePath) {
                        filePath = filePath.replace('/src/main/webapp/', '');
                        filePath = filePath.replace('/<%= config.tmp %>/', '');
                        return '<script src="/' + filePath + '"></script>';
                    },
                    starttag: '<!-- injector:js -->',
                    endtag: '<!-- endinjector -->'
                },
                files: {
                    '<%= config.client %>/index.html': [
                        ['{<%= config.tmp %>,<%= config.client %>}/{app,components}/**/*.js',
                            '!{<%= config.tmp %>,<%= config.client %>}/app/app.main.js',
                            '!{<%= config.tmp %>,<%= config.client %>}/app/app.main.test.js',
                            '!{<%= config.tmp %>,<%= config.client %>}/{app,components}/**/*.spec.js',
                            '!{<%= config.tmp %>,<%= config.client %>}/{app,components}/**/*.mock.js']
                    ]
                }
            },

            // Inject component scss into app.scss
            sass: {
                options: {
                    transform: function(filePath) {
                        filePath = filePath.replace('/src/main/webapp/app/', '');
                        filePath = filePath.replace('/src/main/webapp/components/', '');
                        return '@import \'' + filePath + '\';';
                    },
                    starttag: '// injector',
                    endtag: '// endinjector'
                },
                files: {
                    '<%= config.client %>/app/app.scss': [
                        '<%= config.client %>/{app,components}/**/*.{scss,sass}',
                        '!<%= config.client %>/app/app.{scss,sass}'
                    ]
                }
            },

            // Inject component css into index.html
            css: {
                options: {
                    transform: function(filePath) {
                        filePath = filePath.replace('/src/main/webapp/', '');
                        filePath = filePath.replace('/<%= config.tmp %>/', '');
                        return '<link rel="stylesheet" href="/' + filePath + '">';
                    },
                    starttag: '<!-- injector:css -->',
                    endtag: '<!-- endinjector -->'
                },
                files: {
                    '<%= config.client %>/index.html': [
                        '<%= config.client %>/{app,components}/**/*.css'
                    ],
                    '<%= config.client %>/welcome.html': [
                        '<%= config.client %>/{app,components}/**/*.css'
                    ],
                    '<%= config.client %>/error.jsp': [
                        '<%= config.client %>/{app,components}/**/*.css'
                    ]
                }
            }
        }
    });

    // Used for delaying livereload until after server has restarted
    grunt.registerTask('wait', function() {
        grunt.log.ok('Waiting for server reload...');

        var done = this.async();

        setTimeout(function() {
            grunt.log.writeln('Done waiting!');
            done();
        }, 1500);
    });

    grunt.registerTask('connect-keepalive', 'Keep grunt running', function() {
        this.async();
    });

    grunt.registerTask('serve', function(target) {

        grunt.task.run([
            'injector:sass',
            'sass',
            'postcss',
            'injector',
            'wiredep',
            'configureProxies:dev',
            'connect:dev',
            'wait',
            'open',
            'watch'
        ]);
    });

    grunt.registerTask('test', function(target) {
        if (target === 'client') {
            return grunt.task.run([
                'injector:sass',
                'sass',
                'injector',
                'postcss',
                'karma'
            ]);
        }

        else if (target === 'e2e') {
            return grunt.task.run([
                'injector:sass',
                'injector',
                'wiredep',
                'postcss',
                'express:dev',
                'protractor'
            ]);
        }

        else {
            grunt.task.run([
                'test:client'
            ]);
        }
    });

    grunt.registerTask('build_no_minify', [
        'clean:dist',
        'jshint',
        'injector:sass',
        'sass',
        'postcss',
        'injector:scripts',
        'injector:css',
        'wiredep',
        'karma'
    ]);

    grunt.registerTask('build', [
        'clean:dist',
        'jshint',
        'copy:dist',
        'injector:sass',
        'sass',
        'postcss',
        'injector:scripts',
        'injector:css',
        'wiredep',
        'useminPrepare',
        'karma',
        'ngtemplates',
        'concat',
        'ngAnnotate',
        'cssmin',
        'uglify',
        'filerev',
        'usemin'
    ]);

    grunt.registerTask('jshintcheck', [
        'jshint'
    ]);
};
