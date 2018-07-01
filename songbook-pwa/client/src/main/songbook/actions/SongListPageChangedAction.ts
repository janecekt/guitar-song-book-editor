import {INITIAL_STATE} from "main/logic/State";
import {Action} from "main/logic/Action";
import {SongListPageState} from "main/songbook/SongListPageState";

const SONGBOOK_LIST_CHANGED = 'SONG_LIST_PAGE_CHANGED';

export interface SingListPageChangedAction<K extends keyof SongListPageState> extends Action {
    field: K
    value : SongListPageState[K]
}

export function songBookListPageReducer(oldState : SongListPageState = INITIAL_STATE.songListPage, action : Action) : SongListPageState {
    if (action.type === SONGBOOK_LIST_CHANGED) {
        let songListPageChanged = action as SingListPageChangedAction<any>;
        return Object.assign({}, oldState, {
            [songListPageChanged.field] : songListPageChanged.value
        })
    }
    return oldState;
}

export function songBookListPageChangedAction<K extends keyof SongListPageState>(field: K, value: SongListPageState[K]) : SingListPageChangedAction<K> {
    return {
        type: SONGBOOK_LIST_CHANGED,
        field: field,
        value: value
    }
}