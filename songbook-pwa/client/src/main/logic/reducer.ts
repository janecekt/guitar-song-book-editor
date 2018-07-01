import {combineReducers, Reducer} from "redux";

import {State} from "main/logic/State";
import {Action} from "main/logic/Action";

import {routeChangedReducer} from "main/logic/RouteChanged";
import {loadingDataReducer} from "main/logic/LoadingData";
import {songBookLoadedReducer} from "main/songbook/actions/SongBookLoadedAction";
import {songBookListPageReducer} from "main/songbook/actions/SongListPageChangedAction";
import {songDetailPageReducer} from "main/songbook/actions/SongDetailPageChangedAction";


function composeReducers<S>(reducers : Reducer<S>[] ) : Reducer<S> {
    return function (oldState : S, action : Action) : S {
        let state = oldState;
        for (let i=0; i<reducers.length; i++) {
            let reducer = reducers[i];
            state = reducer(state, action);
        }
        return state;
    }
}

const combinedReducers = composeReducers([
    combineReducers<State>({
        route: routeChangedReducer,
        server: loadingDataReducer,
        songBook: songBookLoadedReducer,
        songListPage: songBookListPageReducer,
        songDetailPage: songDetailPageReducer,
    })]);


const loggingReducer = function (oldState : State, action : Action) : State {
    console.log("Action: ", action, "OldState:", oldState);

    const newState = combinedReducers(oldState, action);

    console.log("NewState:", newState);
    return newState;
};

export default loggingReducer;