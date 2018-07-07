import {Action} from "main/framework/actions/Action";
import {SongListPageState} from "main/application/songbook/SongListPageState";

const SONGBOOK_LIST_CHANGED = 'SONG_LIST_PAGE_CHANGED';

export interface SingListPageChangedActionPayload<K extends keyof SongListPageState> {
    field: K
    value : SongListPageState[K]
}

export function songBookListPageChangedAction<K extends keyof SongListPageState>(field: K, value: SongListPageState[K]) : Action<SingListPageChangedActionPayload<K>> {
    return {
        type: SONGBOOK_LIST_CHANGED,
        payload: {
            field: field,
            value: value
        }
    }
}

export function songBookListPageReducer(oldState : SongListPageState, action : Action<any>) : SongListPageState {
    if (action.type === SONGBOOK_LIST_CHANGED) {
        let songListPageChanged = action.payload as SingListPageChangedActionPayload<any>;
        return Object.assign({}, oldState, {
            [songListPageChanged.field] : songListPageChanged.value
        })
    }
    return (oldState === undefined) ? null : oldState;
}