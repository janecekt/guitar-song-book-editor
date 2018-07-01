// Karma configuration
module.exports = function(config) {
  config.set({

    // base path that will be used to resolve all patterns (eg. files, exclude)
    basePath: '.',

    // frameworks to use
    // available frameworks: https://npmjs.org/browse/keyword/karma-adapter
    frameworks: ['jasmine'],


    // list of files / patterns to load in the browser
    files: [ 'index-test.js' ],


    // list of files / patterns to exclude
    exclude: [
    ],


    // preprocess matching files before serving them to the browser
    // available preprocessors: https://npmjs.org/browse/keyword/karma-preprocessor
    preprocessors: {
        'index-test.js' : ['webpack', 'sourcemap']
    },

    webpack: {
        mode: 'development',
        module: {
            rules: [
                {
                    test: /\.less$/,
                    loaders: ['style-loader', 'css-loader', 'less-loader']
                },
                {
                    test: /\.css$/,
                    loaders: ['style-loader', 'css-loader']
                },
                {
                    test: /\.ts(x?)$/,
                    loaders: ['babel-loader', 'ts-loader'],
                    exclude: /(node_modules|bower_components)/
                },
                {
                    test: /\.(js(x?))$/,
                    loaders: ['babel-loader'],
                    exclude: /(node_modules|bower_components)/
                },
                {
                    test: /\.(eot|svg|ttf|woff|woff2)$/,
                    loader: 'file-loader?name=fonts/[name].[ext]'
                }
            ]
        },
        devtool: 'inline-source-map',
        resolve: {
            extensions: [".ts", ".tsx", ".js"],
            modules: [ './src', './node_modules' ]
        }
    },

    plugins: [
        'karma-webpack',
        'karma-sourcemap-loader',
        'karma-jasmine',
        'karma-chrome-launcher',
        'karma-mocha-reporter'
    ],

    // test results reporter to use
    // possible values: 'dots', 'progress'
    // available reporters: https://npmjs.org/browse/keyword/karma-reporter
    reporters: ['mocha'],


    // web server port
    port: 9876,


    // enable / disable colors in the output (reporters and logs)
    colors: true,


    // level of logging
    // possible values: config.LOG_DISABLE || config.LOG_ERROR || config.LOG_WARN || config.LOG_INFO || config.LOG_DEBUG
    logLevel: config.LOG_INFO,


    // enable / disable watching file and executing tests whenever any file changes
    autoWatch: true,


    // start these browsers
    // available browser launchers: https://npmjs.org/browse/keyword/karma-launcher
    browsers: ['Chrome', 'ChromeHeadlessNoSandbox'],

    customLaunchers: {
      ChromeHeadlessNoSandbox: {
          base: 'ChromeHeadless',
          flags: [
              '--no-sandbox', // required to run without privileges in docker
              '--user-data-dir=/tmp/chrome-test-profile',
              '--disable-web-security'
          ]
      }
    },

    // Continuous Integration mode
    // if true, Karma captures browsers, runs the tests and exits
    singleRun: false,

    // Concurrency level
    // how many browser should be started simultaneous
    concurrency: Infinity
  })
};