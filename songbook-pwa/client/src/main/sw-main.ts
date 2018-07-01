let worker = (self as any) as MyServiceWorker;

// Install Service Worker
worker.addEventListener('install', (event : ExtendableEvent) => {
    event.waitUntil(installSW());
});

// Activate Service Worker
worker.addEventListener("activate", (event : ExtendableEvent) => {
    event.waitUntil(activateSW());
});

// Intercept fetch events
worker.addEventListener('fetch', (event : FetchEvent) => {
    console.log("Fetch event", worker.swProps.cacheName, "for url" ,  event.request.url);
    return event.respondWith(interceptFetch(event.request));
});


async function installSW() : Promise<void> {
    try {
        console.log("Installing service worker", worker.swProps.cacheName);

        // Get prefix of the location where the service worker is installed
        const prefix = worker.location.pathname.substr(0,
            worker.location.pathname.lastIndexOf('/'));

        // Add prefix to all assets to pre-cache
        const prefixedAssetsToPreCache = worker.swProps.assetsToPreCache
            .map(asset => prefix + asset);

        // Open Cache
        const cache: Cache = await caches.open(worker.swProps.cacheName);

        // Download all resources into cache
        await cache.addAll(prefixedAssetsToPreCache);

        // Skip waiting for a worker
        worker.skipWaiting();
    } catch (err) {
        console.error("Failed to install SW", err);
        throw err;
    }
}


async function activateSW() : Promise<void> {
    try {
        console.log("Activating service worker", worker.swProps.cacheName);

        // Remove all unused caches
        const cacheKeys = await caches.keys();
        const keysToRemove = cacheKeys.filter(cacheName => worker.swProps.cacheName !== cacheName);

        for (let idx = 0; idx < keysToRemove.length; idx++) {
            const cacheName = keysToRemove[idx];
            try {
                const deleted = await caches.delete(cacheName);
                console.info("Cache " + cacheName + (deleted ? " deleted" : "not deleted"));
            } catch (err) {
                console.error("Failed to remove cache " + cacheName, err);
            }
        }

        await worker.clients.claim();
    } catch (err) {
        console.error("Failed to activate SW", err);
        throw err;
    }
}


async function interceptFetch(request : Request) : Promise<Response> {
    try {
        const cache = await caches.open(worker.swProps.cacheName);
        const response = await cache.match(request);
        if (response && response.ok) {
            console.log("Serving from SW cache ", request.method, request.url);
            return response;
        } else if (request.headers.get("X-Cache-Permanently") === 'true') {
            console.log("Not found in cache (serving from network) and caching permanently", request.url);
            let response = await fetch(request.clone());
            if (response.ok) {
                console.log("Adding to cache", request.url);
                await cache.put(request, response.clone());
            }
            return response;
        } else {
            console.log("Not found in cache (serving from network)", request.url);
            return fetch(request);
        }
    } catch (err) {
        console.log("Serving from network", request.url, err);
        return fetch(request);
    }
}