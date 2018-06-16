import axios from "axios";
import { action, observable } from "mobx";

export class UserStore {
  @observable username = localStorage.getItem("username");
  @observable token = localStorage.getItem("token");
  @observable moviesToWatch = [];
  @observable loggingIn = false;
  @observable loggedIn = this.token !== null;
  @observable showHomeLoginForm = true;

  @observable clearWatchListClicks = 0;

  @observable
  loginForm = {
    username: "",
    password: "",
    message: ""
  };

  @observable
  registrationForm = {
    username: "",
    password: "",
    passwordReentry: "",
    message: ""
  };

  @observable
  accountForm = {
    oldPassword: "",
    newPassword: "",
    message: "",
    error: ""
  };

  @action
  updateLoginForm(key, value) {
    this.loginForm[key] = value;
  }

  @action
  updateRegistrationForm(key, value) {
    this.registrationForm[key] = value;
  }

  @action
  flipShowHomeLoginForm() {
    this.showHomeLoginForm = !this.showHomeLoginForm;
  }

  @action
  login() {
    var formData = new FormData();
    formData.append("username", this.loginForm.username);
    formData.append("password", this.loginForm.password);

    this.loggingIn = true;
    axios
      .post("/api/login", formData)
      .then(response => {
        var token = response.data.token;
        this.loggingIn = false;
        if (response.data.token) {
          this.token = token;
          localStorage.setItem("token", token);
          localStorage.setItem("username", this.loginForm.username);
          this.username = this.loginForm.username;
          this.loginForm.username = "";
          this.loginForm.password = "";
          this.loginForm.message = "";
          this.loggedIn = true;
          this.refreshData();
        } else {
          this.loginForm.message = response.data.error;
        }
      })
      .catch(e => {
        this.loggingIn = false;
        this.loginForm.message = "Could not connect to server.";
      });
  }

  @action
  refreshData() {
    axios.get("/api/user/").then(response => {
      this.moviesToWatch = response.data.moviesToWatch;
    });
  }

  @action
  logout() {
    this.loggedIn = false;
    this.username = null;
    this.token = null;
    localStorage.removeItem("username");
    localStorage.removeItem("token");
    this.loginForm.username = "";
    this.loginForm.password = "";
  }

  @action
  register() {
    this.loggingIn = true;
    var formData = new FormData();
    var { username, password, passwordReentry } = this.registrationForm;

    if (password !== passwordReentry) {
      this.registrationForm.message = "Passwords do not match";
      this.loggingIn = false;
      return;
    }

    formData.append("username", username);
    formData.append("password", password);
    axios
      .post("/api/register", formData)
      .then(response => {
        if (response.data.message === "Success") {
          this.loginForm.username = username;
          this.loginForm.password = password;
          this.login();
          this.registrationForm.message = "";
          return;
        }
        this.registrationForm.message = response.data.error;
        this.loggingIn = false;
      })
      .catch(e => {
        this.loggingIn = false;
        this.registrationForm.message = "Could not connect to server";
      });
  }

  @action
  addMovieToWatch(id) {
    var formData = new FormData();
    formData.set("id", id);
    axios.post(`/api/user/watchList/${id}`).then(response => {
      this.refreshData();
    });
  }

  @action
  removeMovieToWatch(id) {
    var formData = new FormData();
    formData.set("id", id);
    axios.delete(`/api/user/watchList/${id}`).then(response => {
      this.refreshData();
    });
  }

  @action
  clearWatchList() {
    this.clearWatchListClicks += 1;
    if (this.clearWatchListClicks === 3) {
      axios.delete("/api/user/watchList").then(this.refreshData());
      this.clearWatchListClicks = 0;
    }
  }

  @action
  updateAccountForm(key, value) {
    this.accountForm[key] = value;
  }

  @action
  changePassword() {
    var form = new FormData();
    form.set("new", this.accountForm.newPassword);
    form.set("old", this.accountForm.oldPassword);
    axios.post("/api/user/password", form).then(response => {
      this.accountForm.message = response.data.message;
      this.accountForm.error = response.data.error;
    });
    this.accountForm.newPassword = "";
    this.accountForm.oldPassword = "";
  }

  @action
  resetAccountForm() {
    this.accountForm = {
      oldPassword: "",
      newPassword: "",
      error: "",
      message: ""
    };
  }
}
