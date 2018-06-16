import "./Schedule.css";

import React, { Component } from "react";
import { MovieList } from "./MovieList";
import { SecurePage } from "../authentication";
import { inject, observer } from "mobx-react";
import { Calendar } from "./Calendar";
import { Popup } from "./Popup";
import { ScheduleParameters } from "./ScheduleParameters";

@inject("UserStore", "ScheduleStore")
@observer
export class Schedule extends Component {
  handleCreateSchedule = e => this.props.ScheduleStore.createSchedule();
  handleClearSchedule = e => this.props.ScheduleStore.clearSchedule();
  handleShowRssFeedPopup = e => this.props.ScheduleStore.toggleRssFeedPopup();
  handleClearWatchList = e => this.props.UserStore.clearWatchList();
  handleShowPDF = e => (window.location = "/schedule/pdf");

  componentDidMount() {
    this.props.UserStore.refreshData();
    this.props.ScheduleStore.refreshData();
    this.props.ScheduleStore.hideRssFeedPopup();
  }

  render() {
    const {
      hasSchedule,
      showRssFeedPopup,
      rssFeedLink,
      message
    } = this.props.ScheduleStore;
    const UserStore = this.props.UserStore;
    const { moviesToWatch } = UserStore;
    var clearWatchListMessage = () => {
      if (UserStore.clearWatchListClicks === 0) {
        return "Clear Watchlist";
      } else if (UserStore.clearWatchListClicks === 1) {
        return "Are you sure?";
      } else {
        return "Really sure?";
      }
    };
    const content = hasSchedule ? (
      <div>
        <h2>Your Schedule</h2>
        <div>
          <Calendar calendarEvents={this.props.ScheduleStore.calendarEvents} />
        </div>
        <button onClick={this.handleClearSchedule}>Clear Schedule</button>
        <button onClick={this.handleShowRssFeedPopup}>RSS</button>
        <button onClick={this.handleShowPDF}>PDF</button>
      </div>
    ) : (
      <div>
        <h2>Your Movies</h2>
        {moviesToWatch.length > 0 ? (
          <div>
            <MovieList movies={moviesToWatch} />
            <button onClick={this.handleCreateSchedule}>Create schedule</button>
            <button onClick={this.handleClearWatchList}>
              {clearWatchListMessage()}
            </button>
            {message ? <p class="ScheduleMessage">{message}</p> : null}
            <ScheduleParameters />
          </div>
        ) : (
          <div>
            <p>
              <p>
                You've got no movies. Search or Explore to create your
                watchlist!
              </p>
            </p>
          </div>
        )}
      </div>
    );
    return (
      <SecurePage>
        <Popup show={showRssFeedPopup}>
          <p>
            Your RSS feed is:&nbsp;
            <a href={rssFeedLink}>{rssFeedLink}</a>
          </p>
          <button onClick={this.handleShowRssFeedPopup}>Close</button>
        </Popup>
        {content}
      </SecurePage>
    );
  }
}
