package app.home;

import app.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @Autowired
    UserRepository userRepository;

    @GetMapping("/")
    String index() {
        return "forward:/index.html";
    }
}
