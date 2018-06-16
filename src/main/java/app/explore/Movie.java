package app.explore;

import app.explore.binbinge.BinBingeData;
import app.scheduler.MovieTime;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

/**
 * Movie data class. Contains information about movie such as
 * release date, title, description, genres, etc.
 */
@Entity
public class Movie {
    @Id
    @GeneratedValue
    private Integer id;

    private String title;
    private int year;
    private int runtime;
    private Integer tmdbId;
    private String posterUrl;

    @ElementCollection
    private List<String> genres;

    @Column(length = 1000)
    private String description = "No description.";

    @OneToOne(cascade = CascadeType.ALL)
    private final BinBingeData binBingeData = new BinBingeData(this, 0, 0);

    @OneToMany(mappedBy = "movie")
    List<MovieTime> movieTimes;

    public Movie() {

    }

    /**
     * Generate movie from name and year
     * @param title Movie title
     * @param year Release year of movie
     */
    public Movie(String title, String description, int year, int runtime) {
        this.title = title;
        this.description = description;
        this.year = year;
        this.runtime = runtime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getRuntime() {
        return runtime;
    }

    public Integer getTmdbId() {
        return tmdbId;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setTmdbId(Integer tmdbId) {
        this.tmdbId = tmdbId;
    }

    void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    @JsonIgnore
    public BinBingeData getBinBingeData() {
        return binBingeData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movie movie = (Movie) o;
        return year == movie.year &&
                runtime == movie.runtime &&
                Objects.equals(title, movie.title) &&
                Objects.equals(description, movie.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, year, runtime, description);
    }
}
