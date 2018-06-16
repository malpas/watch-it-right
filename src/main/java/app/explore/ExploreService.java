package app.explore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.BadRequestException;

@Service
public class ExploreService {
    @Autowired
    MovieApiRepository movieApiRepository;

    @Autowired
    MovieRepository movieRepository;

    public String getPoster(int id) {
        Movie movie = getMovie(id);
        return movieApiRepository.getPoster(movie);
    }

    /**
     * Get a movie by its ID from the movie repository
     * @param id Id of movie to find
     * @return Movie with id
     * @throws BadRequestException if no movie found
     */
    public Movie getMovie(int id) {
        return movieRepository.getMovieById(id).orElseThrow(() -> new BadRequestException("movie not found"));
    }

}
