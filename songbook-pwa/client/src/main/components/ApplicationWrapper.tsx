import * as React from "react";

import {createStore} from 'redux';

import {Application} from "main/components/Application";

import reducer from "main/logic/reducer";
import {routeChangedAction} from "main/logic/RouteChanged";
import {State} from "main/logic/State";
import {Action} from "main/logic/Action";

import {Router} from "main/utils/Router";


const store = createStore<State,Action,any,State>(reducer);
const dispatch = store.dispatch;

export class ApplicationWrapper extends React.Component<{}, State> {
    private storeUnsubscribe : () => void = null;

    constructor (props : any, context?: any) {
        super(props, context);
        this.state = store.getState();
    }

    render() {
        return <Application dispatch={dispatch}
                            changeRoute={newRoute => this.changeRoute(newRoute)}
                            state={this.state} />;
    }

    componentDidMount() {
        // Listen to changes on store
        this.storeUnsubscribe = store.subscribe(() => this.onStoreStateChanged());

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
        const newState = store.getState();
        this.setState(newState);
    }

    private syncRouteToStore() {
        store.dispatch(routeChangedAction(Router.getRoute()));
    }

    private changeRoute(route : string) {
        Router.setRoute(route);
    }
}