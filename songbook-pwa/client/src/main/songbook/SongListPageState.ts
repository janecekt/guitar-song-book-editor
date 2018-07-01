export type SongOrderingType = 'ByIndex' | 'ByTitle' | 'BySubTitle';

export interface SongListPageState {
    search: boolean;
    filter: string;
    ordering: SongOrderingType;
}