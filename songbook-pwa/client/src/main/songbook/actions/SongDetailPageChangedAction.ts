import {INITIAL_STATE} from "main/logic/State";
import {Action} from "main/logic/Action";
import {SongDetailPageState} from "main/songbook/SongDetailPageState";

const SONG_DETAIL_PAGE_CHANGED = 'SONG_DETAIL_PAGE_CHANGED';

export interface SongDetailPageChangedAction<K extends keyof SongDetailPageState> extends Action {
    field: K
    value : SongDetailPageState[K]
}

export function songDetailPageReducer(oldState : SongDetailPageState = INITIAL_STATE.songDetailPage, action : Action) : SongDetailPageState {
    if (action.type === SONG_DETAIL_PAGE_CHANGED) {
        let songListPageChanged = action as SongDetailPageChangedAction<any>;
        return Object.assign({}, oldState, {
            [songListPageChanged.field] : songListPageChanged.value
        })
    }
    return oldState;
}

export function songDetailPageChangedAction<K extends keyof SongDetailPageState>(field: K, value: SongDetailPageState[K]) : SongDetailPageChangedAction<K> {
    return {
        type: SONG_DETAIL_PAGE_CHANGED,
        field: field,
        value: value
    }
}