package app.rss;

import app.scheduler.Schedule;
import app.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import javax.persistence.*;

/**
 * Public RSS feed. Includes the RSS tag with a version, as well as the channel that actually
 * contains the feed data.
 */
@JacksonXmlRootElement(localName = "rss")
@Entity
public class RssFeed {
    @JacksonXmlProperty(isAttribute = true)
    public final String version = "2.0";
    private final String name;
    @OneToOne
    private User user;

    @Id
    @GeneratedValue
    int id;
    @OneToOne(mappedBy = "rssFeed", cascade = CascadeType.ALL)
    private Schedule schedule;

    public RssFeed() {
        this.name = null;
    }

    public RssFeed(String name, User user, Schedule schedule) {
        this.name = name;
        this.schedule = schedule;
        this.user = user;
    }

    @JsonIgnore
    public String getName() {
        return name;
    }

    @JsonIgnore
    public User getUser() {
        return user;
    }

    public RssChannel getChannel() {
        return new RssChannel(name, schedule);
    }

    @JsonIgnore
    public Schedule getSchedule() {
        return schedule;
    }
}
