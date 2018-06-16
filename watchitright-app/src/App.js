import React, { Component } from "react";
import "./App.css";
import "./authentication/axiosJwtInterceptor";

import { BrowserRouter as Router, Route, Switch } from "react-router-dom";
import { Provider } from "mobx-react";

import { Titlebar, Footer, LayoutStore } from "./layout";
import { Home, Legal } from "./home";
import { UserStore, AccountSettings } from "./authentication";
import { Search, MoviePage, ExploreStore } from "./explore";
import { Schedule, ScheduleStore } from "./schedule";
import BingeItOrBinIt from "./explore/BingeItOrBinIt";
import NotFound from "./home/NotFound";

export default class App extends Component {
  userStore = new UserStore();
  exploreStore = new ExploreStore();
  layoutStore = new LayoutStore();
  scheduleStore = new ScheduleStore();

  render() {
    return (
      <Provider
        UserStore={this.userStore}
        ExploreStore={this.exploreStore}
        LayoutStore={this.layoutStore}
        ScheduleStore={this.scheduleStore}
      >
        <Router>
          <main>
            <Titlebar user="John" />
            <Switch>
              <Route exact path="/" component={Home} />
              <Route path="/search" component={Search} />
              <Route path="/schedule" component={Schedule} />
              <Route path="/movie/:id" component={MoviePage} />
              <Route path="/account" component={AccountSettings} />
              <Route path="/legal" component={Legal} />
              <Route path="/explore" component={BingeItOrBinIt} />
              <Route component={NotFound} />
            </Switch>
            <Footer />
          </main>
        </Router>
      </Provider>
    );
  }
}
