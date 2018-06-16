import "./Search.css";
import React, { Component } from "react";

import { SecurePage } from "../authentication";
import { MovieGrid } from "./MovieGrid";
import { observer, inject } from "mobx-react";

@inject("ExploreStore")
@observer
class Search extends Component {
  timer = null;
  handleFilterChange = e => {
    clearTimeout(this.timer);
    this.props.ExploreStore.setFilter(e.target.value);
    const that = this;
    this.timer = setTimeout(function() {
      that.props.ExploreStore.searchMovies();
    }, 1000);
  };

  render() {
    const ExploreStore = this.props.ExploreStore;
    return (
      <SecurePage className="SearchPage">
        <h2>Movies</h2>
        <input
          type="text"
          placeholder="search"
          className="SearchInput"
          value={ExploreStore.filter}
          onChange={this.handleFilterChange}
        />
        {ExploreStore.searchError ? (
          <p class="ErrorMessage">{ExploreStore.searchError}</p>
        ) : null}
        <MovieGrid movies={ExploreStore.movies} />
        <h3>Constraints</h3>
        <table className="QueryList">
          <tr>
            <th>Contraint</th>
            <th>Description</th>
          </tr>
          <tr>
            <td>genre:name</td>
            <td>
              <p>
                Find only movies in the genre.{" "}
                <p>
                  If multiple genre: constraints are used, each movie have all
                  the genres.
                </p>
              </p>
            </td>
          </tr>
          <tr>
            <td>after:year</td>
            <td>
              <p>Filter out movies before year.</p>
            </td>
          </tr>
          <tr>
            <td>before:year</td>
            <td>
              <p>Filter out movies after year.</p>
            </td>
          </tr>
        </table>
      </SecurePage>
    );
  }
}

export { Search };
