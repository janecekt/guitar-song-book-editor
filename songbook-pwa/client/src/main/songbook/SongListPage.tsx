import * as React from "react";
import * as _ from "lodash";

import List from "material-ui/List/List";
import {ListItem} from "material-ui/List";
import Typography from "material-ui/Typography";

import {AppHeader} from "main/components/AppHeader";
import {FontAwesomeIcon, FontAwesomeIconName} from "main/components/FontAwesomeIcon";
import {TextField} from "main/components/TextField";

import {Action} from "main/logic/Action";

import {coalesce, sortBy, toSearchString} from "main/utils/Utils";
import {PersistentIndexedList} from "main/utils/IndexedList";

import {Song, SongBook} from "main/songbook/model/SongBook";
import {SongListPageState, SongOrderingType} from "main/songbook/SongListPageState";
import {songBookListPageChangedAction} from "main/songbook/actions/SongListPageChangedAction";

import './SongListPage.less';

export interface SongListPageProps extends SongListPageState {
    songBook: SongBook,
    onRefresh: (force: boolean) => void,
    goToSong: (songIndex : number) => void
    dispatch: (action : Action) => void;
}

interface OrderingData {
    ordering: SongOrderingType,
    next: SongOrderingType,
    icon: FontAwesomeIconName,
    sortKey: (it :Song) => any
}

const orderingMap = PersistentIndexedList.create<SongOrderingType,OrderingData>(
    [
        {
            ordering: "ByIndex",
            next: 'ByTitle',
            icon: 'solid-sort-numeric-down',
            sortKey: song => song.index
        },
        {
            ordering: "ByTitle",
            next: 'BySubTitle',
            icon: 'solid-sort-amount-down',
            sortKey: song => song.title
        },
        {
            ordering: "BySubTitle",
            next: 'ByIndex',
            icon: 'solid-sort-alpha-down',
            sortKey: song => song.subTitle
        }
    ],
    it => it.ordering);

export class SongListPage extends React.Component<SongListPageProps,{}> {
    componentDidMount() {
        this.props.onRefresh(false);
    }

    render() {
        const  orderingData = orderingMap.getItem(this.props.ordering);

        return <div className="songListPage">
            {this.renderHeader(orderingData)}
            {this.renderFilter()}
            {this.renderSongList(orderingData)}
        </div>
    }

    private renderHeader(orderingData : OrderingData) {

        return <AppHeader title="SongBook"
                          leftButtonIcon={'solid-sync-alt'}
                          onLeftButtonClick={() => this.props.onRefresh(true)}
                          rightButtonIcon={'solid-search'}
                          onRightButtonClick={() => this.toggleSearch()}
                          rightButton2Icon={orderingData.icon}
                          onRightButton2Click={() => this.props.dispatch(songBookListPageChangedAction("ordering", orderingData.next))}/>
    }

    private renderFilter() {
        if (!this.props.search) {
            return null;
        }
        return <div className="song-filter-container">
            <TextField
                className="song-filter"
                fullWidth={true}
                label="filter"
                value={this.props.filter}
                onChange={value => this.props.dispatch(songBookListPageChangedAction("filter", value))}
                onClear={() => this.props.dispatch(songBookListPageChangedAction("filter", null))}
            />
        </div>
    }

    private renderSongList(orderingData : OrderingData) {
        const items = (this.props.songBook !== null)
            ? this.props.songBook.songs
            : [];

        const renderedItems = sortBy(items, orderingData.sortKey, false)
                .filter(song => this.matchesFilter(song))
                .map(song => <SongListItem key={'song-'+song.index}
                                           song={song}
                                           goToSong={this.props.goToSong} />);

        return <List className="song-list">
            {renderedItems}
        </List>
    }

    private matchesFilter(song: Song) {
        if (_.isNil(this.props.filter)) {
            return true;
        }
        return toSearchString(this.props.filter)
            .split(" ")
            .map(search => song.searchText.includes(search))
            .reduce((v1,v2) => v1 && v2);
    }

    private toggleSearch() : void {
        if (this.props.search) {
            this.props.dispatch(songBookListPageChangedAction('search', false));
            this.props.dispatch(songBookListPageChangedAction('filter', null));
        } else {
            this.props.dispatch(songBookListPageChangedAction('search', true));
        }
    }
}

export interface SongListItemProps {
    song: Song;
    goToSong: (idx: number) => void;
}

class SongListItem extends React.Component<SongListItemProps,{}> {
    shouldComponentUpdate(nextProps : SongListItemProps) : boolean {
        return this.props !== nextProps;
    }

    render() {
        const song = this.props.song;

        return <ListItem className="song-item"
                         onClick={() => this.props.goToSong(song.index)}>
            <div className="song-title-block">
                <Typography className="song-title">{song.title}</Typography>
                <Typography className="song-subtitle" color="textSecondary">{coalesce(song.subTitle, '\u00A0')}</Typography>
            </div>
            <Typography className="song-index">{song.index}</Typography>
            <FontAwesomeIcon className="song-chevron" iconName='solid-chevron-right' />
        </ListItem>;
    }
}