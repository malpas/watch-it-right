import axios from "axios";
import { action, observable } from "mobx";

export class ExploreStore {
  @observable movies = [];
  @observable filter = "";
  @observable error = "";
  @observable searchError = "";
  @observable selectedMovie = null;
  @observable suggestedMovie = null;
  @observable posterUrls = observable.map();

  @action
  searchMovies() {
    if (this.filter === "") {
      this.movies = [];
      return;
    }
    this.searchError = "";
    axios
      .get(`/api/search?title=${this.filter}`)
      .then(response => {
        if (response.data.error) {
          this.searchError = response.data.error;
          return;
        }
        if (Array.isArray(response.data)) {
          this.movies = response.data;
        }
      })
      .catch(err => (this.searchError = "Could not reach server."));
  }

  @action
  setFilter(text) {
    this.filter = text;
  }

  @action
  selectViewedMovie(id) {
    this.selectedMovie = null;
    var selectedMovieId = id;
    axios
      .get(`/api/movie/${selectedMovieId}`)
      .then(response => {
        this.selectedMovie = response.data;
      })
      .catch(err => (this.selectedMovie = {}));
  }

  @action
  getBingeItOrBinItSuggestion() {
    axios
      .get("/api/binbinge")
      .then(response => {
        if (!response.data.error) {
          this.suggestedMovie = response.data;
        } else {
          this.suggestedMovie = null;
          this.error = response.data.error;
        }
      })
      .catch(response => (this.error = "Could not reach server."));
  }

  @action
  createBingeItOrBinItSuggestion(useUserGenres) {
    axios.post(`/api/binbinge`).then(response => {
      if (!response.data.error) {
        this.suggestedMovie = response.data;
      } else {
        this.suggestedMovie = null;
      }
      this.getBingeItOrBinItSuggestion();
    });
  }

  @action
  bingeSuggestion() {
    axios.post("/api/binbinge/rate?action=binge");
    this.createBingeItOrBinItSuggestion();
  }

  @action
  binSuggestion() {
    axios.post("/api/binbinge/rate?action=bin");
    this.createBingeItOrBinItSuggestion();
  }

  @action
  getPosterUrl(id) {
    if (this.posterUrls[id]) {
      return this.posterUrls.get(id);
    }
    axios.get(`/api/movie/${id}/poster`).then(response => {
      this.posterUrls.set(id, response.data.url);
    });
  }
}
