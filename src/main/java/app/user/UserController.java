package app.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import util.Message;

@RestController(value = "/user")
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping("/api/user")
    User user() {
        return userService.getCurrentUser();
    }

    @GetMapping("/api/login")
    public ModelAndView login() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        return modelAndView;
    }


    @PostMapping("/api/register")
    Message register(@ModelAttribute RegistrationForm form) {
        userService.registerUser(
                form.getUsername(),
                form.getPassword());

        return new Message("Success");
    }

    @PostMapping("api/user/password")
    Message changePassword(@RequestParam("old") String oldPassword, @RequestParam("new") String newPassword) {
        userService.changePassword(oldPassword, newPassword);
        return new Message("Success");
    }

    @DeleteMapping("/api/user/watchList/{id}")
    Message removeMovie(@PathVariable(name = "id") int id) {
        userService.removeMovie(id);
        return new Message("Success");
    }

    @PostMapping("/api/user/watchList/{id}")
    Message addMovie(@PathVariable(name = "id") int id) {
        userService.addMovie(id);
        return new Message("Success");
    }

    @DeleteMapping("/api/user/watchList")
    Message clearWatchList() {
        userService.clearWatchList();
        return new Message("Cleared watch list");
    }
}
