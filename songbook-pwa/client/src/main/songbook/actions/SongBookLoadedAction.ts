import {INITIAL_STATE} from "main/logic/State";
import {Action} from "main/logic/Action";
import {SongBook} from "main/songbook/model/SongBook";

const SONGBOOK_LOADED = 'SONGBOOK_LOADED';

export interface SongBookLoadedAction extends Action {
    songBook: SongBook
}

export function songBookLoadedReducer(oldState : SongBook = INITIAL_STATE.songBook, action : Action) : SongBook {
    if (action.type === SONGBOOK_LOADED) {
        let songBookLoaded = action as SongBookLoadedAction;
        return songBookLoaded.songBook;
    }
    return oldState;
}

export function songBookLoadedAction(songBook : SongBook) : SongBookLoadedAction {
    return {
        type: SONGBOOK_LOADED,
        songBook: songBook
    }
}