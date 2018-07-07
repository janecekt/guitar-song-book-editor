import {Action as ReduxAction} from "redux";

export interface Action<T> extends ReduxAction<string> {
    type: string,
    payload: T
}

export function createAction<T>(type: string) : ((payload : T) =>  Action<T>) {
     return (payload : T) => {
         return {
             type: type,
             payload: payload
         }
     };
}