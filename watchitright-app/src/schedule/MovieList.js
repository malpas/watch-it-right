import "./MovieList.css";
import React, { Component } from "react";
import { Link } from "react-router-dom";
import { inject, observer } from "mobx-react";
import ReactTooltip from "react-tooltip";

@inject("UserStore")
@observer
export class MovieList extends Component {
  handleRemoveMovie(id) {
    console.log(id);
    this.props.UserStore.removeMovieToWatch(id);
  }
  render() {
    const movies = this.props.movies;

    if (!movies) {
      return <div>No movies...</div>;
    }
    return (
      <div>
        <table className="MovieList">
          <tr>
            <th>Movie</th>
            <th>Year</th>
            <th>Genres</th>
            <th />
          </tr>
          {movies.map((movie, i) => (
            <tr>
              <td>
                <Link to={`/movie/${movie.id}`}>{movie.title}</Link>
              </td>
              <td>{movie.year}</td>
              <td>{movie.genres.join(", ")}</td>
              <td className="ActionColumn">
                <button
                  data-tip="Remove from watchlist"
                  onClick={() => this.handleRemoveMovie(movie.id)}
                >
                  X
                </button>
              </td>
            </tr>
          ))}
        </table>
        {this.props.movies.length === 0 ? (
          <p>You have no movies on your watch list.</p>
        ) : null}
        <ReactTooltip delayShow={3000} />
      </div>
    );
  }
}
