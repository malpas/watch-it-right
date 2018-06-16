import "./MoviePage.css";

import React, { Component } from "react";
import { SecurePage } from "../authentication";
import { observer, inject } from "mobx-react";
import { Movie } from "./Movie";

@inject("ExploreStore", "UserStore")
@observer
export class MoviePage extends Component {
  handleAdd = e => {
    const { selectedMovie } = this.props.ExploreStore;
    this.props.UserStore.addMovieToWatch(selectedMovie.id);
  };
  handleRemove = e => {
    const { selectedMovie } = this.props.ExploreStore;
    this.props.UserStore.removeMovieToWatch(selectedMovie.id);
  };

  componentWillMount() {
    var id = this.props.match.params.id;
    this.props.ExploreStore.selectViewedMovie(id);
    this.props.UserStore.refreshData(id);
  }

  render() {
    const { selectedMovie } = this.props.ExploreStore;
    const { moviesToWatch } = this.props.UserStore;
    if (!selectedMovie) {
      return (
        <SecurePage>
          <h1>Loading...</h1>
        </SecurePage>
      );
    }

    var inWatchList = false;
    for (var i = 0; i < moviesToWatch.length; i++) {
      var movie = moviesToWatch[i];
      if (movie.id === selectedMovie.id) {
        inWatchList = true;
      }
    }

    const watchButton = inWatchList ? (
      <button onClick={this.handleRemove}>Don't Watch This</button>
    ) : (
      <button onClick={this.handleAdd}>Watch This</button>
    );

    return (
      <SecurePage>
        <div>
          <h2>
            {selectedMovie.title}
            &nbsp;({selectedMovie.year})
          </h2>
          <div className="responsive-cols">
            <div className="col PosterWrapper">
              <Movie movie={selectedMovie} />
            </div>
            <div className="col MovieInfo">
              <p>{selectedMovie.description}</p>
              <h3>Genres</h3>
              <ul>
                {selectedMovie.genres
                  ? selectedMovie.genres.map(genre => <li>{genre}</li>)
                  : null}
              </ul>

              <p>
                <strong>Runtime</strong>: {selectedMovie.runtime}min
              </p>

              {watchButton}
              <button onClick={this.props.history.goBack}>Back</button>
            </div>
          </div>
        </div>
      </SecurePage>
    );
  }
}
