import {Action, createAction} from "main/framework/actions/Action";

const LOADING_DATA_TYPE = 'LOADING_DATA';

const defaultState : ServerState = {
    isBusy: false,
    serverError: null
};

export interface ServerState {
    isBusy: boolean,
    serverError: string
}

export function loadingDataAction(payload : ServerState) {
    return createAction<ServerState>(LOADING_DATA_TYPE)(payload);
}

export function loadingDataReducer(oldState : ServerState = defaultState, action : Action<any>) : ServerState {
    if (action.type === LOADING_DATA_TYPE) {
        const payload = action.payload as ServerState;
        return Object.assign({}, oldState, {
            isBusy: payload.isBusy,
            serverError: payload.serverError
        });
    }
    return (oldState === undefined) ? null : oldState;
}
