import "./Home.css";
import Explore from "../assets/explore.png";
import Search from "../assets/search.png";
import Schedule from "../assets/schedule.png";

import React, { Component } from "react";
import { inject, observer } from "mobx-react";
import { RegisterOrLoginForm } from "./RegisterOrLoginForm";
import { Calendar } from "../schedule/Calendar";
import { MovieList } from "../schedule/MovieList";

@inject("UserStore", "LayoutStore", "ScheduleStore")
@observer
class Home extends Component {
  componentWillMount() {
    this.props.ScheduleStore.refreshData();
    this.props.UserStore.refreshData();
    this.props.LayoutStore.hideDropdown();
  }

  render() {
    const { loggedIn, moviesToWatch } = this.props.UserStore;
    const { calendarEvents } = this.props.ScheduleStore;
    const scheduleSection = (
      <React.Fragment>
        <h2>Your Schedule</h2>
        {calendarEvents.length > 0 ? (
          <div>
            <Calendar calendarEvents={calendarEvents} />
          </div>
        ) : (
          <p>You don't have a schedule yet.</p>
        )}
      </React.Fragment>
    );

    const watchlistSection = (
      <React.Fragment>
        <h2>Your Watchlist</h2>
        <MovieList movies={moviesToWatch} />
      </React.Fragment>
    );

    const content = loggedIn ? (
      <div className="Content">
        <div className="responsive-cols">
          <div className="col">{scheduleSection}</div>
          <div className="col">{watchlistSection}</div>
        </div>
      </div>
    ) : (
      <div>
        <div className="Blurb">
          <h2>Binge-watch movies? Do it right.</h2>
          <p>
            Watch It Right lets you manage your movie watching schedule. Add the
            shows you love, then maximize your binge-watching efficiency with
            our patent-pending* scheduler.
          </p>
          <RegisterOrLoginForm />
        </div>
        <div />
        <div className="Blurb ScheduleBlurb">
          <div>
            <h2>Schedule it.</h2>
            <p>
              Find the movies you love, then build the optimal schedule to watch
              them.
            </p>
            <p>You can:</p>
            <ul>
              <li>Tailor the schedule to your free time</li>
              <li>Export your schedule to PDF</li>
              <li>Generate your own RSS feed</li>
            </ul>
            <img alt="Schedule page" src={Schedule} />
          </div>
        </div>
        <div className="Blurb SearchBlurb">
          <div>
            <h2>Search it.</h2>
            <p>
              Watch It Right's powerful search engine uses fuzzy string matching
              with constraints to find movies you're looking for.
            </p>
            <p>It's easy and efficent. Here are a few examples.</p>

            <table className="QueryList">
              <tr>
                <th>Task</th>
                <th>Search Query</th>
              </tr>
              <tr>
                <td>Find the Lord of the Rings films</td>
                <td>lordtr</td>
              </tr>
              <tr>
                <td>Catch up on the latest animated films</td>
                <td>genre:animation after:2013</td>
              </tr>
              <tr>
                <td>Rewatch the Beverly Hills Cop films</td>
                <td>bevhc</td>
              </tr>
              <tr>
                <td>Find old drama, thriller films</td>
                <td>genre:drama genre:thriller before:1970</td>
              </tr>
              <tr>
                <td>Keep up with the Spiderman remakes</td>
                <td>spiderman after:2010</td>
              </tr>
            </table>
            <img alt="Search page" src={Search} />
          </div>
        </div>
        <div className="Blurb ExploreBlurb">
          <div>
            <h2>Explore it.</h2>
            <p>
              Find movies from people with similar tastes with Bin it or Binge
              It.
            </p>
            <p>
              Simply "bin" the movies you don't want to watch, "binge" the
              movies you do. The more you use the site, the better the
              suggestions!
            </p>
            <img alt="Explore page" src={Explore} />
          </div>
        </div>
      </div>
    );

    return content;
  }
}

export { Home };
