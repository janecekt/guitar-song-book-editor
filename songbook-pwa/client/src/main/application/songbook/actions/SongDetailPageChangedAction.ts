import {Action} from "main/framework/actions/Action";
import {SongDetailPageState} from "main/application/songbook/SongDetailPageState";

const SONG_DETAIL_PAGE_CHANGED = 'SONG_DETAIL_PAGE_CHANGED';

export interface SongDetailPageChangedActionPayload<K extends keyof SongDetailPageState> {
    field: K
    value : SongDetailPageState[K]
}

export function songDetailPageChangedAction<K extends keyof SongDetailPageState>(field: K, value: SongDetailPageState[K]) : Action<SongDetailPageChangedActionPayload<K>> {
    return {
        type: SONG_DETAIL_PAGE_CHANGED,
        payload: {
            field: field,
            value: value
        }
    };
}

export function songDetailPageReducer(oldState : SongDetailPageState, action : Action<any>) : SongDetailPageState {
    if (action.type === SONG_DETAIL_PAGE_CHANGED) {
        let payload = action.payload as SongDetailPageChangedActionPayload<any>;
        return Object.assign({}, oldState, {
            [payload.field] : payload.value
        })
    }
    return (oldState === undefined) ? null : oldState;
}