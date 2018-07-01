// Find all files ending with Test.ts
let testContext = require.context('./test', true, /Test.ts$/);

// Require all of them into this file
testContext.keys().forEach(testContext);