package app.explore.binbinge;

import app.explore.Movie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.BadRequestException;

@RestController
public class BingeBinController {
    @Autowired
    BingeBinService bingeBinService;

    @PostMapping("/api/binbinge")
    public void update(@RequestParam(name = "withUserGenres", defaultValue = "false", required = false) boolean withUserGenres) {
        bingeBinService.createSuggestion();
    }
    @GetMapping("/api/binbinge")
    public Movie get() {
        return bingeBinService.getSuggestion();
    }

    @PostMapping("/api/binbinge/rate")
    public void rate(@RequestParam(name="action") String action) {
        switch (action) {
            case "binge":
                bingeBinService.bingeSuggestion();
                break;
            case "bin":
                bingeBinService.binSuggestion();
                break;
            default:
                throw new BadRequestException("Action must be bin or binge.");
        }
    }
}
