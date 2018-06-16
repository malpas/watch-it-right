package app.explore;

import org.hibernate.Hibernate;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Movie repository. Uses the entity manager directly over a CrudRepository
 * so it can be directly cached
 */
@Repository
@Transactional
public class MovieRepository {
    @PersistenceContext
    private
    EntityManager entityManager;


    public Optional<Movie> getMovieById(int id) {
        TypedQuery<Movie> query = entityManager.createQuery("SELECT m " +
                "from Movie m WHERE m.id = :id", Movie.class);
        query.setParameter("id", id);
        query.setMaxResults(1);
        return query.getResultList()
                .stream()
                .peek(movie -> Hibernate.initialize(movie.getGenres())) //initialize movies to prevent laziness errors
                .findFirst();
    }

    /**
     * Get all of the movies from the database. Does not update the movies using the API.
     * The movies are cached.
     * @return All movies in the database
     */
    @Cacheable("movieList")
    public List<Movie> getAll() {
        TypedQuery<Movie> query = entityManager.createQuery("SELECT m from " +
                        "Movie m", Movie.class);

        List<Movie> results = query.getResultList();
        return results.stream()
                .peek(movie -> Hibernate.initialize(movie.getGenres())) //initialize genre list to prevent laziness errors
                .collect(Collectors.toList());
    }

    public void save(List<Movie> movies) {
        movies.forEach(entityManager::persist);
    }

    void save(Movie m) {
        entityManager.persist(m);
    }
}
