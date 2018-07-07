import * as React from "react";

import {createStore, Store} from 'redux';

import {Action} from "main/framework/actions/Action";
import {ApplicationBase} from "main/framework/components/ApplicationBase";

import {State} from "main/application/State";
import {applicationReducer} from "main/application/reducer";
import {Application} from "main/application/Application";


const store = createStore<State,Action<any>,any,State>(applicationReducer);

export class ApplicationWrapper extends ApplicationBase<State> {
    constructor (props : any, context?: any) {
        super(props, store, context);
        this.state = store.getState();
    }

    render() {
        return <Application dispatch={store.dispatch}
                            changeRoute={newRoute => this.changeRoute(newRoute)}
                            state={this.state} />;
    }

    protected getStore(): Store<State, Action<any>> {
        return store;
    }
}