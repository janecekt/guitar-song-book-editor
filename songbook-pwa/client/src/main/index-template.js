// Template for creating HTML

module.exports = function (templateParams) {
    let assets = templateParams.compilation.getAssets()
        .map(obj => obj.name);

    let vendorName = assets.find(name => name.startsWith('vendor-') && name.endsWith('.js'));
    if (vendorName === undefined) {
        vendorName = 'vendor.js';
    }

    let bundleName = assets.find(name => name.startsWith('bundle-') && name.endsWith('.js'));
    if (bundleName === undefined) {
        bundleName = 'bundle.js';
    }

    let shellCssName = assets.find(name => name.startsWith('shell-') && name.endsWith('.css'));
    if (shellCssName === undefined) {
        shellCssName = 'shell.css';
    }

    let manifestName = assets.find(name => name.startsWith('manifest-') && name.endsWith('.json'));
    if (manifestName === undefined) {
        manifestName = 'manifest.json';
    }

    let version = bundleName.substring(("bundle-".length), bundleName.length - ('.js'.length));

    return '<!DOCTYPE html>' + "\n"
        + '<html lang="cs">' + "\n"
        + '<head>' + "\n"
        + '  <meta charset="UTF-8" />' + "\n"
        + '  <meta name="theme-color" content="#3F51B5" />' + "\n"
        + '  <meta name="viewport" content="width=device-width, initial-scale=1.0">' + "\n"
        + '  <title>Songbook Mobile</title>' + "\n"
        + '  <link rel="manifest" href="' + manifestName + '" />' + "\n"
        + '  <link rel="stylesheet" href="' + shellCssName + '"/>' + "\n"
        + '</head>' + "\n"
        + '<body class="body">' + "\n"
        + '  <div id="root">' + "\n"
        + '    <div id="static-shell">' + "\n"
        + '      <div class="tmp-app-header">' + "\n"
        + '        <span class="tmp-app-header-text">SongBook</span>' + "\n"
        + '      </div>' + "\n"
        + '      ' + "\n"
        + '      <div class="app-loader-wrapper">' + "\n"
        + '         <span class="app-loader">' + "\n"
        + '           <span class="app-loader-inner"></span>' + "\n"
        + '         </span>' + "\n"
        + '      </div>' + "\n"
        + '    </div>' + "\n"
        + '  </div>' + "\n"
        + '  <script>' + "\n"
        + '     // Add version information' + "\n"
        + "     window.appVersion = '" + version + "';" + "\n"
        + "\n"
        + '     // Link to songbook' + "\n"
        + "     window.songBookJsonUrl= './songbook-sample.json';" + "\n"
        + "     window.songBookPdfUrl= './songbook-sample.pdf';" + "\n"
        + "\n"
        + '     window.onload = function() {' + "\n"
        + "        if ('serviceWorker' in navigator) {" + "\n"
                     // Register service worker (if supported)
        + "          navigator.serviceWorker.register('./sw.js')" + "\n"
        + "            .then(function(reg) { console.log('Service worker registration succeeded:', reg); })" + "\n"
        + "            .catch(function(error) { console.log('Service worker registration failed:', error); });" + "\n"
        + "\n"
                     // Reload page when new service worker is loaded
        + "          navigator.serviceWorker.addEventListener('controllerchange', function() {" + "\n"
        + "            console.log('Service worker updated - refreshing page.');" + "\n"
        + "            location.reload();" + "\n"
        + "          });" + "\n"
        + "        } else {" + "\n"
        + "           console.log('Service workers are not supported.')" + "\n"
        + "        }" + "\n"
        + "\n"
        + '        // Add bundle script - this is done from JavaScript to avoid blocking' + "\n"
        + "        let scriptVendor = document.createElement('script');" + "\n"
        + "        scriptVendor.type = 'text/javascript';" + "\n"
        + "        scriptVendor.src = '" + vendorName  + "';" + "\n"
        + "        document.body.appendChild(scriptVendor);" + "\n"
        + "\n"
        + "        let scriptBundle = document.createElement('script');" + "\n"
        + "        scriptBundle.type = 'text/javascript';" + "\n"
        + "        scriptBundle.src = '" + bundleName  + "';" + "\n"
        + "        document.body.appendChild(scriptBundle);" + "\n"
        + "      }" + "\n"
        + '  </script>' + "\n"
        + '</body>' + "\n"
        + '</html>';
};