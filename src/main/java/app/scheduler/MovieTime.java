package app.scheduler;

import app.explore.Movie;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * A MovieTime represents one scheduling of a movie. It includes the movie
 * itself, the date to be watched, and the duration of the movie.
 */
@Entity
public class MovieTime {
    //Date and time to start watching movie
    private final LocalDateTime date;

    public MovieTime(MovieTime other) {
        this.date = other.date;
        this.duration = other.duration;
        this.movie = other.movie;
        this.title = other.title;
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, duration, movie, title, id);
    }

    //Duration of movie in minutes
    private final int duration;
    @ManyToOne
    private final Movie movie;
    //Title of scheduling for presentation purposes
    private final String title;
    @Id
    @GeneratedValue
    int id;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MovieTime movieTime = (MovieTime) o;
        return duration == movieTime.duration &&
                Objects.equals(date, movieTime.date) &&
                Objects.equals(movie, movieTime.movie) &&
                Objects.equals(title, movieTime.title);
    }

    MovieTime() {
        duration = 0;
        date = null;
        title = null;
        movie = null;
    }

    /**
     * Create a scheduling based on a movie, an hour and the minute to watch
     * it (24 hour time). The number of slots can be determined by the movie
     * runtime.
     *
     * @param movie The movie to watch
     * @param date  The date to watch the movie
     */
    MovieTime(Movie movie, LocalDateTime date) {
        this.movie = movie;
        this.date = date;
        this.duration = movie.getRuntime();
        String title = movie.getTitle();

        // shorten movie title if necessary
        if (title.length() > 40) {
            title = title.substring(0, 40).trim() + "...";
        }

        title += String.format(" (%d)", movie.getYear());
        this.title = title;
    }

    /**
     * Checks if two schedules movie watching times isOverlapping.
     *
     * @param movieTime The MovieTime to compare
     * @return Whether the movie times isOverlapping in watching time
     */
    boolean isOverlapping(MovieTime movieTime) {
        return !getStart().isAfter(movieTime.getEnd())
                && !movieTime.getStart().isAfter(getEnd());
    }

    public Movie getMovie() {
        return movie;
    }

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    public LocalDateTime getStart() {
        return date;
    }

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    public LocalDateTime getEnd() {
        return date.plusMinutes(duration);
    }

    public int getDuration() {
        return duration;
    }

    public String getTitle() {
        return title;
    }
}
