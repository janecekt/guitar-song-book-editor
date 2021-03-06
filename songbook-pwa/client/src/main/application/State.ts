import {ServerState} from "main/framework/actions/LoadingDataAction";

import {SongBook} from "main/application/songbook/model/SongBook";
import {SongListPageState} from "main/application/songbook/SongListPageState";
import {SongDetailPageState} from "main/application/songbook/SongDetailPageState";

export interface State {
    route: string,
    server: ServerState,
    songBook: SongBook
    songListPage: SongListPageState;
    songDetailPage: SongDetailPageState;
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