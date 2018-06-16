import "react-slidedown/lib/slidedown.css";
import "./Dropdown.css";

import React, { Component } from "react";
import { Link } from "react-router-dom";
import { SlideDown } from "react-slidedown";
import { inject, observer } from "mobx-react";

@inject("LayoutStore")
@inject("UserStore")
@observer
export class Dropdown extends Component {
  handleClickHamburger = e => this.props.LayoutStore.toggleDropdown();
  handleLogout = e => {
    this.props.UserStore.logout();
  };

  componentDidMount() {
    document.body.onclick = e => {
      if (
        e.target.className !== "DropdownContent" &&
        !e.target.className.includes("Hamburger")
      ) {
        this.props.LayoutStore.hideDropdown();
      }
    };
  }

  secureLink(link, text) {
    if (this.props.UserStore.loggedIn) {
      return <Link to={link}>{text}</Link>;
    }
  }

  render() {
    const { showDropdownContent } = this.props.LayoutStore;
    const { loggedIn, username } = this.props.UserStore;

    const dropdownContent = showDropdownContent ? (
      <div className="DropdownContent">
        <Link to="/explore" className="hideDesktop">
          Explore
        </Link>
        <Link to="/search" className="hideDesktop">
          Search
        </Link>
        <Link to="/schedule" className="hideDesktop">
          Schedule
        </Link>
        <Link to="/account">Account</Link>
        {username === "admin" ? <a href="/admin">Admin</a> : null}
        {loggedIn ? (
          <Link to="/" onClick={this.handleLogout}>
            Logout
          </Link>
        ) : null}
      </div>
    ) : null;
    return <SlideDown className="Dropdown">{dropdownContent}</SlideDown>;
  }
}
