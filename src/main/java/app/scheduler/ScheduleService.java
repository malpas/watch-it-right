package app.scheduler;

import app.rss.RssFeedRepository;
import app.user.User;
import app.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import util.Message;

import javax.ws.rs.BadRequestException;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class ScheduleService {
    @Autowired
    ScheduleRepository scheduleRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RssFeedRepository feedRepository;

    @PreAuthorize("hasAuthority('USER')")
    public Optional<Schedule> getScheduleForUser() {
        User user = userRepository.getLoggedInUser();
        return scheduleRepository.findFirstByUser(user);
    }

    /**
     * Creates the user schedule for the logged in user, deleting any
     * pre-existing one.
     *
     * @return A message containing "Success" or "Failure"
     * @param requirements
     */
    @PreAuthorize("hasAuthority('USER')")
    public Message createScheduleForUser(ScheduleRequirements requirements) {
        User user = userRepository.getLoggedInUser();
        if (user.getMoviesToWatch().size() == 0) {
            throw new BadRequestException("No movies.");
        }
        Schedule schedule = ScheduleBuilder.build(user, user.getMoviesToWatch(),
                LocalDate.now(), requirements);
        scheduleRepository.save(schedule);
        return new Message("Success");
    }

    /**
     * Deletes the logged in user's schedule, and any RSS channels with it.
     */
    @PreAuthorize("hasAuthority('USER')")
    public void clearScheduleForUser() {
        User  user = userRepository.getLoggedInUser();
        scheduleRepository.deleteAllByUser(user);
    }
}
