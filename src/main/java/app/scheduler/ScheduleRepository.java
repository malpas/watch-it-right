package app.scheduler;

import app.user.User;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.Optional;

@Transactional
public interface ScheduleRepository extends CrudRepository<Schedule, Integer> {
    Optional<Schedule> findFirstByUser(User user);

    void deleteAllByUser(User user);
}
