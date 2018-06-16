import "./Titlebar.css";
import logo from "../assets/logo.png";
import React, { Component } from "react";

import { Link } from "react-router-dom";
import { inject, observer } from "mobx-react";
import { Dropdown } from "./Dropdown";

@inject("UserStore")
@inject("LayoutStore")
@observer
class Titlebar extends Component {
  handleLogout = e => this.props.UserStore.logout();
  handleClickHamburger = e => {
    this.props.LayoutStore.toggleDropdown();
    e.preventDefault();
  };

  render() {
    const { loggedIn } = this.props.UserStore;
    const { showDropdownContent } = this.props.LayoutStore;

    const dropdownOrLogin = loggedIn ? <Dropdown /> : null;

    const that = this;
    function hamburger(id) {
      if (loggedIn) {
        return (
          <a
            href=""
            className={`Hamburger ${id}`}
            onClick={that.handleClickHamburger}
          >
            {showDropdownContent ? "🞪" : "☰"}
          </a>
        );
      }
    }

    const desktopLinks = loggedIn ? (
      <div className="DesktopBar">
        <Link to="/explore">Explore</Link>
        <Link to="/search">Search</Link>
        <Link to="/schedule">Schedule</Link>
      </div>
    ) : null;

    return (
      <div className="TitleContainer">
        <h1>
          <Link to="/">
            <img className="Logo" src={logo} alt="Watch It Right" />
          </Link>{" "}
          {hamburger("hideDesktop")}{" "}
        </h1>
        <div className="NavBar">
          {hamburger("hideMobile")}
          {desktopLinks}

          <ul className="NavLinks">{dropdownOrLogin}</ul>
        </div>
      </div>
    );
  }
}

export { Titlebar };
