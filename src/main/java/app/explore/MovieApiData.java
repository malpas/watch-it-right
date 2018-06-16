package app.explore;

class MovieApiData {
    private final int tmdbId;
    private final String posterUrl;

    public MovieApiData(int tmdbId, String posterUrl) {
        this.tmdbId = tmdbId;
        this.posterUrl = posterUrl;
    }

    public int getTmdbId() {
        return tmdbId;
    }

    public String getPosterUrl() {
        return posterUrl;
    }
}
