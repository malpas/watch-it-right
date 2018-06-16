import "./Popup.css";

import React, { Component } from "react";

export class Popup extends Component {
  render() {
    return (
      <div
        className="Popup"
        style={{ display: this.props.show ? "block" : "none" }}
      >
        {this.props.show ? (
          <div className="PopupInner Content">{this.props.children}</div>
        ) : null}
      </div>
    );
  }
}
