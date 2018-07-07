import * as React from "react";
import {Store} from "redux";

import {routeChangedAction} from "main/framework/actions/RouteChangedAction";
import {Action} from "main/framework/actions/Action";
import {Router} from "main/framework/utils/Router";

export abstract class ApplicationBase<S> extends React.Component<{}, S> {
    private storeUnsubscribe : () => void = null;

    constructor (props : any, store : Store<S,Action<any>>, context?: any) {
        super(props, context);
        this.state = store.getState();
    }

    protected abstract getStore() : Store<S,Action<any>>;

    componentDidMount() {
        // Listen to changes on store
        this.storeUnsubscribe = this.getStore().subscribe(() => this.onStoreStateChanged());

        // Listen to changes of hash
        window.addEventListener('hashchange', () => this.syncRouteToStore());
        this.syncRouteToStore()
    }

    componentWillUnmount() {
        if (this.storeUnsubscribe !== null) {
            this.storeUnsubscribe();
        }
    }

    private onStoreStateChanged() {
        const newState = this.getStore().getState();
        this.setState(newState);
    }

    private syncRouteToStore() {
        this.dispatch(routeChangedAction({newRoute: Router.getRoute()}));
    }

    protected changeRoute(route : string) {
        Router.setRoute(route);
    }

    protected dispatch(action : Action<any>) {
        this.getStore().dispatch(action);
    }
}