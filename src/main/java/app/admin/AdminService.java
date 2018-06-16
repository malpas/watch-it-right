package app.admin;

import app.admin.csv.CSVReader;
import app.admin.csv.HeaderNotFoundException;
import app.explore.Movie;
import app.explore.MovieApiRepository;
import app.explore.MovieRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class AdminService {
    @Autowired
    MovieRepository movieRepository;

    @Autowired
    MovieApiRepository movieApiRepository;

    /**
     * Upload movie data to the database with a CSV input, given the header names
     * and date format to use.
     * @param titleHeader Header name for movie title
     * @param yearHeader Header name for year
     * @param runtimeHeader Header name for runtime
     * @param descriptionHeader Header name for movie description/overview
     * @param dateFormat Data format
     * @param input CSV input to parse
     * @return The errors as a string list
     * @throws IOException If input cannot be read
     * @throws IllegalArgumentException If a given header name is wrong
     */
    public List<String> upload(String titleHeader,
                       String yearHeader,
                       String runtimeHeader,
                       String descriptionHeader,
                       String genreHeader,
                       String dateFormat,
                       InputStream input) throws IOException {
        CSVReader reader;
        try {
            reader = CSVReader.fromStream(input);
        } catch (HeaderNotFoundException e) {
            throw new BadRequestException("invalid file (no header)");
        }
        DateTimeFormatter format = DateTimeFormatter.ofPattern(dateFormat);
        List<String> errors = new ArrayList<>();
        List<Movie> toAdd = new ArrayList<>();
        AtomicInteger lineNum = new AtomicInteger(0);
        reader.stream().forEach(line ->
            {
                try {
                    String title = line.get(titleHeader);
                    String description = line.get(descriptionHeader);
                    List<String> genres = extractGenres(line.get(genreHeader), errors, lineNum.get());
                    int runtime = Integer.parseInt(line.get(runtimeHeader));
                    LocalDate date = LocalDate.parse(line.get(yearHeader), format);
                    int year = date.getYear();
                    Movie movie = new Movie(title, description,
                            year, runtime);
                    movie.setGenres(genres);
                    toAdd.add(movie);
                } catch (NotFoundException | NumberFormatException | DateTimeParseException e) {
                    errors.add(e.toString() + "\n");
                }
                lineNum.incrementAndGet();
            });
        movieRepository.save(toAdd);
        return errors;
    }

    int getMovieCount() {
        return movieRepository.getAll().size();
    }

    private List<String> extractGenres(String genreJson, List<String> errors, int lineNum) {
        List<String> genres = new ArrayList<>();
        try {
            JsonNode genreJsonNode = new ObjectMapper().readTree(genreJson);
            genreJsonNode.elements().forEachRemaining(node -> {
                String genreName = node.get("name").asText();
                genres.add(genreName);
            });
        } catch (IOException e) {
            errors.add(String.format("Line %d: %s", lineNum, e.toString()));
        }
        return genres;
    }
}
