import {Action, createAction} from "main/framework/actions/Action";

const ROUTE_CHANGED_TYPE = 'ROUTE_CHANGED';

const defaultState = "/";

export interface RouteChangedActionPayload {
    newRoute: string;
}

export const routeChangedAction = createAction<RouteChangedActionPayload>(ROUTE_CHANGED_TYPE);

export function routeChangedReducer(oldState : string = defaultState, action : Action<any>) : string {
    if (action.type === ROUTE_CHANGED_TYPE) {
        let payload = action.payload as RouteChangedActionPayload;
        return payload.newRoute;
    }
    return (oldState === undefined) ? null : oldState;
}
