package app.user;

import app.config.ApplicationConfig;
import app.explore.Movie;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.transaction.Transactional;
import javax.ws.rs.NotFoundException;
import java.util.List;
import java.util.Optional;

@Transactional
public interface UserRepository extends CrudRepository<User, Integer> {
    Optional<User> findFirstByUsername(String username);

    List<User> findByMoviesToWatchContaining(Movie m);

    default User getLoggedInUser() {
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
        if (username.equals(ApplicationConfig.getAdminUsername())) {
            throw new NotFoundException("Error. Admin is not a real user.");
        }
        return findFirstByUsername(username).orElseThrow(() -> new NotFoundException("Invalid user."));
    }
}
