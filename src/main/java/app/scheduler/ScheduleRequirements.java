package app.scheduler;

import java.time.DayOfWeek;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ScheduleRequirements {
    int id;

    // days available to bingewatch
    final List<DayOfWeek> weekdays;

    // hours available to bingewatch (military time)
    final List<Integer> hours;

    ScheduleRequirements() {
        weekdays = Collections.emptyList();
        hours = Collections.emptyList();
    }

    public ScheduleRequirements(List<DayOfWeek> days, List<Integer> hours) {
        this.weekdays = days;
        this.hours = hours;
    }

    public List<DayOfWeek> getWeekdays() {
        return weekdays;
    }
    public List<Integer> getHours() {
        return hours;
    }

    public int randomSuitableHour() {
        return hours.get(ThreadLocalRandom.current().nextInt(hours.size()));
    }
}
