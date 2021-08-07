import * as React from "react";

import {AppHeader} from "main/framework/components/AppHeader";
import {TextField} from "main/framework/components/TextField";

import './InfoPage.less';

export interface SongDetailPageProps {
    title: string,
    goToHome: () => void,
    songBookVersion: string
    appVersion: string,
    songbookPdfUrl: string;
}

export class InfoPage extends React.Component<SongDetailPageProps,{}> {
    render() {
        return <div className="infoPage">
            {this.renderHeader()}
            {this.renderBody()}
        </div>
    }

    private renderHeader() {
        const onShare = this.getShareFn()
            ? () => this.onShareLink(this.props.title, this.getLink())
            : null;

        return <AppHeader title={this.props.title}
                          leftButtonIcon={'solid-home'}
                          onLeftButtonClick={() => this.props.goToHome()}
                          rightButtonIcon={'solid-file-pdf'}
                          onRightButtonClick={() => this.onDownloadPdf()}
                          rightButton2Icon={'solid-share-alt'}
                          onRightButton2Click={onShare}/>
    }

    private renderBody() {
        return <div className='body-content'>
            <TextField className="link-field"
                       label="Link"
                       fullWidth={true}
                       value={this.getLink()} />

            <TextField className="songbook-version-field"
                       label="Songbook Version"
                       fullWidth={true}
                       value={this.props.songBookVersion} />

            <TextField className="app-version-field"
                       label="Version"
                       fullWidth={true}
                       value={this.props.appVersion} />
        </div>
    }

    private onDownloadPdf() {
        const songBookUrl = this.getLink(this.props.songbookPdfUrl);
        if (this.getShareFn()) {
            this.onShareLink('SongBook PDF', songBookUrl)
        } else {
            window.open(songBookUrl, '_blank');
        }
    }

    private getLink(suffix : string = '') {
        return window.location.href.replace(/#.*/, suffix);
    }

    private onShareLink(title : string, link : string) {
        const shareFn = this.getShareFn();
        if (shareFn) {
            try {
                shareFn({
                    title: title,
                    url: link,
                })
                .then(() => console.info("Shared successfully"))
                .catch((error) => console.error(error));
            } catch (ex) {
                console.error(ex);
            }
        }
    }

    private getShareFn() : (params : object) => Promise<void> {
        return ((navigator as any).share)
            ? params => (navigator as any).share(params)
            : null;
    }
}
