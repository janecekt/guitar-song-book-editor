import {Reducer} from "redux";

import {Action} from "main/framework/actions/Action";

export function composeReducers<S>(reducers : Reducer<S>[] ) : Reducer<S> {
    return function (oldState : S, action : Action<any>) : S {
        let state = oldState;
        for (let i=0; i<reducers.length; i++) {
            let reducer = reducers[i];
            state = reducer(state, action);
        }
        return state;
    }
}

export function loggingReducer<S>(oldState : S, action : Action<any>, reducer : Reducer<S>) : S {
    console.log("Action: ", action, "OldState:", oldState);

    const newState = reducer(oldState, action);

    console.log("NewState:", newState);
    return newState;
}

