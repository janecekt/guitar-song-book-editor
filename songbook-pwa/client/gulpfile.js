'use strict';

let gulp = require('gulp');
let gulpInject = require('gulp-inject');
let del = require('del');
let gulpUtil = require('gulp-util');

let webpackStream = require('webpack-stream');
let webpack = require('webpack');
let WebPackDevServer = require('webpack-dev-server');
let CopyWebpackPlugin = require('copy-webpack-plugin');
let UglifyJsPlugin = require('uglifyjs-webpack-plugin')

let HtmlWebpackPlugin = require('html-webpack-plugin');


let config = {
    allTypeScript: `./main/**/*.{ts,tsx}`,
    entry: './src/main/main.tsx',
    dist: `../target/dist`
};

function buildWebPackConfig(watch, environment) {
    let extraEntries = watch
        ? [ "webpack/hot/dev-server", 'webpack-dev-server/client?http://localhost:8080/']
        : [];

    let minimizer = watch
        ? []
        : [
            new UglifyJsPlugin({
                cache: true,
                sourceMap: true,
                exclude: 'sw.js'
            }),
        ];

    let extraPlugins = watch
        ? [ new webpack.HotModuleReplacementPlugin() ]
        : [];

    let hash = watch ? '' : '-[hash]';

    return {
        mode: environment,
        entry: {
            'bundle': [...extraEntries, "./src/main/main.tsx"],
            'sw-main': [...extraEntries, "./src/main/sw-main.ts"]
        },
        output: {
            path: __dirname + "/dist",
            filename: "[name]" + hash + ".js"
        },
        optimization: {
            minimizer: minimizer
        },
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
                    loader: 'file-loader?name=fonts/[name]-[hash].[ext]'
                }
            ]
        },
        devtool: 'source-map',
        devServer: {
            contentBase: config.dist,
            compress: true,
            port: 9000
        },
        resolve: {
            extensions: [".ts", ".tsx", ".js"],
            modules: [ './src', '../node_modules' ]
        },
        plugins: [
            ...extraPlugins,

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
                {from: 'src/main/songbook-*.json', to: '[name].[ext]'}
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
        ],
    };
}

function createWebPackDevServer(compiler, callback, hot) {
    return new WebPackDevServer(compiler, {
        hot: hot,
        stats: 'normal',
        inline: hot
    })
        .listen(8080, "0.0.0.0",
            function(err) {
                if(err) throw new gulpUtil.PluginError("webpack-dev-server", err);
                // Server listening
                gulpUtil.log("[webpack-dev-server]", "http://localhost:8080/index.html");

                // keep the server alive or continue?
                callback();
            });
}


gulp.task('clean', function() {
    del([config.dist], {force: true});
});

/**
 * Generates file list and definition list into tsconfig.json. Atom auto do it
 */
gulp.task('gen-ts-refs', function() {
    let target = gulp.src('./tsconfig.json');

    let sources = gulp.src(
        [config.allTypeScript],
        {read: false}
    );

    return target.pipe(
        gulpInject(sources,
            {
                starttag: '"files": [',
                endtag: ']',
                transform(filePath, file, i, length) {
                    return `'.${filePath}' ${(i + 1 < length ? ',' : '')}`;
                }
            })).pipe(gulp.dest('./'));
});


gulp.task('bundle', function() {
    return gulp
        .src(config.entry)
        .pipe(webpackStream(buildWebPackConfig(false, 'production'), webpack))
        .pipe(gulp.dest(config.dist))
});


gulp.task("webpack-dev-server-hot", function(callback) {
    createWebPackDevServer(
        webpack(buildWebPackConfig(false, 'development')),
        callback,
        true)
});


gulp.task("webpack-dev-server-prod", function(callback) {
    createWebPackDevServer(
        webpack(buildWebPackConfig(false, 'production')),
        callback,
        false)
});

gulp.task('serve', ['clean', 'webpack-dev-server-prod']);


gulp.task('watch', ['clean', 'webpack-dev-server-hot']);


gulp.task('build', ['clean', 'bundle']);