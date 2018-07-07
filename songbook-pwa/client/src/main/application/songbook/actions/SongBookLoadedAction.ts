import {Action, createAction} from "main/framework/actions/Action";
import {SongBook} from "main/application/songbook/model/SongBook";

const SONGBOOK_LOADED = 'SONGBOOK_LOADED';

export interface SongBookLoadedActionPayload {
    songBook: SongBook
}

export const songBookLoadedAction = createAction<SongBookLoadedActionPayload>(SONGBOOK_LOADED);

export function songBookLoadedReducer(oldState : SongBook, action : Action<any>) : SongBook {
    if (action.type === SONGBOOK_LOADED) {
        const payload = action.payload as SongBookLoadedActionPayload;
        return payload.songBook;
    }
    return (oldState === undefined) ? null : oldState;
}
