package app.explore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExploreController {
    @Autowired
    ExploreService exploreService;



    @GetMapping("/api/movie/{id}")
    public Movie movie(@PathVariable("id") int id) {
        return exploreService.getMovie(id);
    }

    @GetMapping("/api/movie/{id}/poster")
    public PosterResponse posterUrl(@PathVariable("id") int id) {
        return new PosterResponse(exploreService.getPoster(id));
    }
}
