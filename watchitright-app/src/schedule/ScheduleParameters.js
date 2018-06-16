import React, { Component } from "react";
import { observer, inject } from "mobx-react";

@inject("ScheduleStore")
@observer
export class ScheduleParameters extends Component {
  render() {
    const schedule = this.props.ScheduleStore;

    const weekdayCheckbox = weekday => (
      <p>
        <input
          type="checkbox"
          checked={schedule.isDayRequired(weekday)}
          onClick={() => schedule.toggleDayRequired(weekday)}
        />{" "}
        {toTitleCase(weekday)}
      </p>
    );

    const hourCheckbox = hour => (
      <p>
        <input
          type="checkbox"
          checked={schedule.isHourRequired(hour)}
          onClick={() => schedule.toggleHourRequired(hour)}
        />{" "}
        {hour < 12 ? `${hour + 1}am` : `${hour - 11}pm`}
      </p>
    );

    return (
      <div className="responsive-cols">
        <div className="col">
          <h3>Days Available</h3>
          {[
            "MONDAY",
            "TUESDAY",
            "WEDNESDAY",
            "THURSDAY",
            "FRIDAY",
            "SATURDAY",
            "SUNDAY"
          ].map(day => weekdayCheckbox(day))}
        </div>
        <div class="col">
          <h3>Hours Available</h3>
          <div class="cols">
            <div className="col">
              {Array.from(Array(12).keys()).map(hour => hourCheckbox(hour + 7))}
            </div>
            <div className="col">
              {Array.from(Array(12).keys()).map(hour =>
                hourCheckbox((hour + 7 + 12) % 24)
              )}
            </div>
          </div>
        </div>
      </div>
    );
  }
}

function toTitleCase(str) {
  return str.charAt(0).toUpperCase() + str.substr(1).toLowerCase();
}
