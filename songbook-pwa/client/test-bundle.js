/*
 * Create a context for all tests files below the src folder and all sub-folders.
 */
const context = require.context('./src/test', true, /\.Test\.ts$/);

/*
 * For each file, call the context function that will require the file and load it up here.
 */
context.keys().forEach(context);