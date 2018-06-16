package app.admin;

import app.explore.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class AdminController {
    @Autowired
    AdminService adminService;

    @Autowired
    MovieRepository movieRepository;

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ModelAndView adminDashboard() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("admin");
        modelAndView.addObject("movieCount", adminService.getMovieCount());
        return modelAndView;
    }

    @PostMapping("/admin/upload")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<String> handleFileUpload(@RequestParam(name = "titleHeader") String titleHeader,
                                         @RequestParam(name = "yearHeader") String yearHeader,
                                         @RequestParam(name = "runtimeHeader") String runtimeHeader,
                                         @RequestParam(name = "descriptionHeader") String descriptionHeader,
                                         @RequestParam(name = "genreHeader") String genreHeader,
                                         @RequestParam(name = "dateFormat") String dateFormat,
                                         @RequestPart(name = "file") MultipartFile file) {

        List<String> errors = new ArrayList<>();
        try {
            errors = adminService.upload(titleHeader, yearHeader,
                    runtimeHeader, descriptionHeader, genreHeader, dateFormat,
                    file.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return errors;
    }
}
