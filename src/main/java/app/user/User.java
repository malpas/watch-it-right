package app.user;

import app.explore.Movie;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * User data class. Contains information necessary to login,
 * as well as app data such as the movies to watch.
 */
@Entity
public class User {
    @Id
    @GeneratedValue
    private Integer id;
    // User's unique username
    private final String username;
    // User's password (stored encoded)
    private String password;
    // User's watchlist
    @ManyToMany
    private Set<Movie> moviesToWatch;
    // User's currently suggested movie on Bin It or Binge It
    @ManyToOne
    private Movie suggestedMovie = null;
    // Movies that have been previously suggested to user
    @ManyToMany
    private List<Movie> previouslySuggestedMovies = new ArrayList<>();

    User() {
        username = null;
        password = null;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Set<Movie> getMoviesToWatch() {
        return moviesToWatch;
    }

    public void setMoviesToWatch(Set<Movie> moviesToWatch) {
        this.moviesToWatch = moviesToWatch;
    }

    @JsonIgnore
    public Integer getId() {
        return id;
    }

    @JsonIgnore
    public String getUsername() {
        return username;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    /**
     * Add a movie to the user's movies to watch list
     *
     * @param movie Movie to watch
     */
    public void addMovie(Movie movie) {
        if (moviesToWatch.size() > 500) {
            System.out.println(String.format("User %s tried to have more than" +
                    " 500 scheduled movies", username));
            return;
        }
        moviesToWatch.add(movie);
    }

    /**
     * Remove a movie from the user's watch list
     *
     * @param movie Movie to remove from watch list
     */
    public void removeMovie(Movie movie) {
        moviesToWatch.remove(movie);
    }

    public Optional<Movie> getSuggestedMovie() {
        return Optional.ofNullable(suggestedMovie);
    }

    /**
     * Set the suggested movie of the user and add the movie to the previously suggested movie list. If it exceeds
     * 20 items, clear the set.
     * @param suggestedMovie Movie to suggest
     */
    public void suggestMovie(Movie suggestedMovie) {
        previouslySuggestedMovies.add(suggestedMovie);
        if (previouslySuggestedMovies.size() > 20) {
            previouslySuggestedMovies.remove(1);
        }
        this.suggestedMovie = suggestedMovie;
    }

    public List<Movie> getPreviouslySuggestedMovies() {
        return previouslySuggestedMovies;
    }

    public void clearSuggestedMovie() {
        suggestedMovie = null;
    }

    public void clearMoviesToWatch() {
        moviesToWatch.clear();
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
