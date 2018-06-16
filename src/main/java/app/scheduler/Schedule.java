package app.scheduler;

import app.explore.Movie;
import app.rss.RssFeed;
import app.scheduler.annealer.Annealable;
import app.user.User;
import util.DateRange;

import javax.persistence.*;
import javax.ws.rs.BadRequestException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Entity
public class Schedule implements Annealable {
    @ManyToMany(cascade = {CascadeType.ALL})
    private final List<MovieTime> movieTimes;
    @OneToOne
    private final User user;

    LocalDateTime creationDate = LocalDateTime.now();

    @OneToOne
    RssFeed rssFeed;

    @Id
    @GeneratedValue
    int id;

    //Test dummy requirements
    @Transient
    public ScheduleRequirements requirements = new ScheduleRequirements(
            Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY),
            Arrays.asList(7, 8, 9, 10, 11, 12)
    );

    public Schedule() {
        user = null;
        movieTimes = null;
    }

    public Schedule(User user) {
        this.user = user;
        this.movieTimes = new ArrayList<>();
    }

    public Schedule(Schedule schedule) {
        this.user = schedule.getUser();
        this.movieTimes = new ArrayList<>(schedule.movieTimes);
        this.requirements = schedule.requirements;
        this.creationDate = schedule.creationDate;
    }

    public List<MovieTime> getMovieTimes() {
        return movieTimes;
    }

    /**
     * Add a movie to the list of scheduled movie watching times
     *
     * @param movie The movie to watch
     * @param date  The day to watch the movie
     */
    public void scheduleMovie(Movie movie, LocalDateTime date) {
        MovieTime movieTime = new MovieTime(movie, date);
        movieTimes.add(movieTime);
    }

    public User getUser() {
        return user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Schedule schedule = (Schedule) o;
        return id == schedule.id &&
                Objects.equals(movieTimes, schedule.movieTimes) &&
                Objects.equals(user, schedule.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(movieTimes, user, id);
    }



    @Override
    public float getScore() {
        int score = 0;

        //discourage longer schedules (count only schedulable days)
        long scheduleLength = countAvailableDays(creationDate, lastMovieTime());
        score -= 10 * scheduleLength;

        //discourage schedules with more days
        score -= numDays();
        return score;
    }

    private long countAvailableDays(LocalDateTime first, LocalDateTime second) {
        return new DateRange(first, second).stream()
                .filter((LocalDateTime d) -> requirements.getWeekdays().contains(d.getDayOfWeek()))
                .count();
    }

    private int numDays() {
        Set<LocalDate> days = new HashSet<>();
        for (MovieTime movieTime : movieTimes) {
            days.add(movieTime.getStart().toLocalDate());
        }
        return days.size();
    }

    @Override
    public Annealable randomNeighbour() {
        Schedule newSchedule = new Schedule(this);

        randomlyMoveMovieTime(newSchedule);
        randomlyBumpMovieTime(newSchedule);
        nothingPersonellMovieTime(newSchedule);

        return newSchedule;
    }

    /**
     * Move a random movie right behind another.
     * @param schedule Schedule to copy and modify
     */
    private void nothingPersonellMovieTime(Schedule schedule) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        MovieTime first = schedule.getMovieTimes().get(random.nextInt(movieTimes.size()));
        int tries = 0;
        while (tries < 100) {
            MovieTime second = schedule.getMovieTimes().get(random.nextInt(movieTimes.size()));
            LocalDateTime newTime = second.getStart().minusMinutes(first.getDuration()).minusMinutes(2);
            MovieTime teleportsBehindU = new MovieTime(first.getMovie(), newTime);
            if (isMovieTimeValid(teleportsBehindU, schedule)) {
                schedule.getMovieTimes().remove(first);
                schedule.getMovieTimes().add(teleportsBehindU);
                return;
            }
            tries += 1;
        }
    }

    /**
     * Move a random movie backwards in time. A maximum of 7 days and 30 minutes.
     * @param schedule Schedule to copy and modify
     */
    private void randomlyBumpMovieTime(Schedule schedule) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        MovieTime randomTime = schedule.getMovieTimes().get(random.nextInt(movieTimes.size()));

        int tries = 0;
        while (tries < 100) {
            int minuteBump = random.nextInt(31);
            int dayBump = random.nextInt(8);
            LocalDateTime newTime = randomTime.getStart().minusMinutes(minuteBump).minusDays(dayBump);

            MovieTime bumpedTime = new MovieTime(randomTime.getMovie(), newTime);
            if (isMovieTimeValid(bumpedTime, schedule)) {
                schedule.getMovieTimes().remove(randomTime);
                schedule.getMovieTimes().add(bumpedTime);
                return;
            }
            tries += 1;
        }
    }

    /**
     * Move a random movie to a random day, anywhere from the schedule start to a week after the oldest
     * movie time.
     * @param schedule Schedule to copy and modify
     */
    private void randomlyMoveMovieTime(Schedule schedule) {
        LocalDateTime minDay = creationDate.plusDays(1); //schedule begins tomorrow
        LocalDateTime maxDay = lastMovieTime().plusDays(7);
        List<LocalDateTime> potentialDatesToMoveMovieTime = new DateRange(minDay, maxDay).toList();

        ThreadLocalRandom random = ThreadLocalRandom.current();
        MovieTime randomMovieTime = schedule.getMovieTimes().get(random.nextInt(movieTimes.size()));

        int tries = 0;
        while (tries < 100) {
            int randomHour = requirements.randomSuitableHour();
            LocalDateTime newDay = potentialDatesToMoveMovieTime.get(random.nextInt(potentialDatesToMoveMovieTime.size()));
            MovieTime movedTime = new MovieTime(randomMovieTime.getMovie(),
                    newDay.toLocalDate().atTime(randomHour, 0));

            if (isMovieTimeValid(movedTime, schedule)) {
                schedule.getMovieTimes().remove(randomMovieTime);
                schedule.getMovieTimes().add(movedTime);
                return;
            }
            tries += 1;
        }
    }

    private boolean isMovieTimeValid(MovieTime movieTime, Schedule schedule) {
        return !isMovieTimeOverlap(movieTime, schedule) &&
                requirements.getWeekdays().contains(movieTime.getStart().getDayOfWeek()) &&
                requirements.getHours().contains(movieTime.getStart().getHour()) &&
                requirements.getHours().contains(movieTime.getEnd().getHour()) &&
                creationDate.plusDays(1).isBefore(movieTime.getStart());
    }

    private boolean isMovieTimeOverlap(MovieTime movieTime, Schedule schedule) {
        for (MovieTime m : schedule.getMovieTimes()) {
            if (movieTime.isOverlapping(m) && !movieTime.equals(m)) {
                return true;
            }
        }
        return false;
    }

    private LocalDateTime lastMovieTime() {
        return movieTimes.stream()
                .map(MovieTime::getEnd)
                .max(LocalDateTime::compareTo)
                .orElseThrow(() -> new BadRequestException("could not get last movie time"));
    }

    public void setRequirements(ScheduleRequirements requirements) {
        this.requirements = requirements;
    }
}
