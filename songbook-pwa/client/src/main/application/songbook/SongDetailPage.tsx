import * as React from "react";
import * as _ from "lodash";

import Typography from "material-ui/Typography";

import {AppHeader} from "main/framework/components/AppHeader";
import {Swipeable} from "main/framework/components/Swipeable";

import {Action} from "main/framework/actions/Action";

import {ChordFragment, Fragment, Line, SongBook, TextFragment, Verse} from "main/application/songbook/model/SongBook";
import {SongDetailPageState} from "main/application/songbook/SongDetailPageState";
import {songDetailPageChangedAction} from "main/application/songbook/actions/SongDetailPageChangedAction";
import {transposeChord} from "main/application/songbook/SongUtils";

import './SongDetailPage.less';


export interface SongDetailPageProps extends SongDetailPageState {
    songIndex: number,
    songBook: SongBook,
    onRefresh: (force: boolean) => void,
    goToHome: () => void,
    goToSong: (index : number) => void,
    dispatch: (action : Action<any>) => void;
}

export class SongDetailPage extends React.Component<SongDetailPageProps,{}> {
    componentDidMount() {
        this.props.onRefresh(false);
    }

    render() {
        return <div className="songDetailPage">
            {this.renderHeader()}
            {this.renderSongDetail()}
        </div>
    }

    private renderHeader() {
        return <AppHeader title="SongBook"
                          leftButtonIcon={'solid-home'}
                          onLeftButtonClick={() => this.goToHome()}
                          rightButtonIcon='solid-music'
                          onRightButtonClick={() => this.toggleChords()}
                          rightButton2Icon='solid-angle-double-up'
                          onRightButton2Click={() => this.transpose(1)}
                          rightButton3Icon='solid-angle-double-down'
                          onRightButton3Click={() => this.transpose(-1)} />
    }

    private renderSongDetail() {
        if (_.isNil(this.props.songBook)) {
            return null;
        }
        const song = this.props.songBook.songIndex.getItem("" + this.props.songIndex);

        const transposeInfo = (this.props.transposeBy !== 0)
            ? <span className="song-transpose-info">({this.props.transposeBy>0 ? '+' : ''}{this.props.transposeBy})</span>
            : null;

        return <Swipeable className="songDetail"
                          gestureMinLengthPx={70}
                          gestureMaxDeviationPercentage={50}
                          onSwipeLeft={() => this.goToSong(1)}
                          onSwipeRight={() => this.goToSong(-1)}>
            <div className="song-overview-block">
                <div className="song-title-block">
                    <Typography className="song-title">{song.title}{transposeInfo}</Typography>
                    <Typography className="song-subtitle" color="textSecondary">{song.subTitle}</Typography>
                </div>
                <Typography className="song-index">{song.index}</Typography>
            </div>
            {song.verses.map((verse, idx) => this.renderVerse(idx, verse))}
        </Swipeable>
    }

    private renderVerse(idx : number, verse: Verse) {
        // Check if verse contains chords
        const containsChords = verse.lines
            .map(line =>
                    line.fragments
                        .map(fragment => fragment.type === 'Chord')
                        .reduce((v1,v2) => v1 || v2)
            )
            .reduce((v1,v2) => v1 || v2);

        const className = containsChords && this.props.showChords
                ? 'songVerse songVerseWithChords'
                : 'songVerse songVerseNoChords';

        return <div className={className}
                    key={"line-" + idx} >
            {verse.lines.map((line, idx) => this.renderLine(idx, line))}
        </div>
    }

    private renderLine(idx : number, line: Line) {
        if (this.props.showChords) {
            return <Typography className="songLine" key={"line-" + idx}>
                    {line.fragments.map((fragment, idx) => this.renderFragment(idx, fragment))}
                </Typography>
        } else {
            const lineAsText = line.fragments
                .filter(fragment => fragment.type === 'Text')
                .map(fragment => (fragment as TextFragment).text)
                .join(' ')
                .replace(/ + /, ' ')
                .replace(/ +\./, '.')
                .replace(/ +,/, ',');

            return <Typography className="songLine" key={"line-" + idx}>{lineAsText}</Typography>
        }
    }

    private renderFragment(idx : number, fragment: Fragment) {
        if (fragment.type === 'Text') {
            return this.renderTextFragment(idx, fragment as TextFragment)
        } else if (fragment.type === 'Chord') {
            return this.renderChordFragment(idx, fragment as ChordFragment);
        } else {
            console.error("Unsupported fragment skipping ... ", fragment);
            return null;
        }
    }

    private renderTextFragment(idx: number, textFragment: TextFragment) {
        return <span className="songText" key={"text-"+idx}>{textFragment.text}</span>
    }

    private renderChordFragment(idx : number, chordFragment: ChordFragment) {
        const chord1 = transposeChord(chordFragment.chord1, this.props.transposeBy);
        const chord2 = transposeChord(chordFragment.chord2, this.props.transposeBy);
        const chordText = _.isNil(chord2) ? chord1 : chord1 + "/" + chord2;
        return <span className="songChord" key={"chord-"+idx}>{chordText}</span>

    }

    private toggleChords() {
        this.props.dispatch(songDetailPageChangedAction('showChords', !this.props.showChords));
    }

    private transpose(number: number) {
        this.props.dispatch(songDetailPageChangedAction('transposeBy', this.props.transposeBy + number));
    }

    private goToHome() {
        this.props.dispatch(songDetailPageChangedAction('transposeBy', 0));
        this.props.goToHome();
    }

    private goToSong(delta: number) {
        this.props.dispatch(songDetailPageChangedAction('transposeBy', 0));

        const maxSongIdx = this.props.songBook.songs.length;
        let songIdx = 1 + ((this.props.songIndex - 1 + delta + maxSongIdx) % maxSongIdx);
        this.props.goToSong(songIdx);
    }
}
