import React, { Component } from "react";
import { inject, observer } from "mobx-react";
import { SecurePage } from ".";

@inject("UserStore")
@observer
export class AccountSettings extends Component {
  handleOldChange = e =>
    this.props.UserStore.updateAccountForm("oldPassword", e.target.value);
  handleNewChange = e =>
    this.props.UserStore.updateAccountForm("newPassword", e.target.value);
  handleChangePassword = e => this.props.UserStore.changePassword();
  handleEnterChangePassword = e => {
    if (e.key === "Enter") {
      this.props.UserStore.changePassword();
    }
  };

  componentDidMount() {
    this.props.UserStore.resetAccountForm();
  }

  render() {
    const { UserStore } = this.props;
    return (
      <SecurePage>
        <h1>Your Account</h1>
        <div class="Form">
          <input
            type="password"
            onChange={this.handleOldChange}
            onKeyDown={this.handleEnterChangePassword}
            value={UserStore.accountForm.oldPassword}
            placeholder="old password"
          />
          <input
            type="password"
            onChange={this.handleNewChange}
            onKeyDown={this.handleEnterChangePassword}
            value={UserStore.accountForm.newPassword}
            placeholder="new password"
          />
          <button onClick={this.handleChangePassword}>Change Password</button>
          {UserStore.accountForm.error ? (
            <p class="ErrorMessage">{UserStore.accountForm.error}</p>
          ) : null}
          {UserStore.accountForm.message ? (
            <p>{UserStore.accountForm.message}</p>
          ) : null}
        </div>
      </SecurePage>
    );
  }
}
