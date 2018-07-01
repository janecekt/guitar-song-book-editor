import * as React from "react";
import * as _ from "lodash";

import {default as MuiTextField} from 'material-ui/TextField';
import {InputProps, InputAdornment} from "material-ui/Input";
import {IconButton} from "main/components/IconButton";

export interface TextFieldProps {
    id?: string,
    className: string,
    type?: 'string' | 'number' | 'password'
    fullWidth?: boolean,
    label?: React.ReactNode;
    multiline?: boolean,
    rows?: number,
    value: string | number,
    onChange?: (value: string) => void,
    onClick?: () => void,
    endAdornment?: React.ReactNode,
    autoFocus?: boolean,
    autoComplete?: string,
    autoSave?: string,
    error?: string,
    disabled?: boolean,
    onClear?: () => void,
}

export class TextField extends React.Component<TextFieldProps, {}> {
    render() {
        // Input props
        let inputProps : Partial<InputProps> = {};
        if (!_.isNil(this.props.endAdornment)) {
            inputProps['endAdornment'] = this.props.endAdornment;
        } else if (!_.isNil(this.props.onClear)) {
            inputProps['endAdornment'] = this.renderClearAdornment();
        }

        // On Click
        const onClick = _.isNil(this.props.onClick)
                ? null
                : () => this.props.onClick();

        // On Change
        const onChange = _.isNil(this.props.onChange)
                ? null
                : (evt : any) => this.onChange(evt);

        return <MuiTextField id={this.props.id}
                             className={this.props.className}
                             autoFocus={this.props.autoFocus}
                             autoComplete={this.props.autoComplete}
                             autoSave={this.props.autoSave}
                             margin='normal'
                             type={this.props.type}
                             fullWidth={this.props.fullWidth}
                             label={this.props.label}
                             multiline={this.props.multiline}
                             rows={this.props.rows}
                             InputProps={inputProps}
                             value={this.props.value || ''}
                             onChange={onChange}
                             onClick={onClick}
                             error={!_.isNil(this.props.error)}
                             helperText={this.props.error}
                             disabled={this.props.disabled}
        />
    }

    private onChange(evt : any) {
        const rawValue = evt.target.value;
        const value = rawValue === '' ? null : rawValue;
        this.props.onChange(value);
    }

    private renderClearAdornment() {
        return <InputAdornment position="end">
            <IconButton className="clear-button"
                        iconName="solid-times"
                        propagateClickEvent={false}
                        onClick={this.props.onClear} />
        </InputAdornment>
    }
}