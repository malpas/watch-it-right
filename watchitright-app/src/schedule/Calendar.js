import "./Calendar.css";

import React, { Component } from "react";
import $ from "jquery";

import "fullcalendar";
import "./fullcalendar.min.css";

export class Calendar extends Component {
  componentDidMount() {
    //FullCalendar does not interface with React, so use jQuery instead
    $("#ScheduleCalendar").fullCalendar({
      events: this.props.calendarEvents,
      defaultView: "listWeek",
      height: "auto",
      eventClick: function(event, element) {
        window.location = `/movie/${event.movie.id}`;
      },
      eventRender: function(event, element) {
        element.css("cursor", "pointer");
      }
    });
  }
  render() {
    return <div id="ScheduleCalendar" />;
  }
}
