package app.scheduler;

import app.explore.Movie;
import app.scheduler.annealer.SimulatedAnnealer;
import app.user.User;

import java.time.LocalDate;
import java.util.Set;

class ScheduleBuilder {
    //TODO ADD PARAMETERS

    public static Schedule build(User user, Set<Movie> movies, LocalDate
            startingDate, ScheduleRequirements requirements) {
        int sizeFactor = movies.size() < 5 ? 1 : 2;
        int seconds = sizeFactor * movies.size();
        return (Schedule) SimulatedAnnealer.solve(initial(user, movies, startingDate, requirements), seconds);
    }

    private static Schedule initial(User user, Set<Movie> movies, LocalDate
            startingDate, ScheduleRequirements requirements) {
        Schedule schedule = new Schedule(user);
        schedule.setRequirements(requirements);
        LocalDate startDay = startingDate.plusDays(1);
        for (Movie movie : movies) {
            while (!schedule.requirements.getWeekdays().contains(startDay.getDayOfWeek())) {
                startDay = startDay.plusDays(1);
            }
            schedule.scheduleMovie(movie, startDay.atTime(schedule.requirements.getHours().get(0), 0));
            startDay = startDay.plusDays(1);
        }
        return schedule;
    }

}
