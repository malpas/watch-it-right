package app.rss;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import util.Message;

import javax.ws.rs.core.MediaType;

@RestController
public class RssController {
    @Autowired
    RssService rssService;

    WordIDGenerator wordIDGenerator = new WordIDGenerator();

    @GetMapping(value = "/rss/{name}", produces = MediaType.APPLICATION_XML)
    public RssFeed get(@PathVariable String name) {
        return rssService.getFeed(name);
    }

    @PostMapping("/api/rss/")
    public Message createFeed() {
        rssService.createUserFeed();
        return new Message("Success");
    }

    @GetMapping("/api/user/rss")
    public Message getUserFeedName() {
        return new Message(rssService.getUserFeedName());
    }
}
