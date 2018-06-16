package app.explore.binbinge;

import app.explore.Movie;
import app.explore.MovieRepository;
import app.user.User;
import app.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;


/**
 * Service for the Bin It Or Binge it feature, which provides users with
 * suggestions on movies they might like.
 */
@Service
public class BingeBinService {
    @Autowired
    MovieRepository movieRepository;
    @Autowired
    UserRepository userRepository;

    private final ThreadLocalRandom rand = ThreadLocalRandom.current();

    public Movie getSuggestion() {
        Optional<Movie> suggestion = userRepository.getLoggedInUser().getSuggestedMovie();
        return suggestion.orElseGet(this::createSuggestion);
    }

    Movie createSuggestion() {
        User user = userRepository.getLoggedInUser();

        Set<Movie> potentialMovies = new HashSet<>();

        // get a list of movies from users with same movies
        if (user.getMoviesToWatch().size() > 0) {
            Movie randomUserMovie = new ArrayList<>(user.getMoviesToWatch())
                    .get(rand.nextInt(user.getMoviesToWatch().size()));
            for (User otherUser : userRepository.findByMoviesToWatchContaining(randomUserMovie)) {
                potentialMovies.addAll(otherUser.getMoviesToWatch());
            }
        }

        // find the top ranking valid suggestion
        Optional<Movie> finalSuggestion = potentialMovies.stream()
                        .filter(m -> !isMovieUnsuitable(m, user))
                        .max(Comparator.comparing(m -> scoreMovie(m, user)));

        // get a random suitable movie from a random sample if no suggestion preset
        if (!finalSuggestion.isPresent()) {
            final int SAMPLES = 40;
            potentialMovies = new HashSet<>();
            List<Movie> movies = movieRepository.getAll();
            if (movies.size() == 0) {
                throw new BadRequestException("Error: no movies in database.");
            }
            for (int i = 0; i < SAMPLES; i++) {
                Movie randomMovie = movies.get(rand.nextInt(movies.size()));
                if (isMovieUnsuitable(randomMovie, user)) {
                    continue;
                }
                potentialMovies.add(randomMovie);
            }
            finalSuggestion = potentialMovies.stream()
                    .max(Comparator.comparing(m -> scoreMovie(m, user)));
        }
        // if simply nothing could be found, send an error message
        if (!finalSuggestion.isPresent()) {
            throw new NotFoundException("Could not find a movie.");
        }
        user.suggestMovie(finalSuggestion.get());
        userRepository.save(user);
        return finalSuggestion.get();
    }

    @PreAuthorize("hasAuthority('USER')")
    public void bingeSuggestion() {
        User user = userRepository.getLoggedInUser();
        Optional<Movie> movieOptional = user.getSuggestedMovie();
        movieOptional.ifPresent(movie -> {
            movie.getBinBingeData().userLike();
            user.addMovie(movie);
            user.clearSuggestedMovie();
            userRepository.save(user);
        });
    }

    @PreAuthorize("hasAuthority('USER')")
    public void binSuggestion() {
        User user = userRepository.getLoggedInUser();
        Optional<Movie> movieOptional = user.getSuggestedMovie();
        movieOptional.ifPresent(movie -> {
            movie.getBinBingeData().userDislike();
            user.suggestMovie(null);
            userRepository.save(user);
        });
    }

    private Set<String> getUserGenres() {
        return userRepository.getLoggedInUser()
                .getMoviesToWatch().stream()
                .flatMap(m -> m.getGenres().stream())
                .collect(Collectors.toSet());
    }

    private int scoreMovie(Movie movie, User user) {
        int score = 0;

        // increase score by rating (scale by factor of 50)
        BinBingeData data = movie.getBinBingeData();
        score += data.getRating() * 50;

        // increase score if movie is in user genres
        boolean inGenres = false;
        for (String genre : movie.getGenres()) {
            for (Movie m : user.getMoviesToWatch()) {
                if (m.getGenres().contains(genre)) {
                    score += 1;
                }
            }
        }

        return score;
    }

    private boolean isMovieUnsuitable(Movie movie, User user) {
        return user.getPreviouslySuggestedMovies().contains(movie)
                || user.getMoviesToWatch().contains(movie);
    }
}
