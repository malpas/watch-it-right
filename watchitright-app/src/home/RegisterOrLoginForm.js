import "./RegisterOrLoginForm.css";

import React, { Component } from "react";
import { observer, inject } from "mobx-react";
import { RegistrationForm, LoginForm } from "../authentication";

@inject("UserStore")
@observer
export class RegisterOrLoginForm extends Component {
  flipForm = e => this.props.UserStore.flipShowHomeLoginForm();

  render() {
    const { showHomeLoginForm, loggingIn } = this.props.UserStore;
    return (
      <div>
        {showHomeLoginForm ? <LoginForm /> : <RegistrationForm />}
        {!loggingIn ? (
          <p className="FakeLink" onClick={this.flipForm}>
            {showHomeLoginForm ? "Not a user?" : "Have an account?"}
          </p>
        ) : null}
      </div>
    );
  }
}
