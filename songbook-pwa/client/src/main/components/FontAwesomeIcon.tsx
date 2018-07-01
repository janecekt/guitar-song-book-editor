import * as React from "react";

export type FontAwesomeIconName =
    'solid-times'           // close "x"
    | 'solid-sync-alt'      // refresh button
    | 'solid-home'          // home button
    | 'solid-arrow-left'    // arrow back       "<-"
    | 'solid-arrow-right'   // arrow forward    "->"
    | 'solid-chevron-right' // small right arrow ">"

    | 'solid-plus'          // plus representing add
    | 'solid-edit'          // edit
    | 'solid-trash-alt'     // trash can
    | 'solid-search'        // maginifying glass

    | 'solid-volleyball-ball'
    | 'regular-calendar-alt'
    | 'regular-address-card'
    | 'solid-shopping-cart'
    | 'solid-sign-in-alt'

    | 'regular-credit-card'
    | 'solid-star'
    | 'regular-star'
    | 'solid-user-plus'
    | 'solid-user-times'

    | 'solid-sort-numeric-down'
    | 'solid-sort-amount-down'
    | 'solid-sort-alpha-down'

    | 'solid-angle-double-up'
    | 'solid-angle-double-down'
    | 'solid-music';


export class FontAwesomeIconProps {
    className?: string;
    iconName: FontAwesomeIconName
}

export class FontAwesomeIcon extends React.Component<FontAwesomeIconProps, {}> {
    static defaultProps : Partial<FontAwesomeIconProps> = { className: '' };

    render() {
        const iconClassName = this.props.iconName.startsWith('solid-')
            ? this.props.iconName.replace('solid-', 'fas fa-')
            : this.props.iconName.replace('regular-', 'far fa-');

        const className = `${iconClassName} ${this.props.className}`;
        return <i className={className} />
    }
}