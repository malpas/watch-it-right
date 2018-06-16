import { action, observable, computed, toJS } from "mobx";
import Axios from "axios";

export class ScheduleStore {
  @observable schedule = {};
  @observable message = "";

  @observable rssFeedLink = "";
  @observable showRssFeedPopup = false;

  @observable
  requirements = {
    weekdays: ["MONDAY", "TUESDAY", "WEDNESDAY"],
    hours: [15, 16, 17, 18, 19]
  };

  @computed
  get hasSchedule() {
    return this.schedule.length > 0;
  }

  @action
  refreshData() {
    this.schedule = {};
    Axios.get("/api/user/schedule")
      .then(response => {
        if (response.data === {}) {
          this.schedule = {};
          this.message = "Could not get schedule";
          return;
        }
        this.schedule = response.data;
      })
      .then(response => {
        if (!this.hasSchedule) {
          return;
        }
        Axios.get("/api/user/rss").then(response => {
          this.rssFeedLink =
            window.location.origin + "/rss/" + response.data.message;
        });
      });
  }

  @computed
  get calendarEvents() {
    return toJS(this.schedule);
  }

  @action
  createSchedule() {
    this.message = "Creating schedule!";
    Axios.put("/api/user/schedule", this.requirements).then(response => {
      this.refreshData();
      this.message = "";
    });
  }

  @action
  clearSchedule() {
    this.schedule = {};
    Axios.delete("/api/user/schedule").then(response => {
      this.refreshData();
    });
  }

  @action
  toggleRssFeedPopup() {
    console.log("hi");
    this.showRssFeedPopup = !this.showRssFeedPopup;
  }

  @action
  hideRssFeedPopup() {
    this.showRssFeedPopup = false;
  }

  @action
  toggleDayRequired(weekday) {
    if (!this.requirements.weekdays.includes(weekday)) {
      this.requirements.weekdays.push(weekday);
    } else {
      this.requirements.weekdays = this.requirements.weekdays.filter(
        day => day !== weekday
      );
    }
  }
  @action
  toggleHourRequired(hour) {
    if (!this.requirements.hours.includes(hour)) {
      this.requirements.hours.push(hour);
    } else {
      this.requirements.hours = this.requirements.hours.filter(h => h !== hour);
    }
  }

  @observable
  isDayRequired(weekday) {
    return this.requirements.weekdays.includes(weekday);
  }

  @observable
  isHourRequired(hour) {
    var { hours } = this.requirements;
    return hours.includes(hour);
  }
}
