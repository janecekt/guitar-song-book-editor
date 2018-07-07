// Based on http://krasimirtsonev.com/blog/article/A-modern-JavaScript-router-in-100-lines-history-api-pushState-hash-url

export type RouterHandler = (routeArgs : string[]) => React.ReactNode;

export interface RouteDescriptor {
    pattern: string,
    handler: RouterHandler;
}

export class Router {
    private routes : RouteDescriptor[] = [];

    public static setRoute(route: string) {
        window.location.hash = route;
    }

    public static add(pattern : string, handler: RouterHandler) : Router {
        return new Router().add(pattern, handler);
    }

    public add(pattern : string, handler: RouterHandler) : Router {
        this.routes.push({ pattern: pattern, handler: handler });
        return this;
    }

    public dispatch(route : string, defaultRoute : () => React.ReactNode) : React.ReactNode {

        // Find first matching descriptor
        for(let i=0, r; i<this.routes.length, r = this.routes[i]; i++) {
            let desc = this.routes[i];

            let match = route.match('^' + desc.pattern + '$');
            if(match) {
                match.shift();
                return desc.handler(match);
            }
        }
        return defaultRoute();
    }

    public static getRoute() : string {
        let match = window.location.href.match(/#(.*)$/);
        let routeFragment = match ? match[1] : '';
        return "/" + Router.clearSlashes(routeFragment);
    }

    private static clearSlashes(path : string) : string {
        return path.toString()
            .replace(/\/$/, '')
            .replace(/^\//, '');
    }
}