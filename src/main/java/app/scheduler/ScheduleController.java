package app.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import util.Message;

import java.util.List;
import java.util.Optional;

@RestController
public class ScheduleController {
    @Autowired
    ScheduleService scheduleService;

    @GetMapping(value = "/api/user/schedule", produces = MediaType.APPLICATION_JSON_VALUE)
    List<MovieTime> getSchedule() {
        Optional<Schedule> scheduleOptional = scheduleService.getScheduleForUser();
        return scheduleOptional.map(Schedule::getMovieTimes).orElse(null);
    }

    @PutMapping("/api/user/schedule")
    Message createSchedule(@RequestBody ScheduleRequirements requirements) {
        return scheduleService.createScheduleForUser(requirements);
    }

    @DeleteMapping("/api/user/schedule")
    String clearSchedule() {
        scheduleService.clearScheduleForUser();
        return "";
    }
}
