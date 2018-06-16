import React from "react";
import "./MovieGrid.css";
import { Movie } from "./Movie";

class MovieGrid extends React.Component {
  render() {
    const movies = this.props.movies;
    return (
      <div className="MovieGrid">
        {movies.map(movie => <Movie small movie={movie} key={movie.id} />)}
      </div>
    );
  }
}

export { MovieGrid };
