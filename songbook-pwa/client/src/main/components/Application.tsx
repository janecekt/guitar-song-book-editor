import * as React from "react";

// Import FontAwesome icons
import '@fortawesome/fontawesome-free-webfonts/css/fontawesome.css';
import '@fortawesome/fontawesome-free-webfonts/css/fa-solid.css';
import '@fortawesome/fontawesome-free-webfonts/css/fa-regular.css';

// Theme
import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider';
import createMuiTheme from 'material-ui/styles/createMuiTheme';


// Routing
import {Router} from "main/utils/Router";

// Redux
import {State} from "main/logic/State";
import {Action} from "main/logic/Action";
import {loadingDataAction} from "main/logic/LoadingData";

// Services
import {default as SongBookService} from "main/songbook/service/SongBookService";
import {ServerError} from "main/service/ServerError";
import {SongBook} from "main/songbook/model/SongBook";
import {songBookLoadedAction} from "main/songbook/actions/SongBookLoadedAction";


// Pages
import {ServerStatusOverlay} from "main/components/ServerStatusOverlay";
import {SongListPage} from "main/songbook/SongListPage";
import {SongDetailPage} from "main/songbook/SongDetailPage";

import './Application.less'

const pbtTheme = createMuiTheme({
    palette: {
        primary: { main: '#3F51B5' }
    }
});

export interface ApplicationProps {
    dispatch: (action : Action) => void;
    changeRoute: (newRoute : string) => void;
    state: State
}

export class Application extends React.Component<ApplicationProps, {}> {
    render() {
        return <div className="application">
            <MuiThemeProvider theme={pbtTheme}>
                <ServerStatusOverlay
                    {... this.props.state.server}
                    onServerErrorDismiss={() => this.onServerErrorDismiss()} />
                {this.getComponentBasedOnRoute()}
            </MuiThemeProvider>
            </div>;
    }

    getComponentBasedOnRoute() : React.ReactNode {
        let route = Router.getRoute();

        const version = (window as any).appVersion as string;

        return Router
            .add('/',
                () => <SongListPage {... this.props.state.songListPage}
                                            songBook={this.props.state.songBook}
                                            onRefresh={force => this.loadSongBook(force)}
                                            goToSong={songIndex => this.goToSong(songIndex)}
                                            dispatch={this.props.dispatch} />)

            .add('/song/([1-9][0-9]*)',
                args => <SongDetailPage {... this.props.state.songDetailPage}
                                        songIndex={parseInt(args[0])}
                                        songBook={this.props.state.songBook}
                                        onRefresh={force => this.loadSongBook(force)}
                                        changeRoute={this.props.changeRoute}
                                        dispatch={this.props.dispatch} />)
            .dispatch(route,
                () => <div>Unknown route</div>);
    }


    private loadData(loader: () => Promise<any>) : void {
        this.props.dispatch(loadingDataAction(true, null));
        loader()
            .then(() => {
                this.props.dispatch(loadingDataAction(false, null));
            })
            .catch((error : ServerError) => this.onServerError(error));
    }

    private onServerError(error : ServerError) {
        this.props.dispatch(loadingDataAction(false, error.message));
    }

    private onServerErrorDismiss() {
        this.props.dispatch(loadingDataAction(this.props.state.server.isBusy, null));
    }

    private async loadSongBook(force: boolean) : Promise<void> {
        if (force || this.props.state.songBook === null) {
            const songBookUrl = (window as any).songBookUrl as string;

            return this.loadData(async () => {
                    let songBook : SongBook = await SongBookService.loadSongBook(songBookUrl);
                    this.props.dispatch(songBookLoadedAction(songBook));
                }
            );
        }
    }

    private goToSong(songIndex: number) : void {
        this.props.changeRoute("/song/" + songIndex);
    }
}