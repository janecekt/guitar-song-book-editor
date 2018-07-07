import * as React from "react";
import * as _ from "lodash";

import Card from "material-ui/Card";
import Typography from "material-ui/Typography";
import Button from "material-ui/Button";
import {CircularProgress} from "material-ui/Progress";

import {AppHeader} from "main/framework/components/AppHeader";

import {ServerState} from "main/framework/actions/LoadingDataAction";

import './ServerStatusOverlay.less';


export interface ServerStatusOverlayProps extends ServerState {
    onServerErrorDismiss: () => void;
}

export class ServerStatusOverlay extends React.Component<ServerStatusOverlayProps, {}> {
    render() {
        // Do not render if there is no server activity
        if (!this.isBusy() && _.isNil(this.props.serverError)) {
            return null;
        }

        return <div className="server-status-overlay">
            {this.renderProgressSpinnerIfBusy()}
            {this.renderServerErrorMessageIfPresent()}
        </div>
    }

    private renderProgressSpinnerIfBusy() {
        if (!this.isBusy()) {
            return null;
        }
        return <div className="server-busy-pane">
            <CircularProgress className="busy-spinner" thickness={7} size={125} />
        </div>
    }

    private renderServerErrorMessageIfPresent() {
        if (this.isBusy() || _.isNil(this.props.serverError)) {
            return null;
        }

        return <div className="server-error-pane">
            <Card className="server-error-card">
                <AppHeader title="Chyba"  />
                <div className="server-error-content">
                    <Typography className="server-error-message">{this.props.serverError}</Typography>
                    <div className="buttons">
                        <Button className="ok-button" variant="raised" onClick={() => this.props.onServerErrorDismiss()}>OK</Button>
                    </div>
                </div>
            </Card>
        </div>
    }

    private isBusy() {
        return !_.isNil(this.props.isBusy) && (this.props.isBusy === true)
    }
}