import React from "react";
import { inject, observer } from "mobx-react";

import { Redirect } from "react-router";

@inject("UserStore", "LayoutStore")
@observer
export class SecurePage extends React.Component {
  componentWillMount() {
    this.props.LayoutStore.hideDropdown();
  }
  render() {
    return this.props.UserStore.loggedIn ? (
      <div className="Content">{this.props.children}</div>
    ) : (
      <Redirect to="/" />
    );
  }
}
