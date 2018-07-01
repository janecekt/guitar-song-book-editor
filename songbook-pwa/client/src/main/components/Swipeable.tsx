import * as React from "react";
import * as _ from "lodash";

import {TouchEvent,MouseEvent} from "react";


export interface SwipeableProps {
    className: string,
    gestureMaxDeviationPercentage: number;
    gestureMinLengthPx: number;
    onSwipeLeft?: () => void;
    onSwipeRight?: () => void;
}

export class Swipeable extends React.Component<SwipeableProps, {}> {
    private startX : number = null;
    private startY : number = null;
    private lastX: number = null;
    private lastY: number = null;
    private deltaUp : number = null;
    private deltaDown : number = null;
    private deltaLeft : number = null;
    private deltaRight : number = null;

    render() {
        return <div className={this.props.className}
                    onMouseDown={evt => this.onMouseDown(evt)}
                    onMouseMove={evt => this.onMouseMove(evt)}
                    onMouseUp={() => this.onEndMove('MouseUp')}
                    onTouchStart={evt => this.onTouchStart(evt)}
                    onTouchMove={evt => this.onTouchMove(evt)}
                    onTouchEnd={() => this.onEndMove('TouchEnd')}>
            {this.props.children}
        </div>
    }

    private onMouseDown(evt: MouseEvent<any>) {
        this.clearState();
        this.onStartMove(evt.clientX, evt.clientY);
        // this.logStatus("MouseDown")
    }

    private onMouseMove(evt: MouseEvent<any>) {
        if (!_.isNil(this.startX)) {
            this.onMove(evt.clientX, evt.clientY);
            // this.logStatus("MouseMove")
        }
    }

    private onTouchStart(evt: TouchEvent<any>) {
        this.clearState();
        if (!_.isNil(evt.touches) && evt.touches.length === 1) {
            this.onStartMove(evt.touches[0].clientX, evt.touches[0].clientY);
        }
        // this.logStatus("TouchStart")
    }

    private onTouchMove(evt: TouchEvent<any>) {
        if (!_.isNil(evt.touches) && evt.touches.length === 1) {
            this.onMove(evt.touches[0].clientX, evt.touches[0].clientY);
        }
        // this.logStatus("TouchMove")
    }

    private onStartMove(x: number, y: number) {
        this.startX = x;
        this.startY = y;
        this.lastX = x;
        this.lastY = y;
    }

    private onMove(x: number, y: number) {
        this.lastX = x;
        this.lastY = y;
        const deltaX = this.lastX - this.startX;
        const deltaY = this.lastY - this.startY;
        if (deltaY > 0) {
            this.deltaDown = Math.max(this.deltaDown, deltaY);
        } else {
            this.deltaUp = Math.max(this.deltaUp, -deltaY);
        }
        if (deltaX > 0) {
            this.deltaRight = Math.max(this.deltaRight, deltaX);
        } else {
            this.deltaLeft = Math.max(this.deltaLeft, -deltaX);
        }
    }

    private onEndMove(msg: string) {
        this.logStatus(msg);

        // Classify movement
        const maxDelta = Math.max(this.deltaLeft, this.deltaRight, this.deltaUp, this.deltaDown);
        const maxDeviation = maxDelta * (this.props.gestureMaxDeviationPercentage / 100.0);
        const deltaX = Math.abs(this.startX - this.lastX);

        if (this.deltaUp < maxDeviation
            && this.deltaDown < maxDeviation
            && deltaX > this.props.gestureMinLengthPx
            && deltaX > (maxDelta-maxDeviation)) {
            if (maxDelta === this.deltaLeft && this.deltaRight < maxDeviation) {
                this.logStatus("Swipe left");
                Swipeable.invokeIfDefined(this.props.onSwipeLeft);
            }
            if (maxDelta === this.deltaRight && this.deltaLeft < maxDeviation) {
                this.logStatus("Swipe right");
                Swipeable.invokeIfDefined(this.props.onSwipeRight);
            }
        }
        this.clearState();
        // this.logStatus(msg);
    }

    private clearState() {
        this.startX = null;
        this.startY = null;
        this.lastX = null;
        this.lastY = null;
        this.deltaUp = 0;
        this.deltaDown = 0;
        this.deltaLeft = 0;
        this.deltaRight = 0;
    }

    private static invokeIfDefined(action : () => void) {
        if (!_.isNil(action)) {
            action();
        }
    }

    private logStatus(msg : string) {
        console.info(msg, "startX:", this.startX, "startY:", this.startX, "lastX", this.lastX, "lastY", this.lastY, "up", this.deltaUp, "down", this.deltaDown, "left", this.deltaLeft, "right", this.deltaRight);
    }
}