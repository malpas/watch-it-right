import "./BingeItOrBinIt.css";
import React, { Component } from "react";
import { inject, observer } from "mobx-react";
import { SecurePage } from "../authentication";
import { Movie } from "./Movie";
import ReactTooltip from "react-tooltip";

@inject("ExploreStore")
@observer
export default class BingeItOrBinIt extends Component {
  componentDidMount() {
    this.props.ExploreStore.getBingeItOrBinItSuggestion();
  }

  handleBinIt = e => this.props.ExploreStore.binSuggestion();
  handleBingeIt = e => this.props.ExploreStore.bingeSuggestion();
  handleGetStarted = e => {
    this.props.ExploreStore.createBingeItOrBinItSuggestion(false);
  };

  render() {
    const { suggestedMovie, error } = this.props.ExploreStore;
    if (!suggestedMovie) {
      return (
        <SecurePage>
          <h1>Bin It Or Binge It</h1>
          {error ? <p>{error}</p> : <p>Finding you a suggestion...</p>}
        </SecurePage>
      );
    }
    return (
      <SecurePage>
        <h1>Binge It Or Bin It</h1>
        <div className="SuggestionWrapper">
          <div className="Suggestion responsive-cols">
            <div className="col">
              <button data-tip="Don't watch" onClick={this.handleBinIt}>
                Bin It
              </button>
            </div>
            <div className="Movie-col">
              <Movie movie={suggestedMovie} />
            </div>
            <div className="col">
              <button
                data-tip="Watch it!"
                className="col"
                onClick={this.handleBingeIt}
              >
                Binge It
              </button>
            </div>
          </div>
        </div>
        <h2>Overview</h2>
        <p>{suggestedMovie.description}.</p>
        <ReactTooltip delayShow={3000} />
      </SecurePage>
    );
  }
}
