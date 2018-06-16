package app.user;

import app.authorization.UserDetailsServiceImpl;
import app.explore.Movie;
import app.explore.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.ws.rs.BadRequestException;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    MovieRepository movieRepository;

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    PasswordEncoder passwordEncoder;

    /**
     * Get the public user description of the logged in user.
     *
     * @return the public user description of the logged in user
     */
    @PreAuthorize("hasAuthority('USER')")
    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        Optional<User> user = userRepository.findFirstByUsername(username);
        return user.get();
    }

    @PreAuthorize("hasAuthority('USER')")
    public void addMovie(int id) {
        User user = userRepository.getLoggedInUser();
        Optional<Movie> movie = movieRepository.getMovieById(id);
        movie.ifPresent(m -> {
            user.addMovie(m);
            userRepository.save(user);
        });
    }

    @PreAuthorize("hasAuthority('USER')")
    public void removeMovie(int movieId) {
        User user = userRepository.getLoggedInUser();
        Optional<Movie> movie = movieRepository.getMovieById(movieId);
        movie.ifPresent(m -> {
            user.removeMovie(m);
            userRepository.save(user);
        });
    }

    void registerUser(String username, String password)
            throws BadRequestException{
        if (userRepository.findFirstByUsername(username).isPresent()) {
            throw new BadRequestException("Username already exists.");
        }
        validateUsernameAndPassword(username, password);

        userRepository.save(new User(username, passwordEncoder.encode(password)));
    }

    private void validateUsernameAndPassword(String username, String password)
            throws BadRequestException {
        boolean userAlphanumeric = true;
        for (char c : username.toCharArray()) {
            if (!Character.isLetter(c) && !Character.isDigit(c)) {
                userAlphanumeric = false;
            }
        }
        if (!userAlphanumeric) {
            throw new BadRequestException("Username must be alphanumeric.");
        }
        else if (username.length() <= 3) {
            throw new BadRequestException("Username must be longer than 3 letters.");
        }

        if (password.length() <= 3) {
            throw new BadRequestException("Password must be at least 5 characters long.");
        }
    }

    @PreAuthorize("hasAuthority('USER')")
    public void changePassword(String verificationPassword, String newPassword) {
        User currentUser = userRepository.getLoggedInUser();
        if (!passwordEncoder.matches(verificationPassword, currentUser.getPassword())) {
            throw new BadRequestException("Wrong password.");
        }
        currentUser.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(currentUser);
    }

    @PreAuthorize("hasAuthority('USER')")
    public void clearWatchList() {
        User loggedInUser = userRepository.getLoggedInUser();
        loggedInUser.clearMoviesToWatch();
        userRepository.save(loggedInUser);
    }
}
