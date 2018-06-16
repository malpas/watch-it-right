package app.explore.search;

import app.explore.Movie;
import app.explore.MovieApiRepository;
import app.explore.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.NotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.reverseOrder;

@Service
public class SearchService {
    @Autowired
    MovieRepository movieRepository;

    @Autowired
    MovieApiRepository movieApiRepository;

    /**
     * SearchQuery for a movie (using fuzzy string matching)
     * @param text Title of movie to search for
     * @param numItems Number of items
     * @return A list of potential movies found
     */
    public List<Movie> search(String text, int numItems) {
        Map<Movie, Integer> moviesWithScores = new HashMap<>();
        List<Movie> movies = movieRepository.getAll();
        if (movies.size() == 0) {
            throw new NotFoundException("No movies to be found.");
        }

        SearchQuery query = SearchQuery.parseFrom(text);

        // score each movie in the database
        for (Movie movie : movies) {
            int searchScore = fuzzyStringMatch(query.getText(), movie.getTitle());
            // filter out incorrect years
            int beforeYear = query.getBeforeYear();
            int afterYear = query.getAfterYear();
            if (beforeYear != -1 && movie.getYear() > beforeYear) {
                continue;
            }
            if (afterYear != -1 && movie.getYear() < afterYear) {
                continue;
            }
            // strongly encourage movies with queried genres
            if (query.getGenres().size() > 0) {
                List<String> movieLowerCaseGenres =  movie.getGenres()
                        .stream()
                        .map(String::toLowerCase)
                        .collect(Collectors.toList());
                List<String> queryLowerCaseGenres = query.getGenres()
                        .stream()
                        .map(String::toLowerCase)
                        .collect(Collectors.toList());

                if (!movieLowerCaseGenres.containsAll(queryLowerCaseGenres)) {
                    continue;
                }
            }
            moviesWithScores.put(movie, searchScore);
        }
        // return the numItems top-scoring movies
        return sortMoviesByScore(moviesWithScores)
                .limit(numItems)
                .peek(movie -> movieRepository.getMovieById(movie.getId())) //update with API
                .collect(Collectors.toList());
    }

    private Stream<Movie> sortMoviesByScore(Map<Movie, Integer> moviesWithScores) {
        return moviesWithScores.entrySet().stream()
                .sorted(reverseOrder(Map.Entry.comparingByValue()))
                .map(Map.Entry::getKey);
    }

    /**
     * Scores a query string in comparison to another string.
     * Inspired by Sublime Text's fuzzy string matching. Uses the criteria suggested
     * in: https://blog.forrestthewoods.com/reverse-engineering-sublime-text-s-fuzzy-match-4cffeed33fdb
     * @param pattern Pattern to search with
     * @param string String to match
     * @return
     */
    private int fuzzyStringMatch(String pattern, String string) {
        int score = 0;
        int patternIndex = 0;
        int stringIndex = 0;
        boolean consecutiveMatch = false;
        int leadingLetterPenalty = 0;
        // continue until either the pattern has been matched or the string has been iterated over
        while (patternIndex != pattern.length() && stringIndex != string.length()) {
            char patternChar = pattern.charAt(patternIndex);
            char stringChar = string.charAt(stringIndex);
            // adjust score depending on a match
            if (Character.toLowerCase(patternChar) == Character.toLowerCase(stringChar)) {
                //matching an upper case letter or whitespace is worth 10 score
                if (Character.isUpperCase(stringChar) || Character.isWhitespace(stringChar)) {
                    score += 10;
                }
                //add 5 score on a consecutive match, else 0
                score += consecutiveMatch ? 5 : 0;
                patternIndex += 1;
                consecutiveMatch = true;
            } else {
                // no match worth -1
                score -= 1;
                //failing to match the leading letter is worth -3 score (max -9)
                if (stringIndex >= 1 && Character.isWhitespace(string.charAt(stringIndex - 1)) && leadingLetterPenalty < 9) {
                    score -= 3;
                    leadingLetterPenalty += 3;
                }
                consecutiveMatch = false;
            }
            stringIndex += 1;
        }
        // halve score if pattern was not fully matched
        return patternIndex == pattern.length() ? score : score / 2;
    }


}
