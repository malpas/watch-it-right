import React, { Component } from "react";
import { inject, observer } from "mobx-react";

@inject("UserStore")
@observer
export class RegistrationForm extends Component {
  handleRegister = e => this.props.UserStore.register();
  handleUsernameChange = e =>
    this.props.UserStore.updateRegistrationForm("username", e.target.value);
  handlePasswordChange = e =>
    this.props.UserStore.updateRegistrationForm("password", e.target.value);
  handlePasswordReentryChange = e =>
    this.props.UserStore.updateRegistrationForm(
      "passwordReentry",
      e.target.value
    );
  handleEnterRegister = e => {
    if (e.key === "Enter") {
      this.props.UserStore.register();
    }
  };

  render() {
    const { registrationForm, loggingIn } = this.props.UserStore;
    return !loggingIn ? (
      <div>
        <div className="Form">
          <input
            type="text"
            placeholder="username"
            onKeyDown={this.handleEnterRegister}
            onChange={this.handleUsernameChange}
          />
          <input
            type="password"
            placeholder="password"
            onKeyDown={this.handleEnterRegister}
            onChange={this.handlePasswordChange}
          />
          <input
            type="password"
            placeholder="password again"
            onKeyDown={this.handleEnterRegister}
            onChange={this.handlePasswordReentryChange}
          />
          <button value="Register" onClick={this.handleRegister}>
            Register
          </button>
        </div>
        {registrationForm.message ? (
          <p className="ErrorMessage">{registrationForm.message}</p>
        ) : null}
      </div>
    ) : (
      <p>Registering...</p>
    );
  }
}
