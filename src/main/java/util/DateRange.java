package util;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

/** SOURCE: https://stackoverflow.com/questions/38220543/java-8-localdate-how-do-i-get-all-dates-between-two-dates */
public class DateRange implements Iterable<LocalDateTime> {

    private final LocalDateTime startDate;
    private final LocalDateTime endDate;

    public DateRange(LocalDateTime startDate, LocalDateTime endDate) {
        //check that range is valid (null, start < end)
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public Iterator<LocalDateTime> iterator() {
        return stream().iterator();
    }

    public Stream<LocalDateTime> stream() {
        return Stream.iterate(startDate, d -> d.plusDays(1))
                .limit(ChronoUnit.DAYS.between(startDate, endDate) + 1);
    }

    public List<LocalDateTime> toList() { //could also be built from the stream() method
        List<LocalDateTime> dates = new ArrayList<>();
        for (LocalDateTime d = startDate; !d.isAfter(endDate); d = d.plusDays(1)) {
            dates.add(d);
        }
        return dates;
    }
}