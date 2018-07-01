import {INITIAL_STATE} from "main/logic/State";
import {Action} from "main/logic/Action";

const ROUTE_CHANGED_TYPE = 'ROUTE_CHANGED';

export interface RouteChangedAction extends Action {
    newRoute: string;
}

export function routeChangedReducer(oldState : string = INITIAL_STATE.route, action : Action) : string {
    if (action.type === ROUTE_CHANGED_TYPE) {
        let routeChangedAction = action as RouteChangedAction;
        return routeChangedAction.newRoute;
    }
    return oldState;
}

export function routeChangedAction(newRoute: string) : RouteChangedAction {
    return {
        type: ROUTE_CHANGED_TYPE,
        newRoute: newRoute
    }
}
