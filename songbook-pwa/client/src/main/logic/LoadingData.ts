import {INITIAL_STATE, ServerState} from "main/logic/State";
import {Action} from "main/logic/Action";

const LOADING_DATA_TYPE = 'LOADING_DATA';

export interface LoadingDataAction extends Action {
    isBusy: boolean;
    serverError: string,
}

export function loadingDataReducer(oldState : ServerState = INITIAL_STATE.server, action : Action) : ServerState {
    if (action.type === LOADING_DATA_TYPE) {
        let loadingDataAction = action as LoadingDataAction;
        return Object.assign({}, oldState, {
            isBusy: loadingDataAction.isBusy,
            serverError: loadingDataAction.serverError
        });
    }
    return oldState;
}

export function loadingDataAction(isBusy: boolean, serverError : string) : LoadingDataAction {
    return {
        type: LOADING_DATA_TYPE,
        isBusy: isBusy,
        serverError: serverError
    }
}
