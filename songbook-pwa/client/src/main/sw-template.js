// Template for creating HTML

module.exports = function (templateParams) {
    let assets = templateParams.webpack.assets
        .map(obj => obj.name)
        .filter(name => !name.endsWith('.map'))
        .filter(name => !name.endsWith('.htaccess'));

    let swMainName = assets.find(name => name.startsWith('sw-main-') && name.endsWith('.js'));
    if (swMainName === undefined) {
        swMainName = 'sw-main.js';
    }

    let cacheName = swMainName.substring(0, swMainName.length - 3);

    let assetsAsJson = '['
        + "'/', "
        + assets
            .map(name => "'" + "/" + name + "'")
            .reduce((accumulator, name) => accumulator + ", " +  name )
        + ']';

    return "self.swProps = {\n"
        + "'assetsToPreCache' : " + assetsAsJson + "," + "\n"
        + "'cacheName' : '" + cacheName + "'" + "\n"
        + "};" + "\n"
        + "importScripts('" + swMainName  + "');";
};