package app.rss;

import app.scheduler.Schedule;
import app.scheduler.ScheduleRepository;
import app.user.User;
import app.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import java.util.Optional;

@Service
public class RssService {
    private final WordIDGenerator wordIDGenerator = new WordIDGenerator();
    @Autowired
    UserRepository userRepository;
    @Autowired
    ScheduleRepository scheduleRepository;
    @Autowired
    RssFeedRepository feedRepository;

    public RssFeed getFeed(String feedName) {
        Optional<RssFeed> feed = feedRepository.findFirstByName(feedName);
        if (feed.isPresent()) {
            User user = feed.get().getUser();
            Optional<Schedule> schedule = scheduleRepository.findFirstByUser(user);
            if (schedule.isPresent()) {
                return new RssFeed(feedName, user, schedule.get());
            }
        }

        throw new BadRequestException("no schedule found");
    }

    public String getUserFeedName() {
        User loggedInUser = userRepository.getLoggedInUser();
        Optional<RssFeed> feed = feedRepository.findFirstByUser(loggedInUser);
        if (!feed.isPresent()) {
            createUserFeed();
            feed = feedRepository.findFirstByUser(loggedInUser);
            return feed.get().getName();
        } else {
            return feed.get().getName();
        }
    }

    public void createUserFeed() {
        User loggedInUser = userRepository.getLoggedInUser();
        Optional<Schedule> scheduleOptional = scheduleRepository.findFirstByUser(loggedInUser);
        if (!scheduleOptional.isPresent()) {
            throw new NotFoundException("No schedule to create feed from.");
        }
        if (feedRepository.findFirstByUser(loggedInUser).isPresent()) {
            throw new BadRequestException("User channel already exists.");
        }
        scheduleOptional.ifPresent(schedule -> {
            RssFeed newFeed = new RssFeed(wordIDGenerator.newID(), loggedInUser, schedule);
            feedRepository.save(newFeed);
        });
    }
}
