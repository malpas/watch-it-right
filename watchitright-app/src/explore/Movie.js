import "./Movie.css";

import React, { Component } from "react";
import { Link } from "react-router-dom";
import { inject, observer } from "mobx-react";

@inject("ExploreStore")
@observer
export class Movie extends Component {
  componentWillMount() {
    this.props.ExploreStore.getPosterUrl(this.props.movie.id);
  }
  render() {
    const movie = this.props.movie;
    const { posterUrls } = this.props.ExploreStore;
    var style = {
      backgroundImage: `url(${posterUrls.get(movie.id)})`
    };
    if (this.props.small) {
      style.maxWidth = "200px";
      style.maxHeight = "300px";
    }
    return (
      <Link
        to={`movie/${movie.id}`}
        className="MovieItem"
        key={movie.id}
        style={style}
      >
        {!posterUrls.get(movie.id) ? (
          <p>
            {movie.title}&nbsp;({movie.year})
          </p>
        ) : null}
      </Link>
    );
  }
}
