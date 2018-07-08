// Template for creating HTML

module.exports = function (templateParams) {
    let assets = templateParams.webpack.assets
        .map(obj => obj.name)
        .filter(name => !name.endsWith('.map'))
        .filter(name => !name.endsWith('.htaccess'))
        .filter(name => !(name.startsWith('songbook-')  && name.endsWith('.json')));

    let swMainName = assets.find(name => name.startsWith('sw-main-') && name.endsWith('.js'));
    if (swMainName === undefined) {
        swMainName = 'sw-main.js';
    }

    let cacheName = swMainName.substring(0, swMainName.length - 3);

    let assetsAsJson = '['
        + "'/',\n\t\t"
        + assets
            .map(name => "'" + "/" + name + "'")
            .reduce((accumulator, name) => accumulator + ",\n\t\t" +  name )
        + ']';

    return "self.swProps = {\n\t"
        + "'assetsToPreCache' : " + assetsAsJson + "," + "\n\t"
        + "'cacheName' : '" + cacheName + "'" + "\n"
        + "};" + "\n"
        + "importScripts('" + swMainName  + "');";
};