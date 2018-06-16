package app.rss;

import app.scheduler.MovieTime;

import java.time.LocalDateTime;

class RssItem {
    public LocalDateTime pubDate;
    private String title;
    private String link;
    private String description;

    //TODO ADD GUID FOR COMPATIBILITY
    RssItem() {
        this.pubDate = null;
        title = null;
        link = null;
        description = null;
    }

    RssItem(MovieTime movieTime, String link, RssChannel parent) {
        this.title = movieTime.getTitle();
        this.description = generateDescription(movieTime);
        this.pubDate = movieTime.getStart();
        this.link = link;
    }

    static RssItem noneFound() {
        RssItem item = new RssItem();
        item.title = "Nothing upcoming!";
        item.description = "You've got no movies upcoming. Take it easy.";
        item.link = "/";
        item.pubDate = LocalDateTime.now();
        return item;
    }

    private String generateDescription(MovieTime movieTime) {
        return movieTime.getMovie().getDescription();
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getDescription() {
        return description;
    }

    public String getPubDate() {
        return pubDate.toString();
    }
}
