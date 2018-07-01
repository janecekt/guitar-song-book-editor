import * as _ from "lodash";
import {Song, SongBook, TextFragment} from "main/songbook/model/SongBook";
import {ServerError} from "main/service/ServerError";
import {toSearchString} from "main/utils/Utils";
import {PersistentIndexedList} from "main/utils/IndexedList";

class SongBookService {
    public async loadSongBook(url : string) : Promise<SongBook> {
        const rawSongBook = await this.fetchSongBook(url);

        const songs = rawSongBook.songs
            .map(song => Object.assign({}, song, {
                searchText: this.buildSearchText(song)
            }));

        return {
            songs: songs,
            songIndex: PersistentIndexedList.create<string,Song>(songs, it => "" + it.index)
        }
    }

    private buildSearchText(song : Song) : string {
        return [
            song.index,
            toSearchString(song.title),
            toSearchString(song.subTitle),
            song.verses
                .map(verse =>
                    verse.lines
                        .map(line =>
                            line.fragments
                                .filter(fragment => fragment.type == 'Text')
                                .map(fragment => toSearchString((fragment as TextFragment).text))
                                .join(" "))
                        .join(" "))
                .join(" ")
        ].join(" ");
    }

    private async fetchSongBook(url : string) : Promise<SongBook> {
        let headers = new Headers();
        headers.append('X-Cache-Permanently', 'true');

        let init : RequestInit = {
            method: 'GET',
            headers: headers
        };
        const response = await this.doFetch(url, init);
        return this.processResponse(response);
    }

    // Do fetch and convert potential error to ServerError
    private async doFetch(path : string, init?: RequestInit) : Promise<Response> {
        try {
            return await fetch(path, init);
        } catch (err) {
            const statusText = _.isNil(err.message) ? '' : " (" + err.message + ")";
            throw new ServerError(0, "CommunicationError", "Nepodařilo se navázat spojení - buď nefunguje server nebo nejste pripojeni k internetu." + statusText);
        }
    }

    private async processResponse<T>(response : Response) : Promise<any> {
        // Handle ErrorResponse
        if (!response.ok) {
            throw new ServerError(response.status, "LoadingError", "LoadingError: " + response.statusText);
        }
        // Handle normal response
        try {
            return await response.json();
        } catch (err) {
            const statusText = _.isNil(err.message) ? '' : " (" + err.message + ")";
            throw new ServerError(-1, "DeserializationError", "Failed to parse JSON response: " + statusText);
        }

    }
}

export default new SongBookService();