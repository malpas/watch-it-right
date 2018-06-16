package app;

import app.admin.AdminService;
import app.explore.MovieRepository;
import app.user.User;
import app.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.nio.file.Paths;

/**
 * Runs on server start. Inserts dummy data to the database when --testData is added as a
 * command-line argument.
 */
@Component
public class TestDataLoader implements ApplicationRunner {
    @Autowired
    AdminService adminService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    MovieRepository movieRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (!args.getOptionNames().contains("testData")) {
            return;
        }
        System.out.println("app/TestDataLoader populating database");

        // create test user
        userRepository.save(new User("john", encoder.encode("john")));

        // upload test data (test_movies.csv)
        try {
            adminService.upload("title", "release_date",
                    "runtime", "overview", "genres",
                    "yyyy-MM-dd",
                    new FileInputStream(Paths.get("test_movies.csv").toFile())
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("app/TestDataLoader done");

    }
}

