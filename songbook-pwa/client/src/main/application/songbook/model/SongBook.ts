import {IndexedList} from "main/framework/utils/IndexedList";

export interface SongBook {
    songs: Song[];
    songIndex: IndexedList<string,Song>
}

export interface Song {
    title: string;
    subTitle?: string;
    index?: number;
    verses: Verse[];
    searchText: string;
}

export interface Verse {
    lines: Line[]
}

export interface Line {
    fragments: Fragment[]
}

export interface Fragment {
    type: string
}

export interface TextFragment extends Fragment{
    text: string
}

export interface ChordFragment extends Fragment {
    chord1: string,
    chord2: string,
}