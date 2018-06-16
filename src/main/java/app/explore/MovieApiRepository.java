package app.explore;

import app.config.ApplicationConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Repository
@Transactional
public class MovieApiRepository {
    @Autowired
    MovieRepository movieRepository;

    //Key to access TMDB API
    private final String apiKey = ApplicationConfig.getTmdbApiKey();

    /**
     * Get the poster url for a movie from TMDB, given its title and release year
     * @param movie Movie to get poster of
     * @return The API data returned from the first result
     */
    public String getPoster(Movie movie) {
        final String API_TARGET = "https://api.themoviedb.org/3";

        if (movie.getPosterUrl() != null) {
            return movie.getPosterUrl();
        }

        String queryPath;
        try {
            queryPath = String.format("/search/movie?api_key=%s" +
                            "&language=en-US&query=%s&page=1&include_adult=false&year" +
                            "=%d", apiKey, URLEncoder.encode(movie.getTitle(), "UTF-8"),
                            movie.getYear());
        } catch (UnsupportedEncodingException e) {
            return null;
        }

        String response = ClientBuilder.newClient()
                .target(API_TARGET + queryPath)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get(String.class);
        try {
            JsonNode jsonNode = new ObjectMapper().readTree(response);
            if (jsonNode.get("total_results").asInt() != 0) {
                JsonNode result = jsonNode.get("results").elements().next();
                String posterUrl = String.format("https://image.tmdb" +
                        ".org/t/p/w600_and_h900_bestv2%s",
                        result.get("poster_path").asText());
                movie.setPosterUrl(posterUrl);
                movieRepository.save(movie);
                return posterUrl;
            }

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }
}
