import * as React from "react";

export class Loader extends React.Component<{}, {}> {
    render() {
        return <div className="app-loader-wrapper">
                 <span className="app-loader">
                   <span className="app-loader-inner" />
                 </span>
              </div>;
    }
}