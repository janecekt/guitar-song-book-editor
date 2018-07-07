import * as React from "react";
import * as _ from "lodash";

import {default as MuiIconButton} from 'material-ui/IconButton';
import {FontAwesomeIcon, FontAwesomeIconName} from "main/framework/components/FontAwesomeIcon";

export class IconButtonProps {
    className?: string;
    iconName: FontAwesomeIconName;
    onClick: () => void;
    disabled?: boolean;
    propagateClickEvent?: boolean;
}

export class IconButton extends React.Component<IconButtonProps, {}> {
    static defaultProps : Partial<IconButtonProps> = {
        disabled: false,
        propagateClickEvent: true,
    };

    render() {
        return  <MuiIconButton className={this.props.className}
                               color="inherit"
                               aria-label={this.props.iconName}
                               onClick={evt => this.onClick(evt as any)}>
                    <FontAwesomeIcon iconName={this.props.iconName} />
                </MuiIconButton>
    }

    private onClick(evt : Event) {
        if (!this.props.propagateClickEvent) {
            evt.stopPropagation();
        }
        if ((this.props.disabled === false) && (!_.isNil(this.props.onClick))) {
            this.props.onClick();
        }
    }
}