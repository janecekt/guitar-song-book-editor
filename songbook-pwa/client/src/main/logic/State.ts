import {SongBook} from "main/songbook/model/SongBook";
import {SongListPageState} from "main/songbook/SongListPageState";
import {SongDetailPageState} from "main/songbook/SongDetailPageState";


export interface State {
    route: string,
    server: ServerState,
    songBook: SongBook
    songListPage: SongListPageState;
    songDetailPage: SongDetailPageState;
}

export interface ServerState {
    isBusy: boolean,
    serverError: string
}

export const INITIAL_STATE : State = {
    route: '/',
    server: {
        isBusy: false,
        serverError: null
    },
    songBook: null,
    songListPage: {
        search: false,
        filter: null,
        ordering: 'ByIndex'
    },
    songDetailPage: {
        transposeBy: 0,
        showChords: true
    }
};