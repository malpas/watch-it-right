package app.explore.search;

import app.explore.Movie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SearchController {
    @Autowired
    SearchService searchService;

    @GetMapping("api/search")
    List<Movie> search(@RequestParam("title") String title) {
        return searchService.search(title, 10);
    }
}
