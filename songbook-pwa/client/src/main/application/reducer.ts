import {combineReducers} from "redux";

import {composeReducers, loggingReducer} from "main/framework/actions/Reducer";

import {INITIAL_STATE, State} from "main/application/State";

import {Action} from "main/framework/actions/Action";
import {routeChangedReducer} from "main/framework/actions/RouteChangedAction";
import {loadingDataReducer} from "main/framework/actions/LoadingDataAction";
import {songBookLoadedReducer} from "main/application/songbook/actions/SongBookLoadedAction";
import {songBookListPageReducer} from "main/application/songbook/actions/SongListPageChangedAction";
import {songDetailPageReducer} from "main/application/songbook/actions/SongDetailPageChangedAction";

const combinedReducers = composeReducers<State>([
    combineReducers<State>({
        route: routeChangedReducer,
        server: loadingDataReducer,
        songBook: songBookLoadedReducer,
        songListPage: songBookListPageReducer,
        songDetailPage: songDetailPageReducer,
    })]);


export function applicationReducer(oldState : State = INITIAL_STATE, action: Action<any>) : State {
    return loggingReducer(oldState, action, combinedReducers);
}