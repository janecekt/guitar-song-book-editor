// My typings
interface ServiceWorkerProps {
    cacheName : string,
    assetsToPreCache : string[]
}

interface MyServiceWorker extends ServiceWorker {
    swProps : ServiceWorkerProps;
    skipWaiting : () => void;
    clients : Clients;
    location: Location;
}



// Events
interface ExtendableEvent extends Event {
    waitUntil(fn: Promise<any>): void;
}

interface FetchEvent extends Event {
    request: Request;
    respondWith(response: Promise<Response>|Response): Promise<Response>;
}



// Client API
type ClientFrameType = "auxiliary" | "top-level" | "nested" | "none";
type ClientMatchTypes = "window" | "worker" | "sharedworker" | "all";
type WindowClientState = "hidden" | "visible" | "prerender" | "unloaded";

interface Client {
    frameType: ClientFrameType;
    id: string;
    url: string;
}

interface Clients {
    claim(): Promise<any>;
    get(id: string): Promise<Client>;
    matchAll(options?: ClientMatchOptions): Promise<Array<Client>>;
    openWindow(url: string): Promise<WindowClient>;
}

interface ClientMatchOptions {
    includeUncontrolled?: boolean;
    type?: ClientMatchTypes;
}

interface WindowClient {
    focused: boolean;
    visibilityState: WindowClientState;
    focus(): Promise<WindowClient>;
    navigate(url: string): Promise<WindowClient>;
}



/*
interface Navigator {
    serviceWorker: ServiceWorkerContainer;
}


interface ServiceWorker extends Worker {
    scriptURL: string;
    state: ServiceWorkerState;
}

interface ServiceWorkerContainer {
    controller?: ServiceWorker;
    oncontrollerchange?: (event?: Event) => any;
    onerror?: (event?: Event) => any;
    onmessage?: (event?: Event) => any;
    ready: Promise<ServiceWorkerRegistration>;
    getRegistration(scope?: string): Promise<ServiceWorkerRegistration>;
    getRegistrations(): Promise<Array<ServiceWorkerRegistration>>;
    register(url: string, options?: ServiceWorkerRegistrationOptions): Promise<ServiceWorkerRegistration>;
}

interface ServiceWorkerNotificationOptions {
    tag?: string;
}

interface ServiceWorkerRegistration {
    active?: ServiceWorker;
    installing?: ServiceWorker;
    onupdatefound?: (event?: Event) => any;
    pushManager: PushManager;
    scope: string;
    waiting?: ServiceWorker;
    getNotifications(options?: ServiceWorkerNotificationOptions): Promise<Array<Notification>>;
    update(): void;
    unregister(): Promise<boolean>;
}

interface ServiceWorkerRegistrationOptions {
    scope?: string;
}

type ServiceWorkerState = "installing" | "installed" | "activating" | "activated" | "redundant";

// CacheStorage API
interface Cache {
    add(request: Request): Promise<void>;
    addAll(requestArray: Array<Request>): Promise<void>;
    'delete'(request: Request, options?: CacheStorageOptions): Promise<boolean>;
    keys(request?: Request, options?: CacheStorageOptions): Promise<Array<string>>;
    match(request: Request, options?: CacheStorageOptions): Promise<Response>;
    matchAll(request: Request, options?: CacheStorageOptions): Promise<Array<Response>>;
    put(request: Request|string, response: Response): Promise<void>;
}

interface CacheStorage {
    'delete'(cacheName: string): Promise<boolean>;
    has(cacheName: string): Promise<boolean>;
    keys(): Promise<Array<string>>;
    match(request: Request, options?: CacheStorageOptions): Promise<Response>;
    open(cacheName: string): Promise<Cache>;
}

interface CacheStorageOptions {
    cacheName?: string;
    ignoreMethod?: boolean;
    ignoreSearch?: boolean;
    ignoreVary?: boolean;
}

interface FetchEvent extends Event {
    request: Request;
    respondWith(response: Promise<Response>|Response): Promise<Response>;
}

interface InstallEvent extends ExtendableEvent {
    activeWorker: ServiceWorker;
}

interface ActivateEvent extends ExtendableEvent {
}

interface Headers {
    new(init?: any): Headers;
    append(name: string, value: string): void;
    'delete'(name: string): void;
    entries(): Array<Array<string>>;
    get(name: string): string;
    getAll(name: string): Array<string>;
    has(name: string): boolean;
    keys(): Array<string>;
    set(name: string, value: string): void;
    values(): Array<string>;
}


type BufferSource = ArrayBuffer | ArrayBufferView;
type ReferrerPolicy = "" | "no-referrer" | "no-referrer-when-downgrade" | "origin-only" | "origin-when-cross-origin" |
    "unsafe-url";
type RequestCache = "default" | "no-store" | "reload" | "no-cache" | "force-cache";
type RequestCredentials = "omit" | "same-origin" | "include";
type RequestMode = "cors" | "no-cors" | "same-origin" | "navigate";
type RequestRedirect = "follow" | "error" | "manual";
type ResponseType = "basic" | "cores" | "error" | "opaque";

// Notification API
interface Notification {
    body: string;
    data: any;
    icon: string;
    lang: string;
    requireInteraction: boolean;
    silent: boolean;
    tag: string;
    timestamp: number;
    title: string;
    close(): void;
    requestPermission(): Promise<string>;
}

interface NotificationEvent {
    action: string;
    notification: Notification;
}

// Push API
interface PushEvent extends ExtendableEvent {
    data: PushMessageData;
}

interface PushManager {
    getSubscription(): Promise<PushSubscription>;
    permissionState(): Promise<string>;
    subscribe(): Promise<PushSubscription>;
}

interface PushMessageData {
    arrayBuffer(): ArrayBuffer;
    blob(): Blob;
    json(): any;
    text(): string;
}

interface PushSubscription {
    endpoint: string;
    getKey(method: string): ArrayBuffer;
    toJSON(): string;
    unsubscribe(): Promise<boolean>;
}

// Sync API
interface SyncEvent extends Event {
    lastChance: boolean;
    tag: string;
}

// ServiceWorkerGlobalScope
declare var Headers: Headers;
declare var Request: Request;
declare var caches: CacheStorage;
declare var clients: Clients;
declare var onactivate: (event?: ExtendableEvent) => any;
declare var onfetch: (event?: FetchEvent) => any;
declare var oninstall: (event?: ExtendableEvent) => any;
declare var onmessage: (event: MessageEvent) => any;
declare var onnotificationclick: (event?: NotificationEvent) => any;
declare var onnotificationclose: (event?: NotificationEvent) => any;
declare var onpush: (event?: PushEvent) => any;
declare var onpushsubscriptionchange: () => any;
declare var onsync: (event?: SyncEvent) => any;
declare var registration: ServiceWorkerRegistration;

declare function fetch(request: Request|string): Promise<Response>;
declare function skipWaiting(): void;
*/