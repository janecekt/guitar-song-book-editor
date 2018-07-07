import * as React from "react";

import AppBar from 'material-ui/AppBar';
import Toolbar from 'material-ui/Toolbar';
import Typography from 'material-ui/Typography';
import Button from 'material-ui/Button';

import {IconButton} from "main/framework/components/IconButton";
import {FontAwesomeIconName} from "main/framework/components/FontAwesomeIcon";

import './AppHeader.less';

export interface AppHeaderProps {
    leftButtonIcon?: FontAwesomeIconName,
    onLeftButtonClick?: () => void,
    rightButtonText?: string,
    rightButtonIcon?: FontAwesomeIconName,
    onRightButtonClick?: () => void,
    rightButtonDisabled?: boolean,
    rightButton2Icon?: FontAwesomeIconName,
    onRightButton2Click?: () => void,
    rightButton3Icon?: FontAwesomeIconName,
    onRightButton3Click?: () => void,
    title?: string
}

export class AppHeader extends React.Component<AppHeaderProps, {}> {
    render() {
        let title = this.props.title || 'PBT Mobile';

        return <div className="app-header">
                    <AppBar position="static">
                        <Toolbar className="toolbar">
                            {this.getLeftButton()}
                            <Typography className="title" variant="title" color="inherit">{title}</Typography>
                            {this.getRightButton()}
                            {this.getRightButton2()}
                            {this.getRightButton3()}
                        </Toolbar>
                    </AppBar>
            </div>;
    }

    private getLeftButton() {
        if (this.props.onLeftButtonClick && this.props.leftButtonIcon) {
            return <IconButton iconName={this.props.leftButtonIcon} onClick={this.props.onLeftButtonClick} />
        }
        return null;
    }

    private getRightButton() {
        const disabled = this.props.rightButtonDisabled || false;

        if (this.props.onRightButtonClick) {
            if (this.props.rightButtonIcon) {
                return <IconButton iconName={this.props.rightButtonIcon} onClick={this.props.onRightButtonClick} />;
            }
            if (this.props.rightButtonText) {
                return <Button color="inherit" onClick={this.props.onRightButtonClick} disabled={disabled}>
                    {this.props.rightButtonText}
                    </Button>
            }
        }
        return null;
    }

    private getRightButton2() {
        if (this.props.onRightButton2Click && this.props.rightButton2Icon) {
            return <IconButton iconName={this.props.rightButton2Icon} onClick={this.props.onRightButton2Click} />;
        }
        return null;
    }

    private getRightButton3() {
        if (this.props.onRightButton3Click && this.props.rightButton3Icon) {
            return <IconButton iconName={this.props.rightButton3Icon} onClick={this.props.onRightButton3Click} />;
        }
        return null;
    }
}
