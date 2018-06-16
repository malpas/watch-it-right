package app.rss;

import app.scheduler.MovieTime;
import app.scheduler.Schedule;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The channel of an RSS feed. Contains all the links (An RssFeed only holds the channel).
 */
@JacksonXmlRootElement(localName = "channel")
@JsonSerialize(using = RssChannelSerializer.class)
class RssChannel {
    public final String title = "Watch It Right";

    public final String link;

    public final List<RssItem> items = new ArrayList<>();
    public final String description;

    RssChannel() {
        link = null;
        description = null;
    }

    RssChannel(String name, Schedule schedule) {
        final String BASE_URL = "https://watchitright.uqcloud.net/rss/";
        List<MovieTime> movieTimes = schedule.getMovieTimes();
        description = String.format(
                "%s's WatchItRight schedule. You've watched %d/%d movies.",
                schedule.getUser().getUsername(),
                pastMovieTimes(movieTimes).size(),
                movieTimes.size());
        link = BASE_URL + name;

        // add upcoming movies to the RSS feed
        for (MovieTime movieTime : movieTimes) {
            if (!isUpcoming(movieTime)) {
                continue;
            }
            RssItem item = new RssItem(movieTime,
                    BASE_URL + "movie/" + movieTime.getMovie().getId(),
                    this);
            items.add(item);
        }

        // if there are no movies upcoming, create a descriptive item
        if (items.size() == 0) {
            items.add(RssItem.noneFound());
        }
    }

    /**
     * Check if a MovieTime is scheduled between today and three days into the future. This
     * ensures that it is relevant to the RSS feed.
     *
     * @param movieTime MovieTime to check
     * @return Whether movie is scheduled in the future but fewer than five days later
     */
    private boolean isUpcoming(MovieTime movieTime) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime scheduledDate = movieTime.getStart();
        return scheduledDate.isAfter(now) && scheduledDate.isBefore(now.plusDays(3));
    }

    /**
     * Get movie times scheduled in the past
     * @param movieTimes A list of MovieTimes to filter
     * @return The MovieTimes that are scheduled in the past
     */
    private List<MovieTime> pastMovieTimes(List<MovieTime> movieTimes) {
        LocalDateTime now = LocalDateTime.now();
        return movieTimes.stream()
                .filter(m -> m.getStart().isBefore(now))
                .collect(Collectors.toList());
    }
}
