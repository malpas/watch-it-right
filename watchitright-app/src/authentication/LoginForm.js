import "./LoginRegistrationForm.css";

import React from "react";
import { inject, observer } from "mobx-react";

@inject("UserStore")
@observer
class LoginForm extends React.Component {
  changeUsername = e =>
    this.props.UserStore.updateLoginForm("username", e.target.value);
  changePassword = e =>
    this.props.UserStore.updateLoginForm("password", e.target.value);

  login = e => this.props.UserStore.login();
  handleEnterLogin = e => {
    if (e.key === "Enter") {
      this.props.UserStore.login();
    }
  };

  render() {
    const { loggingIn, loginForm } = this.props.UserStore;

    var loginP = loginForm.message ? (
      <p className="ErrorMessage">{loginForm.message}</p>
    ) : null;

    return !loggingIn ? (
      <div>
        <div className="Form">
          <input
            type="text"
            onChange={this.changeUsername}
            onKeyPress={this.handleEnterLogin}
            value={loginForm.username}
            placeholder="username"
          />
          <input
            type="password"
            onChange={this.changePassword}
            onKeyPress={this.handleEnterLogin}
            value={loginForm.password}
            placeholder="password"
          />
          <button onClick={this.login}>Login</button>
        </div>
        {loginP}
      </div>
    ) : (
      <p>Logging in...</p>
    );
  }
}

export { LoginForm };
