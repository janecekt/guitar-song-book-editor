import * as React from "react";

// Import FontAwesome icons
import '@fortawesome/fontawesome-free-webfonts/css/fontawesome.css';
import '@fortawesome/fontawesome-free-webfonts/css/fa-solid.css';
import '@fortawesome/fontawesome-free-webfonts/css/fa-regular.css';

// Theme
import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider';
import createMuiTheme from 'material-ui/styles/createMuiTheme';


// Routing
import {Router} from "main/framework/utils/Router";

// Redux
import {State} from "main/application/State";
import {Action} from "main/framework/actions/Action";
import {loadingDataAction} from "main/framework/actions/LoadingDataAction";
import {ServerError} from "main/framework/service/ServerError";

// Services
import {default as SongBookService} from "main/application/songbook/service/SongBookService";
import {SongBook} from "main/application/songbook/model/SongBook";
import {songBookLoadedAction} from "main/application/songbook/actions/SongBookLoadedAction";


// Pages
import {ServerStatusOverlay} from "main/framework/components/ServerStatusOverlay";
import {SongListPage} from "main/application/songbook/SongListPage";
import {SongDetailPage} from "main/application/songbook/SongDetailPage";
import {InfoPage} from "main/application/infoPage/InfoPage";

import './Application.less'

const pbtTheme = createMuiTheme({
    palette: {
        primary: { main: '#3F51B5' }
    }
});

export interface ApplicationProps {
    dispatch: (action : Action<any>) => void;
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
        const songBookVersion = this.getSongbookUrl()
            .replace(/.*songbook-/, '')
            .replace(/\.json/, '');

        return Router
            .add('/',
                () => <SongListPage {... this.props.state.songListPage}
                                            songBook={this.props.state.songBook}
                                            onRefresh={force => this.loadSongBook(force)}
                                            goToInfo={() => this.props.changeRoute('/info')}
                                            goToSong={songIndex => this.goToSong(songIndex)}
                                            dispatch={this.props.dispatch} />)

            .add('/song/([1-9][0-9]*)',
                args => <SongDetailPage {... this.props.state.songDetailPage}
                                        songIndex={parseInt(args[0])}
                                        songBook={this.props.state.songBook}
                                        onRefresh={force => this.loadSongBook(force)}
                                        goToHome={() => this.goToHome()}
                                        goToSong={songIndex => this.goToSong(songIndex)}
                                        dispatch={this.props.dispatch} />)

            .add('/info',
                () => <InfoPage title="SongBook"
                                appVersion={this.getAppVersion()}
                                songBookVersion={songBookVersion}
                                goToHome={() => this.goToHome()} />)

            .dispatch(route,
                () => <div>Unknown route</div>);
    }


    private loadData(loader: () => Promise<any>) : void {
        this.props.dispatch(loadingDataAction({isBusy : true, serverError: null}));
        loader()
            .then(() => {
                this.props.dispatch(loadingDataAction({isBusy : false, serverError: null}));
            })
            .catch((error : ServerError) => this.onServerError(error));
    }

    private onServerError(error : ServerError) {
        this.props.dispatch(loadingDataAction({isBusy : false, serverError: error.message}));
    }

    private onServerErrorDismiss() {
        this.props.dispatch(loadingDataAction({isBusy: this.props.state.server.isBusy, serverError: null}));
    }

    private async loadSongBook(force: boolean) : Promise<void> {
        if (force || this.props.state.songBook === null) {
            const songBookUrl = this.getSongbookUrl();
            return this.loadData(async () => {
                    let songBook : SongBook = await SongBookService.loadSongBook(songBookUrl);
                    this.props.dispatch(songBookLoadedAction({songBook: songBook}));
                }
            );
        }
    }

    private goToHome() : void {
        this.props.changeRoute("/");
    }

    private goToSong(songIndex: number) : void {
        this.props.changeRoute("/song/" + songIndex);
    }

    private getAppVersion() : string {
        return (window as any).appVersion;
    }

    private getSongbookUrl() : string {
        return (window as any).songBookUrl;
    }
}