package app.rss;

import app.scheduler.Schedule;
import app.user.User;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.Optional;

@Transactional
public interface RssFeedRepository extends CrudRepository<RssFeed, Integer> {
    Optional<RssFeed> findFirstByUser(User user);

    Optional<RssFeed> findFirstByName(String name);

    void deleteAllByUser(User user);

    void deleteAllBySchedule(Schedule schedule);
}
