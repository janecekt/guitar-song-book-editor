'use strict';

let gulp = require('gulp');
let del = require('del');
let gulpUtil = require('gulp-util');

let webpackStream = require('webpack-stream');
let webpack = require('webpack');
let WebPackDevServer = require('webpack-dev-server');
let CopyWebpackPlugin = require('copy-webpack-plugin');
let UglifyJsPlugin = require('uglifyjs-webpack-plugin');

let HtmlWebpackPlugin = require('html-webpack-plugin');


let config = {
    allTypeScript: `./src/main/**/*.{ts,tsx}`,
    entry: './src/main/main.tsx',
    dist: `./target`
};

function buildWebPackConfig(watch, environment) {
    let minimizer = watch
        ? []
        : [
            new UglifyJsPlugin({
                cache: true,
                sourceMap: true,
                exclude: 'sw.js'
            }),
        ];

    let hash = watch ? '' : '-[contenthash]';

    return {
        mode: environment,
        entry: {
            'bundle': [ "./src/main/main.tsx" ],
            'sw-main': [ "./src/main/sw-main.ts" ]
        },
        output: {
            path: __dirname + "/dist",
            filename: "[name]" + hash + ".js"
        },
        optimization: {
            moduleIds: 'hashed',
            minimizer: minimizer,
            splitChunks: {
                cacheGroups: {
                    vendor: {
                        test: /[\\/]node_modules[\\/]/,
                        name: 'vendor',
                        chunks: 'all',
                    },
                },
            },
        },
        module: {
            rules: [
                {
                    test: /\.less$/,
                    use: [
                        {
                            loader: 'style-loader'
                        },
                        {
                            loader: 'css-loader'
                        },
                        {
                            loader: 'less-loader'
                        }
                    ]
                },
                {
                    test: /\.css$/,
                    use: [
                        {
                            loader: 'style-loader'
                        },
                        {
                            loader: 'css-loader'
                        }
                    ]
                },
                {
                    test: /\.ts(x?)$/,
                    exclude: /(node_modules|bower_components)/,
                    use: [
                        {
                            loader: 'babel-loader',
                            options: {
                                presets: ['@babel/preset-env']
                            }
                        },
                        {
                            loader: 'ts-loader'
                        }
                    ]
                },
                {
                    test: /\.(js(x?))$/,
                    exclude: /(node_modules|bower_components)/,
                    use: [
                        {
                            loader: 'babel-loader',
                            options: {
                                presets: ['@babel/preset-env']
                            }
                        }
                    ]
                },
                {
                    test: /\.(eot|svg|ttf|woff|woff2)$/,
                    use: [
                        {
                            loader: 'file-loader?name=fonts/[name]-[hash].[ext]'
                        }
                    ]
                }
            ]
        },
        devtool: 'source-map',
        devServer: {
            contentBase: config.dist,
            compress: true,
            port: 9000,
            watchOptions:{
                ignored: [ 'node_modules/**' ],
                poll: 5000,
                aggregateTimeout: 500
            }
        },
        resolve: {
            extensions: [".ts", ".tsx", ".js"],
            modules: [ './src', '../node_modules' ]
        },
        plugins: [
            // Set NODE_ENV
            new webpack.DefinePlugin({
                'process.env':{
                    'NODE_ENV': JSON.stringify(environment)
                }
            }),

            // Copy static resources
            new CopyWebpackPlugin([
                {from: 'src/main/.htaccess', to: '[name].[ext]'},
                {from: 'src/main/shell.css', to: '[name]' + hash + '.[ext]'},
                {from: 'src/main/manifest.json', to: '[name]' + hash + '.[ext]'},
                {from: 'src/main/images', to: 'images'},
                {from: 'src/main/songbook-*.json', to: '[name].[ext]'},
                {from: 'src/main/songbook-*.pdf', to: '[name].[ext]'}
            ]),

            // Generate index.html
            new HtmlWebpackPlugin({
                template: 'src/main/index-template.js',
                inject: false
            }),

            // Generate sw.js
            new HtmlWebpackPlugin({
                template: 'src/main/sw-template.js',
                inject: false,
                filename: 'sw.js'
            }),
        ]
    };
}

function createWebPackDevServer(compiler, callback, hot) {
    return new WebPackDevServer(compiler, {
        hot: hot,
        stats: 'normal',
        inline: hot
    })
        .listen(9090, "0.0.0.0",
            function(err) {
                if(err) throw new gulpUtil.PluginError("webpack-dev-server", err);
                // Server listening
                gulpUtil.log("[webpack-dev-server]", "http://localhost:9090/index.html");

                // keep the server alive or continue?
                callback();
            });
}


function clean(done) {
    del([config.dist]);
    done();
}

function bundle() {
    return gulp
        .src(config.entry)
        .pipe(webpackStream(buildWebPackConfig(false, 'production'), webpack))
        .pipe(gulp.dest(config.dist))
}

function webPackDevServerHot(callback) {
    createWebPackDevServer(
        webpack(buildWebPackConfig(true, 'development')),
        callback,
        true)
}

function webPackDevServerProd(callback) {
    createWebPackDevServer(
        webpack(buildWebPackConfig(true, 'production')),
        callback,
        false)
}

gulp.task('clean', clean);

gulp.task('bundle', bundle);

gulp.task('serve', gulp.series(clean, webPackDevServerProd));

gulp.task('watch', gulp.series(clean, webPackDevServerHot));

gulp.task('build', gulp.series(clean, bundle));